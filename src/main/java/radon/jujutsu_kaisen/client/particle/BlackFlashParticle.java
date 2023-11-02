package radon.jujutsu_kaisen.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class BlackFlashParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected BlackFlashParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);

        this.friction = 0.7F;
        this.gravity = 0.5F;
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.xd += pXSpeed * 0.4D;
        this.yd += pYSpeed * 0.4D;
        this.zd += pZSpeed * 0.4D;
        this.rCol = ParticleColors.BLACK_FLASH.x();
        this.gCol = ParticleColors.BLACK_FLASH.y();
        this.bCol = ParticleColors.BLACK_FLASH.z();
        this.quadSize *= 2.0F;
        this.lifetime = Math.max((int) (12.0D / (Math.random() * 0.8D + 0.6D)), 1);
        this.hasPhysics = false;

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
        return JJKParticleRenderTypes.BLACK_FLASH;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public BlackFlashParticle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z,
                                                 double xSpeed, double ySpeed, double zSpeed) {
            return new BlackFlashParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
