package radon.jujutsu_kaisen.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Locale;

public class SpinningParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final Vec3 center;
    private final double radius;

    private float angle;

    public SpinningParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites, SpinningParticleOptions options) {
        super(pLevel, pX, pY, pZ);

        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;

        this.quadSize = options.size();
        this.lifetime = 20;

        Vector3f color = options.color();
        this.rCol = color.x();
        this.gCol = color.y();
        this.bCol = color.z();

        this.center = new Vec3(pX, pY, pZ);
        this.radius = options.radius();
        this.angle = options.angle;

        double radians = Math.toRadians(this.angle);
        double x = this.center.x() + this.radius * Math.cos(radians);
        double y = this.center.y();
        double z = this.center.z() + this.radius * Math.sin(radians);

        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;

        this.sprites = pSprites;
    }

    @Override
    public void tick() {
        super.tick();

        this.setSprite(this.sprites.get(this.random));

        this.angle += 360.0F / this.lifetime;
        double radians = Math.toRadians(this.angle);
        double x = this.center.x() + this.radius * Math.cos(radians);
        double y = this.center.y();
        double z = this.center.z() + this.radius * Math.sin(radians);
        this.setPos(x, y, z);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }


    public record SpinningParticleOptions(Vector3f color, double radius, float angle, float size) implements ParticleOptions {
        public static Vector3f BLUE_COLOR = Vec3.fromRGB24(30719).toVector3f();

        public static Deserializer<SpinningParticleOptions> DESERIALIZER = new Deserializer<>() {
            public @NotNull SpinningParticle.SpinningParticleOptions fromCommand(@NotNull ParticleType<SpinningParticleOptions> type, @NotNull StringReader reader) throws CommandSyntaxException {
                Vector3f color = SpinningParticleOptions.readColorVector3f(reader);
                reader.expect(' ');
                return new SpinningParticleOptions(color, reader.readDouble(), reader.readFloat(), reader.readFloat());
            }

            public @NotNull SpinningParticle.SpinningParticleOptions fromNetwork(@NotNull ParticleType<SpinningParticleOptions> type, @NotNull FriendlyByteBuf buf) {
                return new SpinningParticleOptions(SpinningParticleOptions.readColorFromNetwork(buf), buf.readDouble(), buf.readFloat(), buf.readFloat());
            }
        };
        public static final Codec<SpinningParticleOptions> CODEC = RecordCodecBuilder.create((builder) ->
                builder.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(options -> options.color),
                                Codec.DOUBLE.fieldOf("radius").forGetter(options -> options.radius),
                                Codec.FLOAT.fieldOf("angle").forGetter(options -> options.angle),
                                Codec.FLOAT.fieldOf("size").forGetter(options -> options.size))
                        .apply(builder, SpinningParticleOptions::new));

        public static Vector3f readColorVector3f(StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float f0 = reader.readFloat();
            reader.expect(' ');
            float f1 = reader.readFloat();
            reader.expect(' ');
            float f2 = reader.readFloat();
            return new Vector3f(f0, f1, f2);
        }

        public static Vector3f readColorFromNetwork(FriendlyByteBuf buf) {
            return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public @NotNull ParticleType<?> getType() {
            return JujutsuParticles.SPINNING.get();
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buf) {
            buf.writeFloat(this.color.x());
            buf.writeFloat(this.color.y());
            buf.writeFloat(this.color.z());
            buf.writeDouble(this.radius);
            buf.writeFloat(this.angle);
            buf.writeFloat(this.size);
        }

        @Override
        public @NotNull String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.color.x(), this.color.y(), this.color.z(), this.radius, this.angle, this.size);
        }
    }

    public static class Provider implements ParticleProvider<SpinningParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public SpinningParticle createParticle(@NotNull SpinningParticle.SpinningParticleOptions options, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SpinningParticle particle = new SpinningParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites, options);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
