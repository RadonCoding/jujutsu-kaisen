package radon.jujutsu_kaisen.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.network.packet.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.TriggerAbilityC2SPacket;

public class PacketHandler {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(JujutsuKaisen.MODID, "messages"))
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
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
