package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.CameraShakeHandler;

public class CameraShakeS2CPacket implements CustomPacketPayload {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(JujutsuKaisen.MOD_ID, "camera_shake_clientbound");

    private final float intensity;
    private final float speed;
    private final int duration;

    public CameraShakeS2CPacket(float intensity, float speed, int duration) {
        this.intensity = intensity;
        this.speed = speed;
        this.duration = duration;
    }

    public CameraShakeS2CPacket(FriendlyByteBuf buf) {
        this(buf.readFloat(), buf.readFloat(), buf.readInt());
    }

    public void handle(ConfigurationPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> CameraShakeHandler.shakeCamera(this.intensity, this.speed, this.duration));
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeFloat(this.intensity);
        pBuffer.writeFloat(this.speed);
        pBuffer.writeInt(this.duration);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return IDENTIFIER;
    }
}