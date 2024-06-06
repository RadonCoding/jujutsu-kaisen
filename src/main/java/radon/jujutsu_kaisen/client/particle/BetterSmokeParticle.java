package radon.jujutsu_kaisen.client.particle;


import radon.jujutsu_kaisen.client.particle.registry.JJKParticles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class BetterSmokeParticle extends SmokeParticle {
    protected BetterSmokeParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, Options options, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, 0.0D, 0.0D, 0.0D, options.scalar, pSprites);

        this.gravity = 0.0F;
        this.friction = 0.98F;

        this.quadSize = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());
        this.lifetime = options.lifetime();

        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;

        this.hasPhysics = false;

        this.speedUpWhenYMotionIsBlocked = false;
    }

    public record Options(float scalar, int lifetime) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Codec.FLOAT.fieldOf("scalar").forGetter(options -> options.scalar),
                                Codec.INT.fieldOf("lifetime").forGetter(options -> options.lifetime)
                        )
                        .apply(builder, Options::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT,
                Options::scalar,
                ByteBufCodecs.INT,
                Options::lifetime,
                Options::new
        );

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.SMOKE.get();
        }
    }

    public static class Provider implements ParticleProvider<Options> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSpriteSet) {
            this.sprites = pSpriteSet;
        }

        @Override
        public BetterSmokeParticle createParticle(@NotNull BetterSmokeParticle.Options options, @NotNull ClientLevel level, double x, double y, double z,
                                                  double xSpeed, double ySpeed, double zSpeed) {
            return new BetterSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, options, this.sprites);
        }
    }
}