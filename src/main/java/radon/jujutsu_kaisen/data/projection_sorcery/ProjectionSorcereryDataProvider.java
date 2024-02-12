package radon.jujutsu_kaisen.data.projection_sorcery;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.data.ten_shadows.TenShadowsData;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncTenShadowsDataS2CPacket;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProjectionSorcereryDataProvider {
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity owner = event.getEntity();

        if (!owner.hasData(JJKAttachmentTypes.PROJECTION_SORCERY)) return;

        IProjectionSorceryData data = owner.getData(JJKAttachmentTypes.PROJECTION_SORCERY);
        data.tick(owner);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player clone = event.getEntity();

        if (!original.hasData(JJKAttachmentTypes.PROJECTION_SORCERY) || !clone.hasData(JJKAttachmentTypes.PROJECTION_SORCERY)) return;

        if (event.isWasDeath()) {
            IProjectionSorceryData data = original.getData(JJKAttachmentTypes.PROJECTION_SORCERY);

            data.resetSpeedStacks();

            if (clone instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(data.serializeNBT()), player);
            }
        }
    }

    public static class Serializer implements IAttachmentSerializer<CompoundTag, IProjectionSorceryData> {
        @Override
        public IProjectionSorceryData read(CompoundTag tag) {
            IProjectionSorceryData data = new ProjectionSorceryData();
            data.deserializeNBT(tag);
            return data;
        }

        @Override
        public CompoundTag write(IProjectionSorceryData attachment) {
            return attachment.serializeNBT();
        }
    }
}