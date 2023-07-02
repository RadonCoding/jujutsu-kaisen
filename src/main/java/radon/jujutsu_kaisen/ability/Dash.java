package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Dash extends Ability {
    public static final double RANGE = 30.0D;
    private static final double SPEED = 2.5D;

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        EntityHitResult hit = HelperMethods.getEntityLookAt(owner, RANGE);

        if (hit != null) {
            Entity target = hit.getEntity();

            double distanceX = target.getX() - owner.getX();
            double distanceY = target.getY() - owner.getY();
            double distanceZ = target.getZ() - owner.getZ();

            double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
            double motionX = distanceX / distance * SPEED;
            double motionY = distanceY / distance * SPEED;
            double motionZ = distanceZ / distance * SPEED;

            owner.setDeltaMovement(motionX, motionY, motionZ);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }
}
