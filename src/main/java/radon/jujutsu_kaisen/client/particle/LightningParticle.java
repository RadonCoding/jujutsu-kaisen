package radon.jujutsu_kaisen.client.particle;

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
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class LightningParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected LightningParticle(ClientLevel pLevel, double pX, double pY, double pZ, Options options, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);

        Vector3f color = options.color();
        this.rCol = color.x;
        this.gCol = color.y;
        this.bCol = color.z;

        this.quadSize = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());
        this.lifetime = options.lifetime();

        this.sprites = pSprites;

        this.setSprite(this.sprites.get(this.random));
    }

    @Override
    public void tick() {
        super.tick();

        this.setSprite(this.sprites.get(this.random));
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return JJKParticleRenderTypes.ADDITIVE;
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        float f = ((float) this.age + pPartialTick) / (float) this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(pPartialTick);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int) (f * 15.0F * 16.0F);

        if (j > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    public record Options(Vector3f color, float scalar, int lifetime) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(options -> options.color),
                                Codec.FLOAT.fieldOf("scalar").forGetter(options -> options.scalar),
                                Codec.INT.fieldOf("lifetime").forGetter(options -> options.lifetime)
                        )
                        .apply(builder, Options::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VECTOR3F,
                Options::color,
                ByteBufCodecs.FLOAT,
                Options::scalar,
                ByteBufCodecs.INT,
                Options::lifetime,
                Options::new
        );

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.LIGHTNING.get();
        }
    }

    public static class Provider implements ParticleProvider<Options> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSpriteSet) {
            this.sprites = pSpriteSet;
        }

        @Override
        public LightningParticle createParticle(@NotNull LightningParticle.Options options, @NotNull ClientLevel level, double x, double y, double z,
                                                double xSpeed, double ySpeed, double zSpeed) {
            return new LightningParticle(level, x, y, z, options, this.sprites);
        }
    }
}