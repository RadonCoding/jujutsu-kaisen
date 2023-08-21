package radon.jujutsu_kaisen.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.network.packet.c2s.*;
import radon.jujutsu_kaisen.network.packet.s2c.*;

public class PacketHandler {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(JujutsuKaisen.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true).simpleChannel();
        INSTANCE.messageBuilder(SyncSorcererDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncSorcererDataS2CPacket::new)
                .encoder(SyncSorcererDataS2CPacket::encode)
                .consumerMainThread(SyncSorcererDataS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(TriggerAbilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(TriggerAbilityC2SPacket::new)
                .encoder(TriggerAbilityC2SPacket::encode)
                .consumerMainThread(TriggerAbilityC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncOverlayDataLocalS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncOverlayDataLocalS2CPacket::new)
                .encoder(SyncOverlayDataLocalS2CPacket::encode)
                .consumerMainThread(SyncOverlayDataLocalS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncOverlayDataRemoteS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncOverlayDataRemoteS2CPacket::new)
                .encoder(SyncOverlayDataRemoteS2CPacket::encode)
                .consumerMainThread(SyncOverlayDataRemoteS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(RequestOverlayDataC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RequestOverlayDataC2SPacket::new)
                .encoder(RequestOverlayDataC2SPacket::encode)
                .consumerMainThread(RequestOverlayDataC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(RequestSorcererDataC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RequestSorcererDataC2SPacket::new)
                .encoder(RequestSorcererDataC2SPacket::encode)
                .consumerMainThread(RequestSorcererDataC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(ReceiveSorcererDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ReceiveSorcererDataS2CPacket::new)
                .encoder(ReceiveSorcererDataS2CPacket::encode)
                .consumerMainThread(ReceiveSorcererDataS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(OpenInventoryCurseC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenInventoryCurseC2SPacket::new)
                .encoder(OpenInventoryCurseC2SPacket::encode)
                .consumerMainThread(OpenInventoryCurseC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(ShootPistolC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ShootPistolC2SPacket::new)
                .encoder(ShootPistolC2SPacket::encode)
                .consumerMainThread(ShootPistolC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(CameraShakeS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CameraShakeS2CPacket::new)
                .encoder(CameraShakeS2CPacket::encode)
                .consumerMainThread(CameraShakeS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(CommandableTargetC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CommandableTargetC2SPacket::new)
                .encoder(CommandableTargetC2SPacket::encode)
                .consumerMainThread(CommandableTargetC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetOverlayMessageS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SetOverlayMessageS2CPacket::new)
                .encoder(SetOverlayMessageS2CPacket::encode)
                .consumerMainThread(SetOverlayMessageS2CPacket::handle)
                .add();
        INSTANCE.messageBuilder(UpdateMultipartS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(UpdateMultipartS2CPacket::new)
                .encoder(UpdateMultipartS2CPacket::encode)
                .consumerMainThread(UpdateMultipartS2CPacket.Handler::onMessage)
                .add();
        INSTANCE.messageBuilder(JumpInputListenerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(JumpInputListenerC2SPacket::new)
                .encoder(JumpInputListenerC2SPacket::encode)
                .consumerMainThread(JumpInputListenerC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetFrequencyC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetFrequencyC2SPacket::new)
                .encoder(SetFrequencyC2SPacket::encode)
                .consumerMainThread(SetFrequencyC2SPacket::handle)
                .add();
        INSTANCE.messageBuilder(SetFrequencyS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SetFrequencyS2CPacket::new)
                .encoder(SetFrequencyS2CPacket::encode)
                .consumerMainThread(SetFrequencyS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendTracking(MSG message, Entity entity) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }

    public static <MSG> void broadcastNearby(MSG message, LivingEntity entity) {
        INSTANCE.send(PacketDistributor.NEAR.with(() ->
                new PacketDistributor.TargetPoint(entity.getX(), entity.getY(), entity.getZ(),
                        64, entity.level.dimension())), message);
    }
}
