package radon.jujutsu_kaisen.client.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.RequestVisualDataC2SPacket;

import java.util.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientVisualHandler {
    private static final RenderType SIX_EYES = JJKRenderTypes.eyes(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/overlay/six_eyes.png"));
    private static final RenderType INUMAKI = RenderType.entityCutoutNoCull(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/overlay/inumaki.png"));

    private static final Map<UUID, VisualData> synced = new HashMap<>();

    public static void receive(UUID identifier, VisualData data) {
        synced.put(identifier, data);
    }

    public static boolean isSynced(UUID identifier) {
        return synced.containsKey(identifier);
    }

    public static VisualData getData(UUID identifier) {
        return synced.get(identifier);
    }

    @Nullable
    public static VisualData getOrRequest(LivingEntity entity) {
        if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return null;

        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return null;

        if (synced.containsKey(entity.getUUID())) {
            PacketHandler.sendToServer(new RequestVisualDataC2SPacket(synced.get(entity.getUUID()).serializeNBT(), entity.getUUID()));
            return synced.get(entity.getUUID());
        } else if (entity == mc.player) {
            if (mc.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                ISorcererData cap = mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                Set<CursedTechnique> techniques = new HashSet<>();

                if (cap.getTechnique() != null) techniques.add(cap.getTechnique());
                if (cap.getCurrentCopied() != null) techniques.add(cap.getCurrentCopied());
                if (cap.getCurrentAbsorbed() != null) techniques.add(cap.getCurrentAbsorbed());
                if (cap.getAdditional() != null) techniques.add(cap.getAdditional());

                VisualData data = new VisualData(cap.getToggled(), cap.getTraits(), techniques, cap.getType());
                return synced.put(mc.player.getUUID(), data);
            }
        } else {
            if (entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                PacketHandler.sendToServer(new RequestVisualDataC2SPacket(new CompoundTag(), entity.getUUID()));
            }
        }
        return null;
    }

    public static void render(EntityModel<?> model, PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity entity) {
        if (synced.containsKey(entity.getUUID())) {
            VisualData data = synced.get(entity.getUUID());

            if (data.traits.contains(Trait.SIX_EYES)) {
                VertexConsumer consumer = buffer.getBuffer(SIX_EYES);
                model.renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                        1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (data.techniques.contains(CursedTechnique.CURSED_SPEECH)) {
                VertexConsumer consumer = buffer.getBuffer(INUMAKI);
                model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                        1.0F, 1.0F, 1.0F, 1.0F);
            }
        } else {
            if (entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                PacketHandler.sendToServer(new RequestVisualDataC2SPacket(new CompoundTag(), entity.getUUID()));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityRemoved(EntityLeaveLevelEvent event) {
        synced.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        VisualData data = getOrRequest(entity);

        if (data == null) return;

        BlueFistsVisual.tick(data, entity);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) return;

        VisualData data = getOrRequest(player);

        if (data == null) return;

        BlueFistsVisual.tick(data, player);
    }

    public record VisualData(Set<Ability> toggled, Set<Trait> traits, Set<CursedTechnique> techniques, JujutsuType type) {
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();

            ListTag toggledTag = new ListTag();

            for (Ability ability : this.toggled) {
                toggledTag.add(StringTag.valueOf(JJKAbilities.getKey(ability).toString()));
            }
            nbt.put("toggled", toggledTag);

            ListTag traitsTag = new ListTag();

            for (Trait trait : this.traits) {
                traitsTag.add(IntTag.valueOf(trait.ordinal()));
            }
            nbt.put("traits", traitsTag);

            ListTag techniquesTag = new ListTag();

            for (CursedTechnique technique : this.techniques) {
                techniquesTag.add(IntTag.valueOf(technique.ordinal()));
            }
            nbt.put("techniques", techniquesTag);

            nbt.putInt("type", this.type.ordinal());

            return nbt;
        }

        public static VisualData deserializeNBT(CompoundTag nbt) {
            Set<Ability> toggled = new HashSet<>();

            for (Tag key : nbt.getList("toggled", Tag.TAG_STRING)) {
                toggled.add(JJKAbilities.getValue(new ResourceLocation(key.getAsString())));
            }

            Set<Trait> traits = new HashSet<>();

            for (Tag key : nbt.getList("traits", Tag.TAG_INT)) {
                if (key instanceof IntTag tag) {
                    traits.add(Trait.values()[tag.getAsInt()]);
                }
            }

            Set<CursedTechnique> techniques = new HashSet<>();

            for (Tag key : nbt.getList("techniques", Tag.TAG_INT)) {
                if (key instanceof IntTag tag) {
                    techniques.add(CursedTechnique.values()[tag.getAsInt()]);
                }
            }
            return new VisualData(toggled, traits, techniques, JujutsuType.values()[nbt.getInt("type")]);
        }
    }
}
