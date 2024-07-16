package radon.jujutsu_kaisen.client.slice;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public record Plane(Vector3f pos, float distance) {
    public static final MapCodec<Plane> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            ExtraCodecs.VECTOR3F.fieldOf("pos").forGetter(plane -> plane.pos),
                            Codec.FLOAT.fieldOf("distance").forGetter(plane -> plane.distance)
                    )
                    .apply(builder, Plane::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, Plane> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F,
            Plane::pos,
            ByteBufCodecs.FLOAT,
            Plane::distance,
            Plane::new
    );
}