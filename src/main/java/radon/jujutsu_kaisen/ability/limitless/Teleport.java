package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Teleport extends Ability {
    private static final double RANGE = 100.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.hasLineOfSight(target) && owner.distanceTo(target) > 5.0D;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable Entity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit) {
            return hit.getEntity();
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (target != null) {
            owner.swing(InteractionHand.MAIN_HAND);
            owner.teleportTo(target.getX(), target.getY(), target.getZ());
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
