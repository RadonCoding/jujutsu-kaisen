package radon.jujutsu_kaisen.network.packet.s2c;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.CameraShakeHandler;

public record CameraShakeS2CPacket(float intensity, float speed, int duration) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CameraShakeS2CPacket> TYPE = new CustomPacketPayload.Type<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "camera_shake_clientbound"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, CameraShakeS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            CameraShakeS2CPacket::intensity,
            ByteBufCodecs.FLOAT,
            CameraShakeS2CPacket::speed,
            ByteBufCodecs.INT,
            CameraShakeS2CPacket::duration,
            CameraShakeS2CPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> CameraShakeHandler.shakeCamera(this.intensity, this.speed, this.duration));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}