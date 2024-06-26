package radon.jujutsu_kaisen.client.particle;


import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class BlackFlashParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected BlackFlashParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);

        this.lifetime = (int) (2.0F / (this.random.nextFloat() * 0.9F + 0.1F));

        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 4.0F;
        this.setSize(this.quadSize, this.quadSize);

        this.rCol = ParticleColors.BLACK_FLASH.x;
        this.gCol = ParticleColors.BLACK_FLASH.y;
        this.bCol = ParticleColors.BLACK_FLASH.z;

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
        return LightTexture.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSpriteSet) {
            this.sprites = pSpriteSet;
        }

        @Override
        public BlackFlashParticle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z,
                                                 double xSpeed, double ySpeed, double zSpeed) {
            return new BlackFlashParticle(level, x, y, z, this.sprites);
        }
    }
}
