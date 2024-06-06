package radon.jujutsu_kaisen.client.particle;


import radon.jujutsu_kaisen.client.particle.registry.JJKParticles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class CursedEnergyParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected CursedEnergyParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, Options options, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);

        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;

        this.quadSize = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());
        this.lifetime = options.lifetime();

        Vector3f color = options.color();
        this.rCol = color.x;
        this.gCol = color.y;
        this.bCol = color.z;

        this.alpha = options.opacity();

        this.hasPhysics = false;

        this.sprites = pSprites;
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void tick() {
        super.tick();

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return  JJKParticleRenderTypes.GLOW;
    }

    public record Options(Vector3f color, float scalar, float opacity, int lifetime) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(options -> options.color),
                                Codec.FLOAT.fieldOf("scalar").forGetter(options -> options.scalar),
                                Codec.FLOAT.fieldOf("opacity").forGetter(options -> options.opacity),
                                Codec.INT.fieldOf("lifetime").forGetter(options -> options.lifetime)
                        )
                        .apply(builder, Options::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VECTOR3F,
                Options::color,
                ByteBufCodecs.FLOAT,
                Options::scalar,
                ByteBufCodecs.FLOAT,
                Options::opacity,
                ByteBufCodecs.INT,
                Options::lifetime,
                Options::new
        );

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.CURSED_ENERGY.get();
        }
    }

    public static class Provider implements ParticleProvider<Options> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(@NotNull CursedEnergyParticle.Options pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            return new CursedEnergyParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, this.sprites);
        }
    }
}