package radon.jujutsu_kaisen.network.codec;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class JJKByteBufCodecs {
    public static StreamCodec<ByteBuf, Vec3> VEC3 = new StreamCodec<>() {
        @Override
        public void encode(@NotNull ByteBuf pBuffer, @NotNull Vec3 pValue) {
            pBuffer.writeDouble(pValue.x);
            pBuffer.writeDouble(pValue.y);
            pBuffer.writeDouble(pValue.z);
        }

        @Override
        public @NotNull Vec3 decode(@NotNull ByteBuf pBuffer) {
            return new Vec3(pBuffer.readDouble(), pBuffer.readDouble(), pBuffer.readDouble());
        }
    };
}
