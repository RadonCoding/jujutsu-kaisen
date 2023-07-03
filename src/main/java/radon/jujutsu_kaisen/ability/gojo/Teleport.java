package radon.jujutsu_kaisen.ability.gojo;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Teleport extends Ability {
    public static final double RANGE = 100.0D;

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        EntityHitResult hit = HelperMethods.getEntityLookAt(owner, RANGE);

        if (hit != null) {
            Entity target = hit.getEntity();
            owner.teleportTo(target.getX(), target.getY(), target.getZ());
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }
}
