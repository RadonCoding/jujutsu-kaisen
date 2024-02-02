package radon.jujutsu_kaisen.client.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.CurseManipulationDataHandler;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.client.visual.base.IOverlay;
import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.RequestVisualDataC2SPacket;

import java.util.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientVisualHandler {
    private static final Map<UUID, ClientData> synced = new HashMap<>();

    public static void receive(UUID identifier, CompoundTag nbt) {
        if (!synced.containsKey(identifier)) {
            synced.put(identifier, new ClientData(nbt));
            return;
        }
        synced.get(identifier).deserializeNBT(nbt);
    }

    @Nullable
    public static ClientVisualHandler.ClientData get(UUID identifier) {
        return synced.get(identifier);
    }

    @Nullable
    public static ClientVisualHandler.ClientData get(Entity entity) {
        if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return null;

        Minecraft mc = Minecraft.getInstance();

        assert mc.level != null && mc.player != null;

        if (synced.containsKey(entity.getUUID())) {
            return synced.get(entity.getUUID());
        } else if (entity == mc.player) {
            if (mc.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                ISorcererData sorcererCap = mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                ICurseManipulationData curseManipulationCap = mc.player.getCapability(CurseManipulationDataHandler.INSTANCE).resolve().orElseThrow();

                Set<ICursedTechnique> techniques = new HashSet<>();

                if (sorcererCap.getTechnique() != null) techniques.add(sorcererCap.getTechnique());
                if (sorcererCap.getCurrentCopied() != null) techniques.add(sorcererCap.getCurrentCopied());
                if (curseManipulationCap.getCurrentAbsorbed() != null) techniques.add(curseManipulationCap.getCurrentAbsorbed());
                if (sorcererCap.getAdditional() != null) techniques.add(sorcererCap.getAdditional());

                return new ClientData(sorcererCap.getToggled(), sorcererCap.getChanneled(), sorcererCap.getTraits(), techniques,
                        sorcererCap.getTechnique(), sorcererCap.getType(), sorcererCap.getExperience(), sorcererCap.getCursedEnergyColor());
            }
        }
        return null;
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (!entity.level().isClientSide) return;

        ClientData data = get(entity);

        if (data == null) return;

        for (IVisual visual : JJKVisuals.VISUALS) {
            if (!visual.isValid(entity, data)) continue;
            visual.tick(entity, data);
        }
    }

    public static <T extends LivingEntity> void renderOverlays(T entity, ResourceLocation texture, EntityModel<T> model, PoseStack poseStack, MultiBufferSource buffer, float partialTicks, int packedLight) {
        ClientData data = get(entity);

        if (data == null) return;

        for (IOverlay overlay : JJKOverlays.OVERLAYS) {
            if (!overlay.isValid(entity, data)) continue;
            overlay.render(entity, data, texture, model, poseStack, buffer, partialTicks, packedLight);
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        synced.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.getConnection() == null) return;

        Entity entity = event.getEntity();

        if (entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            PacketHandler.sendToServer(new RequestVisualDataC2SPacket(entity.getUUID()));
        }
    }

    public static class ClientData {
        public Set<Ability> toggled;
        @Nullable
        public Ability channeled;
        public Set<Trait> traits;
        public Set<ICursedTechnique> techniques;
        @Nullable
        public ICursedTechnique technique;
        public JujutsuType type;
        public float experience;
        public int cursedEnergyColor;

        public int mouth;

        public ClientData(Set<Ability> toggled, @Nullable Ability channeled, Set<Trait> traits, Set<ICursedTechnique> techniques, @Nullable ICursedTechnique technique, JujutsuType type, float experience, int cursedEnergyColor) {
            this.toggled = toggled;
            this.channeled = channeled;
            this.traits = traits;
            this.techniques = techniques;
            this.technique = technique;
            this.type = type;
            this.experience = experience;
            this.cursedEnergyColor = cursedEnergyColor;
        }

        public ClientData(CompoundTag nbt) {
            this.deserializeNBT(nbt);
        }

        public void deserializeNBT(CompoundTag nbt) {
            this.toggled = new HashSet<>();
            this.channeled = nbt.contains("channeled") ? JJKAbilities.getValue(ResourceLocation.tryParse(nbt.getString("channeled"))) : null;
            this.traits = new HashSet<>();
            this.techniques = new HashSet<>();

            this.technique = nbt.contains("technique") ? JJKCursedTechniques.getValue(ResourceLocation.tryParse(nbt.getString("technique"))) : null;

            for (Tag key : nbt.getList("toggled", Tag.TAG_STRING)) {
                this.toggled.add(JJKAbilities.getValue(ResourceLocation.tryParse(key.getAsString())));
            }

            for (Tag key : nbt.getList("traits", Tag.TAG_INT)) {
                if (key instanceof IntTag tag) {
                    this.traits.add(Trait.values()[tag.getAsInt()]);
                }
            }

            for (Tag key : nbt.getList("techniques", Tag.TAG_INT)) {
                this.techniques.add(JJKCursedTechniques.getValue(ResourceLocation.tryParse(key.getAsString())));
            }

            this.type = JujutsuType.values()[nbt.getInt("type")];
            this.experience = nbt.getFloat("experience");
            this.cursedEnergyColor = nbt.getInt("cursed_energy_color");
        }

        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();

            ListTag toggledTag = new ListTag();

            for (Ability ability : this.toggled) {
                toggledTag.add(StringTag.valueOf(JJKAbilities.getKey(ability).toString()));
            }
            nbt.put("toggled", toggledTag);

            if (this.channeled != null) {
                nbt.putString("channeled", JJKAbilities.getKey(this.channeled).toString());
            }

            ListTag traitsTag = new ListTag();

            for (Trait trait : this.traits) {
                traitsTag.add(IntTag.valueOf(trait.ordinal()));
            }
            nbt.put("traits", traitsTag);

            ListTag techniquesTag = new ListTag();

            for (ICursedTechnique technique : this.techniques) {
                techniquesTag.add(StringTag.valueOf(JJKCursedTechniques.getKey(technique).toString()));
            }
            nbt.put("techniques", techniquesTag);

            if (this.technique != null) {
                nbt.putString("technique", JJKCursedTechniques.getKey(this.technique).toString());
            }
            nbt.putInt("type", this.type.ordinal());
            nbt.putFloat("experience", this.experience);
            nbt.putInt("cursed_energy_color", this.cursedEnergyColor);

            return nbt;
        }
    }
}
