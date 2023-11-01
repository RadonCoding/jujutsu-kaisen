package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.projectile.CursedBudProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class CursedBud extends Ability {
    @Override
    public boolean isScalable() {
        return true;
    }

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

        CursedBudProjectile bud = new CursedBudProjectile(owner, this.getPower(owner));

        Vec3 look = owner.getLookAngle();
        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (bud.getBbHeight() / 2.0F), owner.getZ()).add(look);
        bud.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());

        owner.level().addFreshEntity(bud);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 300.0F;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
