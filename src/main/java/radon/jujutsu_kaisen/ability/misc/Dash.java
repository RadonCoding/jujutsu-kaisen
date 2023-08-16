package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Dash extends Ability {
    public static final double RANGE = 30.0D;
    private static final double SPEED = 2.5D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() == target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit) {
            Entity target = hit.getEntity();

            double distanceX = target.getX() - owner.getX();
            double distanceY = target.getY() - owner.getY();
            double distanceZ = target.getZ() - owner.getZ();

            double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
            double motionX = distanceX / distance * SPEED;
            double motionY = distanceY / distance * SPEED;
            double motionZ = distanceZ / distance * SPEED;

            owner.setDeltaMovement(motionX, motionY, motionZ);
        } else if (owner.isInWater() || owner.isOnGround()) {
            owner.setDeltaMovement(owner.getLookAngle().scale(SPEED));
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }
}
