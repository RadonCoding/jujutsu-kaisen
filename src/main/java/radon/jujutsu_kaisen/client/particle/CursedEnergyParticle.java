package radon.jujutsu_kaisen.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class CursedEnergyParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected CursedEnergyParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);

        this.quadSize = 0.75F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.lifetime = 1;

        this.xd = pXSpeed;
        this.yd = this.random.nextFloat() * pYSpeed;
        this.zd = pZSpeed;
        this.alpha = 0.5F;

        this.rCol = 0.0F;
        this.gCol = 0.86F;
        this.bCol = 1.0F;

        this.sprites = pSprites;

        this.setSprite(this.sprites.get(this.level.random));
    }

    @Override
    public void tick() {
        super.tick();

        this.setSprite(this.sprites.get(this.level.random));
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return JJKParticleRenderTypes.GLOW;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public CursedEnergyParticle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z,
                                                   double xSpeed, double ySpeed, double zSpeed) {
            CursedEnergyParticle particle = new CursedEnergyParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
