package radon.jujutsu_kaisen.ability.boogie_woogie;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.projectile.CursedEnergyImbuedItemProjectile;

public class ItemSwap extends Ability {
    public static final double RANGE = 30.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public Ability.ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Nullable
    private Entity getTarget(LivingEntity owner) {
        Entity target = null;

        for (Entity entity : owner.level().getEntitiesOfClass(CursedEnergyImbuedItemProjectile.class, AABB.ofSize(owner.position(), RANGE, RANGE, RANGE))) {
            if (target == null || entity.distanceTo(owner) < target.distanceTo(owner)) {
                target = entity;
            }
        }
        return target;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        Entity target = this.getTarget(owner);

        if (target != null) {
            SwapSelf.swap(owner, target);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 5.0F;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }
}
