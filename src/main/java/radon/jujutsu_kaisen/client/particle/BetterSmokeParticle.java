package radon.jujutsu_kaisen.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Locale;

public class BetterSmokeParticle extends SmokeParticle {
    protected BetterSmokeParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, BetterSmokeParticleOptions options, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, 0.0D, 0.0D, 0.0D, options.scalar, pSprites);

        this.quadSize = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());
        this.lifetime = options.lifetime();

        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;

        this.hasPhysics = false;
    }

    public record BetterSmokeParticleOptions(float scalar, int lifetime) implements ParticleOptions {
        public static Deserializer<BetterSmokeParticleOptions> DESERIALIZER = new Deserializer<>() {
            public @NotNull BetterSmokeParticle.BetterSmokeParticleOptions fromCommand(@NotNull ParticleType<BetterSmokeParticleOptions> type, @NotNull StringReader reader) throws CommandSyntaxException {
                return new BetterSmokeParticleOptions(reader.readFloat(), reader.readInt());
            }

            public @NotNull BetterSmokeParticle.BetterSmokeParticleOptions fromNetwork(@NotNull ParticleType<BetterSmokeParticleOptions> type, @NotNull FriendlyByteBuf buf) {
                return new BetterSmokeParticleOptions(buf.readFloat(), buf.readInt());
            }
        };

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.SMOKE.get();
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buf) {
            buf.writeFloat(this.scalar);
            buf.writeInt(this.lifetime);
        }

        @Override
        public @NotNull String writeToString() {
            return String.format(Locale.ROOT, "%s %.2f %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.scalar, this.lifetime);
        }
    }

    public static class Provider implements ParticleProvider<BetterSmokeParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSpriteSet) {
            this.sprites = pSpriteSet;
        }

        @Override
        public BetterSmokeParticle createParticle(@NotNull BetterSmokeParticle.BetterSmokeParticleOptions options, @NotNull ClientLevel level, double x, double y, double z,
                                                  double xSpeed, double ySpeed, double zSpeed) {
            return new BetterSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, options, this.sprites);
        }
    }
}