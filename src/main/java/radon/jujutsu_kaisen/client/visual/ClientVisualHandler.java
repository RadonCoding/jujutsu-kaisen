package radon.jujutsu_kaisen.client.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
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
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.RequestVisualDataC2SPacket;

import java.util.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientVisualHandler {
    private static final RenderType SIX_EYES = RenderType.eyes(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/overlay/six_eyes.png"));
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

        if (mc.player == null) return null;

        if (synced.containsKey(entity.getUUID())) {
            return synced.get(entity.getUUID());
        } else if (entity == mc.player) {
            if (mc.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                ISorcererData cap = mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                VisualData data = new VisualData(cap.getToggled(), cap.getTraits(), cap.getType(), cap.getTechnique());
                return synced.put(mc.player.getUUID(), data);
            }
        } else {
            if (entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                PacketHandler.sendToServer(new RequestVisualDataC2SPacket(entity.getUUID()));
            }
        }
        return null;
    }

    public static void render(EntityModel<?> model, PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity entity) {
        if (synced.containsKey(entity.getUUID())) {
            VisualData data = synced.get(entity.getUUID());

            if (data.traits.contains(Trait.SIX_EYES)) {
                VertexConsumer consumer = buffer.getBuffer(SIX_EYES);
                model.renderToBuffer(poseStack, consumer, 15728640, OverlayTexture.NO_OVERLAY,
                        1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (data.technique == CursedTechnique.CURSED_SPEECH) {
                VertexConsumer consumer = buffer.getBuffer(INUMAKI);
                model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                        1.0F, 1.0F, 1.0F, 1.0F);
            }
        } else {
            if (entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                PacketHandler.sendToServer(new RequestVisualDataC2SPacket(entity.getUUID()));
            }
        }
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

        if (player.level.getGameTime() % 20  == 0) synced.clear();

        VisualData data = getOrRequest(player);

        if (data == null) return;

        BlueFistsVisual.tick(data, player);
    }

    public record VisualData(Set<Ability> toggled, Set<Trait> traits, JujutsuType type, CursedTechnique technique) {
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

            nbt.putInt("type", this.type.ordinal());
            nbt.putInt("technique", this.technique.ordinal());

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
            return new VisualData(toggled, traits, JujutsuType.values()[nbt.getInt("type")], CursedTechnique.values()[nbt.getInt("technique")]);
        }
    }
}
