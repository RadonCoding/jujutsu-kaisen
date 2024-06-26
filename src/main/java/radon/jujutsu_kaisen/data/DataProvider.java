package radon.jujutsu_kaisen.data;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.network.packet.s2c.*;

import java.util.Optional;
import java.util.function.Supplier;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DataProvider {
    public static <T> Optional<T> getDataIfPresent(Level level, Supplier<AttachmentType<T>> type) {
        if (!level.hasData(type)) return Optional.empty();

        return Optional.of(level.getData(type));
    }

    @SubscribeEvent
    public static void onEntityTickPre(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LivingEntity owner)) return;

        if (owner.isDeadOrDying()) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        cap.getSorcererData().tick();
        cap.getAbilityData().tick();
        cap.getChantData().tick();
        cap.getContractData().tick();
        cap.getTenShadowsData().tick();
        cap.getCurseManipulationData().tick();
        cap.getProjectionSorceryData().tick();
        cap.getIdleTransfigurationData().tick();
        cap.getMimicryData().tick();
        cap.getCursedSpeechData().tick();
    }

    @SubscribeEvent
    public static void onLevelTickPre(LevelTickEvent.Pre event) {
        Level level = event.getLevel();

        getDataIfPresent(level, JJKAttachmentTypes.MISSION_LEVEL).ifPresent(IMissionLevelData::tick);
        getDataIfPresent(level, JJKAttachmentTypes.DOMAIN).ifPresent(IDomainData::tick);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player clone = event.getEntity();

        IJujutsuCapability cap = clone.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        if (event.isWasDeath()) {
            ISorcererData sorcererData = cap.getSorcererData();
            sorcererData.setEnergy(sorcererData.getMaxEnergy());
            sorcererData.resetBrainDamage();
            sorcererData.resetBurnout();
            sorcererData.resetExtraEnergy();
            sorcererData.resetBlackFlash();

            IAbilityData abilityData = cap.getAbilityData();
            abilityData.clear();
            abilityData.resetCooldowns();

            ITenShadowsData tenShadowsData = cap.getTenShadowsData();

            if (!ConfigHolder.SERVER.realisticShikigami.get()) {
                tenShadowsData.revive(false);
            }

            IProjectionSorceryData projectionSorceryData = cap.getProjectionSorceryData();
            projectionSorceryData.resetSpeedStacks();

            if (clone instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(sorcererData.serializeNBT(player.registryAccess())));
                PacketDistributor.sendToPlayer(player, new SyncAbilityDataS2CPacket(abilityData.serializeNBT(player.registryAccess())));
                PacketDistributor.sendToPlayer(player, new SyncTenShadowsDataS2CPacket(tenShadowsData.serializeNBT(player.registryAccess())));
                PacketDistributor.sendToPlayer(player, new SyncProjectionSorceryDataS2CPacket(projectionSorceryData.serializeNBT(player.registryAccess())));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(cap.getSorcererData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncAbilityDataS2CPacket(cap.getAbilityData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncChantDataS2CPacket(cap.getChantData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncContractDataS2CPacket(cap.getContractData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncTenShadowsDataS2CPacket(cap.getTenShadowsData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncCurseManipulationDataS2CPacket(cap.getCurseManipulationData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncProjectionSorceryDataS2CPacket(cap.getProjectionSorceryData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncIdleTransfigurationDataS2CPacket(cap.getIdleTransfigurationData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncMimicryDataS2CPacket(cap.getMimicryData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncCursedSpeechDataS2CPacket(cap.getCursedSpeechData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncSkillDataSC2Packet(cap.getSkillData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncMissionEntityDataS2CPacket(cap.getMissionData().serializeNBT(player.registryAccess())));

        getDataIfPresent(player.level(), JJKAttachmentTypes.MISSION_LEVEL).ifPresent(data ->
                PacketDistributor.sendToPlayer(player, new SyncMissionLevelDataS2CPacket(player.level().dimension(),
                        data.serializeNBT(player.registryAccess()))));

        getDataIfPresent(player.level(), JJKAttachmentTypes.DOMAIN).ifPresent(data ->
                PacketDistributor.sendToPlayer(player, new SyncDomainDataS2CPacket(player.level().dimension(),
                        data.serializeNBT(player.registryAccess()))));
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(cap.getSorcererData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncAbilityDataS2CPacket(cap.getAbilityData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncChantDataS2CPacket(cap.getChantData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncContractDataS2CPacket(cap.getContractData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncTenShadowsDataS2CPacket(cap.getTenShadowsData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncCurseManipulationDataS2CPacket(cap.getCurseManipulationData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncProjectionSorceryDataS2CPacket(cap.getProjectionSorceryData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncIdleTransfigurationDataS2CPacket(cap.getIdleTransfigurationData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncMimicryDataS2CPacket(cap.getMimicryData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncCursedSpeechDataS2CPacket(cap.getCursedSpeechData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncSkillDataSC2Packet(cap.getSkillData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncMissionEntityDataS2CPacket(cap.getMissionData().serializeNBT(player.registryAccess())));

        getDataIfPresent(player.level(), JJKAttachmentTypes.MISSION_LEVEL).ifPresent(data ->
                PacketDistributor.sendToPlayer(player, new SyncMissionLevelDataS2CPacket(player.level().dimension(),
                        data.serializeNBT(player.registryAccess()))));

        getDataIfPresent(player.level(), JJKAttachmentTypes.DOMAIN).ifPresent(data ->
                PacketDistributor.sendToPlayer(player, new SyncDomainDataS2CPacket(player.level().dimension(),
                        data.serializeNBT(player.registryAccess()))));
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        PacketDistributor.sendToPlayer(player, new SyncSorcererDataS2CPacket(cap.getSorcererData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncAbilityDataS2CPacket(cap.getAbilityData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncChantDataS2CPacket(cap.getChantData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncContractDataS2CPacket(cap.getContractData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncTenShadowsDataS2CPacket(cap.getTenShadowsData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncCurseManipulationDataS2CPacket(cap.getCurseManipulationData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncProjectionSorceryDataS2CPacket(cap.getProjectionSorceryData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncIdleTransfigurationDataS2CPacket(cap.getIdleTransfigurationData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncMimicryDataS2CPacket(cap.getMimicryData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncCursedSpeechDataS2CPacket(cap.getCursedSpeechData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncSkillDataSC2Packet(cap.getSkillData().serializeNBT(player.registryAccess())));
        PacketDistributor.sendToPlayer(player, new SyncMissionEntityDataS2CPacket(cap.getMissionData().serializeNBT(player.registryAccess())));
    }
}