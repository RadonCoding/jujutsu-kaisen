package radon.jujutsu_kaisen.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Locale;

public class VaporParticle<T extends VaporParticle.VaporParticleOptions> extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final boolean glow;

    protected VaporParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, T options, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);

        this.lifetime = options.lifetime();

        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;

        Vector3f color = options.color();
        this.rCol = color.x;
        this.gCol = color.y;
        this.bCol = color.z;

        this.alpha = options.opacity();

        this.glow = options.glow();

        this.quadSize = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());

        this.sprites = pSprites;
        this.setSprite(this.sprites.get(this.random));

        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();

        this.setSprite(this.sprites.get(this.random));
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return this.glow ? JJKParticleRenderTypes.GLOW : JJKParticleRenderTypes.TRANSLUCENT;
    }

    public record VaporParticleOptions(Vector3f color, float scalar, float opacity, boolean glow, int lifetime) implements ParticleOptions {
        public static Deserializer<VaporParticleOptions> DESERIALIZER = new Deserializer<>() {
            public @NotNull VaporParticleOptions fromCommand(@NotNull ParticleType<VaporParticleOptions> type, @NotNull StringReader reader) throws CommandSyntaxException {
                Vector3f color = VaporParticleOptions.readColorVector3f(reader);
                reader.expect(' ');
                return new VaporParticleOptions(color, reader.readFloat(), reader.readFloat(), reader.readBoolean(), reader.readInt());
            }

            public @NotNull VaporParticleOptions fromNetwork(@NotNull ParticleType<VaporParticleOptions> type, @NotNull FriendlyByteBuf buf) {
                return new VaporParticleOptions(VaporParticleOptions.readColorFromNetwork(buf), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readInt());
            }
        };

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.VAPOR.get();
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
            buf.writeFloat(this.color.x);
            buf.writeFloat(this.color.y);
            buf.writeFloat(this.color.z);
            buf.writeFloat(this.scalar);
            buf.writeFloat(this.opacity);
            buf.writeBoolean(this.glow);
            buf.writeInt(this.lifetime);
        }

        @Override
        public @NotNull String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %b %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
                    this.color.x, this.color.y, this.color.z, this.scalar, this.opacity, this.glow, this.lifetime);
        }
    }

    public static class Provider implements ParticleProvider<VaporParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(@NotNull VaporParticleOptions pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            return new VaporParticle<>(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, this.sprites);
        }
    }
}