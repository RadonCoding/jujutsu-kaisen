package radon.jujutsu_kaisen.data;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.data.projection_sorcery.ProjectionSorceryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataProvider {
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity owner = event.getEntity();

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
            abilityData.clearToggled();
            abilityData.resetCooldowns();

            ITenShadowsData tenShadowsData = cap.getTenShadowsData();

            if (!ConfigHolder.SERVER.realisticShikigami.get()) {
                tenShadowsData.revive(false);
            }

            IProjectionSorceryData projectionSorceryData = cap.getProjectionSorceryData();
            projectionSorceryData.resetSpeedStacks();

            if (clone instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(sorcererData.serializeNBT()), player);
                PacketHandler.sendToClient(new SyncAbilityDataS2CPacket(abilityData.serializeNBT()), player);
                PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(tenShadowsData.serializeNBT()), player);
                PacketHandler.sendToClient(new SyncProjectionSorceryDataS2CPacket(projectionSorceryData.serializeNBT()), player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        PacketHandler.sendToClient(new SyncCurseManipulationDataS2CPacket(cap.getCurseManipulationData().serializeNBT()), player);
        PacketHandler.sendToClient(new SyncProjectionSorceryDataS2CPacket(cap.getProjectionSorceryData().serializeNBT()), player);
        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.getSorcererData().serializeNBT()), player);
        PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(cap.getTenShadowsData().serializeNBT()), player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        PacketHandler.sendToClient(new SyncCurseManipulationDataS2CPacket(cap.getCurseManipulationData().serializeNBT()), player);
        PacketHandler.sendToClient(new SyncProjectionSorceryDataS2CPacket(cap.getProjectionSorceryData().serializeNBT()), player);
        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.getSorcererData().serializeNBT()), player);
        PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(cap.getTenShadowsData().serializeNBT()), player);
    }
}