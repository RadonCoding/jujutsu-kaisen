package radon.jujutsu_kaisen.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Locale;

public class GenericParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected GenericParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, GenericParticleOptions options, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);

        this.quadSize = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());
        this.lifetime = options.lifetime();

        Vector3f color = options.color();
        this.rCol = color.x();
        this.gCol = color.y();
        this.bCol = color.z();
        
        this.sprites = pSprites;

        this.setSprite(this.sprites.get(this.level.random));
    }

    @Override
    public void tick() {
        super.tick();

        this.setSprite(this.sprites.get(this.level.random));
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public record GenericParticleOptions(Vector3f color, float scalar, int lifetime) implements ParticleOptions {
        public static Deserializer<GenericParticleOptions> DESERIALIZER = new Deserializer<>() {
            public @NotNull GenericParticleOptions fromCommand(@NotNull ParticleType<GenericParticleOptions> type, @NotNull StringReader reader) throws CommandSyntaxException {
                Vector3f color = GenericParticleOptions.readColorVector3f(reader);
                reader.expect(' ');
                return new GenericParticleOptions(color, reader.readFloat(), reader.readInt());
            }

            public @NotNull GenericParticleOptions fromNetwork(@NotNull ParticleType<GenericParticleOptions> type, @NotNull FriendlyByteBuf buf) {
                return new GenericParticleOptions(GenericParticleOptions.readColorFromNetwork(buf), buf.readFloat(), buf.readInt());
            }
        };

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.GENERIC.get();
        }

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
        public void writeToNetwork(FriendlyByteBuf buf) {
            buf.writeFloat(this.color.x());
            buf.writeFloat(this.color.y());
            buf.writeFloat(this.color.z());
            buf.writeFloat(this.scalar);
            buf.writeInt(this.lifetime);
        }

        @Override
        public @NotNull String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.color.x(), this.color.y(), this.color.z(), this.scalar, this.lifetime);
        }
    }

    public static class Provider implements ParticleProvider<GenericParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public GenericParticle createParticle(@NotNull GenericParticleOptions options, @NotNull ClientLevel level, double x, double y, double z,
                                             double xSpeed, double ySpeed, double zSpeed) {
            return new GenericParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, options, this.sprites);
        }
    }
}
