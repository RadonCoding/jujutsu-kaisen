package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;

public class MaximumBlueBlackHole extends BlackHoleEntity {
    private static final int DURATION = 10 * 20;
    private static final float SIZE = 5.0F;

    public MaximumBlueBlackHole(LivingEntity pShooter, Entity target) {
        super(pShooter, JJKAbilities.MAXIMUM_BLUE_STILL.get(), DURATION, SIZE, ParticleColors.DARK_BLUE_COLOR);

        this.setPos(target.getX(), target.getY() + (target.getBbHeight() / 2.0F) - (SIZE / 2), target.getZ());
    }
}
