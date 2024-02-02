package radon.jujutsu_kaisen.network.packet.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.NetworkEvent;
import radon.jujutsu_kaisen.client.CameraShakeHandler;

import java.util.function.Supplier;

public class CameraShakeS2CPacket {
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

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(this.intensity);
        buf.writeFloat(this.speed);
        buf.writeInt(this.duration);
    }

    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            if (FMLLoader.getDist().isClient()) {
                CameraShakeHandler.shakeCamera(this.intensity, this.speed, this.duration);
            }
        });
        ctx.setPacketHandled(true);
    }
}