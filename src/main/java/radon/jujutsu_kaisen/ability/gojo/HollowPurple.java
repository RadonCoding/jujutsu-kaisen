package radon.jujutsu_kaisen.ability.gojo;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.projectile.HollowPurpleProjectile;

public class HollowPurple extends Ability {
    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        HollowPurpleProjectile purple = new HollowPurpleProjectile(owner);
        owner.level.addFreshEntity(purple);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 500.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }
}
