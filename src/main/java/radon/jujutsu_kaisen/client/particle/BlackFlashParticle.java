package radon.jujutsu_kaisen.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class BlackFlashParticle extends TextureSheetParticle {
    protected BlackFlashParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ);

        this.friction = 0.7F;
        this.gravity = 0.5F;
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.xd += pXSpeed * 0.4D;
        this.yd += pYSpeed * 0.4D;
        this.zd += pZSpeed * 0.4D;
        this.rCol = 0.5F;
        this.gCol = 0.0F;
        this.bCol = 0.0F;
        this.quadSize *= 0.75F;
        this.lifetime = Math.max((int)(6.0D / (Math.random() * 0.8D + 0.6D)), 1);
        this.hasPhysics = false;
    }

    @Override
    public float getQuadSize(float pScaleFactor) {
        return this.quadSize * Mth.clamp(((float)this.age + pScaleFactor) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public BlackFlashParticle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z,
                                                   double xSpeed, double ySpeed, double zSpeed) {
            BlackFlashParticle particle = new BlackFlashParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
