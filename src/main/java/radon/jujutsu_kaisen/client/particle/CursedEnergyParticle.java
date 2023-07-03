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

    protected CursedEnergyParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);

        this.quadSize = 0.5F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.lifetime = (int)(4.0F / (this.random.nextFloat() * 0.9F + 0.1F));

        this.rCol = 0.0F;
        this.gCol = 0.86F;
        this.bCol = 1.0F;

        this.sprites = pSprites;
    }

    @Override
    public void tick() {
        super.tick();

        this.setSpriteFromAge(this.sprites);
        this.alpha = (-(0.25F / (float) this.lifetime) * this.age + 0.25F);
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
        public CursedEnergyParticle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            CursedEnergyParticle particle = new CursedEnergyParticle(level, x, y, z,  this.sprites);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
