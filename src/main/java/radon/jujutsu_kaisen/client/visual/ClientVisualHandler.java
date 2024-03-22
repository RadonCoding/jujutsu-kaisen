package radon.jujutsu_kaisen.client.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
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
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

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
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return null;

        if (synced.containsKey(entity.getUUID())) {
            return synced.get(entity.getUUID());
        } else if (entity == mc.player) {
            IJujutsuCapability cap = mc.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return null;

            ISorcererData sorcererData = cap.getSorcererData();
            IAbilityData abilityData = cap.getAbilityData();

            ClientData client = new ClientData(abilityData.getToggled(), abilityData.getChanneled(), sorcererData.getTraits(), sorcererData.getActiveTechniques(),
                    sorcererData.getTechnique(), sorcererData.getType(), sorcererData.getExperience(), sorcererData.getCursedEnergyColor());

            synced.put(mc.player.getUUID(), client);

            return client;
        }
        return null;
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (!entity.level().isClientSide) return;

        ClientData client = get(entity);

        if (client == null) return;

        for (IVisual visual : JJKVisuals.VISUALS) {
            if (!visual.isValid(entity, client)) continue;
            visual.tick(entity, client);
        }
    }

    public static <T extends LivingEntity> void renderOverlays(T entity, ResourceLocation texture, EntityModel<T> model, PoseStack poseStack, MultiBufferSource buffer, float partialTicks, int packedLight) {
        ClientData client = get(entity);

        if (client == null) return;

        for (IOverlay overlay : JJKOverlays.OVERLAYS) {
            if (!overlay.isValid(entity, client)) continue;
            overlay.render(entity, client, texture, model, poseStack, buffer, partialTicks, packedLight);
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

        if (entity == mc.player) return;

        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        PacketHandler.sendToServer(new RequestVisualDataC2SPacket(entity.getUUID()));
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
            this.channeled = nbt.contains("channeled") ? JJKAbilities.getValue(new ResourceLocation(nbt.getString("channeled"))) : null;

            this.technique = nbt.contains("technique") ? JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("technique"))) : null;

            this.toggled = new HashSet<>();

            for (Tag key : nbt.getList("toggled", Tag.TAG_STRING)) {
                this.toggled.add(JJKAbilities.getValue(new ResourceLocation(key.getAsString())));
            }

            this.traits = new HashSet<>();

            for (Tag tag : nbt.getList("traits", Tag.TAG_INT)) {
                this.traits.add(Trait.values()[((IntTag) tag).getAsInt()]);
            }

            this.techniques = new HashSet<>();

            for (Tag key : nbt.getList("techniques", Tag.TAG_INT)) {
                this.techniques.add(JJKCursedTechniques.getValue(new ResourceLocation(key.getAsString())));
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
