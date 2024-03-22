package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.entity.projectile.FireballProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Fireball extends Ability {


    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(40) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        FireballProjectile fireball = new FireballProjectile(owner, this.getOutput(owner));
        owner.level().addFreshEntity(fireball);
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
    public Classification getClassification() {
        return Classification.FIRE;
    }
}
