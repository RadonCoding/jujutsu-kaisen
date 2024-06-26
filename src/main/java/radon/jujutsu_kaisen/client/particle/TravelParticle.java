package radon.jujutsu_kaisen.client.particle;


import com.mojang.datafixers.util.Function7;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.client.particle.registry.JJKParticles;
import radon.jujutsu_kaisen.network.codec.JJKByteBufCodecs;

import java.util.function.Function;

public class TravelParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final Vec3 target;
    private final boolean glow;
    private final boolean fade;
    private final float opacity;

    protected TravelParticle(ClientLevel pLevel, double pX, double pY, double pZ, Options options, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);

        this.quadSize = Math.max(options.scalar, (this.random.nextFloat() - 0.5F) * options.scalar);
        this.setSize(this.quadSize, this.quadSize);

        this.lifetime = options.lifetime;

        this.target = options.target;

        Vector3f color = options.color;
        this.rCol = color.x;
        this.gCol = color.y;
        this.bCol = color.z;

        this.opacity = options.opacity;

        this.glow = options.glow;

        this.fade = options.fade;

        if (this.fade) this.alpha = this.opacity * (1.0F - ((float) this.age / this.lifetime));

        this.sprites = pSprites;

        this.setSprite(this.sprites.get(this.level.random));
    }

    @Override
    public void tick() {
        super.tick();

        this.setSprite(this.sprites.get(this.level.random));

        if (this.fade) this.alpha = this.opacity * (1.0F - ((float) this.age / this.lifetime));

        Vec3 pos = new Vec3(this.x, this.y, this.z);
        Vec3 direction = this.target.subtract(pos);

        double factor = (double) this.age / this.lifetime;

        Vec3 newPos = pos.add(direction.scale(factor));
        this.setPos(newPos.x, newPos.y, newPos.z);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return this.glow ? JJKParticleRenderTypes.GLOW : JJKParticleRenderTypes.TRANSLUCENT;
    }

    public record Options(Vec3 target, Vector3f color, float scalar, float opacity, boolean glow, boolean fade,
                          int lifetime) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Vec3.CODEC.fieldOf("target").forGetter(options -> options.target),
                                ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(options -> options.color),
                                Codec.FLOAT.fieldOf("scalar").forGetter(options -> options.scalar),
                                Codec.FLOAT.fieldOf("opacity").forGetter(options -> options.opacity),
                                Codec.BOOL.fieldOf("glow").forGetter(options -> options.glow),
                                Codec.BOOL.fieldOf("fade").forGetter(options -> options.fade),
                                Codec.INT.fieldOf("lifetime").forGetter(options -> options.lifetime)
                        )
                        .apply(builder, Options::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = composite(
                JJKByteBufCodecs.VEC3,
                Options::target,
                ByteBufCodecs.VECTOR3F,
                Options::color,
                ByteBufCodecs.FLOAT,
                Options::scalar,
                ByteBufCodecs.FLOAT,
                Options::opacity,
                ByteBufCodecs.BOOL,
                Options::glow,
                ByteBufCodecs.BOOL,
                Options::fade,
                ByteBufCodecs.INT,
                Options::lifetime,
                Options::new
        );

        private static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(
                final StreamCodec<? super B, T1> pCodec1,
                final Function<C, T1> pGetter1,
                final StreamCodec<? super B, T2> pCodec2,
                final Function<C, T2> pGetter2,
                final StreamCodec<? super B, T3> pCodec3,
                final Function<C, T3> pGetter3,
                final StreamCodec<? super B, T4> pCodec4,
                final Function<C, T4> pGetter4,
                final StreamCodec<? super B, T5> pCodec5,
                final Function<C, T5> pGetter5,
                final StreamCodec<? super B, T6> pCodec6,
                final Function<C, T6> pGetter6,
                final StreamCodec<? super B, T7> pCodec7,
                final Function<C, T7> pGetter7,
                final Function7<T1, T2, T3, T4, T5, T6, T7, C> pFactory
        ) {
            return new StreamCodec<>() {
                @Override
                public @NotNull C decode(@NotNull B p_330310_) {
                    T1 t1 = pCodec1.decode(p_330310_);
                    T2 t2 = pCodec2.decode(p_330310_);
                    T3 t3 = pCodec3.decode(p_330310_);
                    T4 t4 = pCodec4.decode(p_330310_);
                    T5 t5 = pCodec5.decode(p_330310_);
                    T6 t6 = pCodec6.decode(p_330310_);
                    T7 t7 = pCodec7.decode(p_330310_);
                    return pFactory.apply(t1, t2, t3, t4, t5, t6, t7);
                }

                @Override
                public void encode(@NotNull B p_332052_, @NotNull C p_331912_) {
                    pCodec1.encode(p_332052_, pGetter1.apply(p_331912_));
                    pCodec2.encode(p_332052_, pGetter2.apply(p_331912_));
                    pCodec3.encode(p_332052_, pGetter3.apply(p_331912_));
                    pCodec4.encode(p_332052_, pGetter4.apply(p_331912_));
                    pCodec5.encode(p_332052_, pGetter5.apply(p_331912_));
                    pCodec6.encode(p_332052_, pGetter6.apply(p_331912_));
                    pCodec7.encode(p_332052_, pGetter7.apply(p_331912_));
                }
            };
        }

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.TRAVEL.get();
        }
    }


    public static class Provider implements ParticleProvider<Options> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSpriteSet) {
            this.sprites = pSpriteSet;
        }

        @Override
        public TravelParticle createParticle(@NotNull TravelParticle.Options options, @NotNull ClientLevel level, double x, double y, double z,
                                             double xSpeed, double ySpeed, double zSpeed) {
            return new TravelParticle(level, x, y, z, options, this.sprites);
        }
    }
}
