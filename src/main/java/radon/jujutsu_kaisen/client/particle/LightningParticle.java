package radon.jujutsu_kaisen.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class LightningParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected LightningParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);

        this.quadSize = 0.3F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.lifetime = 10;

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
        return JJKParticleRenderTypes.GLOW;
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        float f = ((float)this.age + pPartialTick) / (float)this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(pPartialTick);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(f * 15.0F * 16.0F);

        if (j > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public LightningParticle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z,
                                                   double xSpeed, double ySpeed, double zSpeed) {
            return new LightningParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}