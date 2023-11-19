package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.projectile.FireArrowProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class FireArrow extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        FireArrowProjectile arrow = new FireArrowProjectile(owner, this.getPower(owner));
        owner.level().addFreshEntity(arrow);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 350.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }



    @Override
    public boolean isScalable() {
        return true;
    }

    @Override
    public Classification getClassification() {
        return Classification.FLAMES;
    }
}
