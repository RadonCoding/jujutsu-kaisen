package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.util.HelperMethods;

public class SoulRestoration extends Ability {
    public static final float RANGE = 5.0F;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            return target;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (!(owner.level() instanceof ServerLevel level)) return;

        LivingEntity target = this.getTarget(owner);

        if (target == null) return;

        target.setHealth(target.getMaxHealth());

        int count = 8 + (int) (target.getBbWidth() * target.getBbHeight()) * 16;

        for (int i = 0; i < count; i++) {
            double x = target.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (target.getBbWidth() * 2) - target.getLookAngle().scale(0.35D).x;
            double y = target.getY() + HelperMethods.RANDOM.nextDouble() * target.getBbHeight();
            double z = target.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (target.getBbWidth() * 2) - target.getLookAngle().scale(0.35D).z;
            level.sendParticles(ParticleTypes.SOUL, x, y, z, 0, 0.0D, HelperMethods.RANDOM.nextDouble() * 0.1D, 0.0D, 1.0D);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target != null && target.isAlive()) {
            return (target.getMaxHealth() - target.getHealth()) * 2;
        }
        return 0.0F;
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
