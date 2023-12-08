package radon.jujutsu_kaisen.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class BloodParticle extends TextureSheetParticle {
    protected BloodParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ);

        this.friction = 0.7F;
        this.gravity = 0.5F;
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.xd += pXSpeed * 0.4D;
        this.yd += pYSpeed * 0.4D;
        this.zd += pZSpeed * 0.4D;
        this.rCol = this.randomizeColor(1.0F);
        this.gCol = this.randomizeColor(0.5F);
        this.bCol = this.randomizeColor(0.5F);
        this.lifetime = Math.max((int) (6.0D / (Math.random() * 0.8D + 0.6D)), 1);
        this.quadSize *= 0.75F;
        this.hasPhysics = false;
    }

    private float randomizeColor(float color) {
        return color - this.random.nextFloat() * color * 0.5F;
    }

    @Override
    public float getQuadSize(float pScaleFactor) {
        return this.quadSize * Mth.clamp(((float) this.age + pScaleFactor) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSpriteSet) {
            this.sprites = pSpriteSet;
        }

        @Override
        public BloodParticle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z,
                                            double xSpeed, double ySpeed, double zSpeed) {
            BloodParticle particle = new BloodParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
