package radon.jujutsu_kaisen.data.projection_sorcery;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.data.ten_shadows.TenShadowsData;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncProjectionSorceryDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncTenShadowsDataS2CPacket;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProjectionSorcereryDataProvider {
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity owner = event.getEntity();

        if (!owner.hasData(JJKAttachmentTypes.PROJECTION_SORCERY)) return;

        IProjectionSorceryData data = owner.getData(JJKAttachmentTypes.PROJECTION_SORCERY);
        data.tick();
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player clone = event.getEntity();

        if (event.isWasDeath()) {
            IProjectionSorceryData data = original.getData(JJKAttachmentTypes.PROJECTION_SORCERY);
            data.resetSpeedStacks();
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        IProjectionSorceryData data = player.getData(JJKAttachmentTypes.PROJECTION_SORCERY);
        PacketHandler.sendToClient(new SyncProjectionSorceryDataS2CPacket(data.serializeNBT()), player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        IProjectionSorceryData data = player.getData(JJKAttachmentTypes.PROJECTION_SORCERY);
        PacketHandler.sendToClient(new SyncProjectionSorceryDataS2CPacket(data.serializeNBT()), player);
    }

    public static class Serializer implements IAttachmentSerializer<CompoundTag, IProjectionSorceryData> {
        @Override
        public @NotNull IProjectionSorceryData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag) {
            IProjectionSorceryData data = new ProjectionSorceryData((LivingEntity) holder);
            data.deserializeNBT(tag);
            return data;
        }

        @Override
        public CompoundTag write(IProjectionSorceryData attachment) {
            return attachment.serializeNBT();
        }
    }
}