package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Uppercut extends Ability {
    private static final double JUMP = 1.5D;
    private static final double RANGE = 10.0D;
    private static final double LAUNCH = 2.0D;
    private static final double SPEED = 2.5D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && HelperMethods.RANDOM.nextInt(3) == 0 && owner.hasLineOfSight(target);
    }

    private @Nullable Entity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit) {
            return hit.getEntity();
        }
        return null;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (target != null) {
            owner.setDeltaMovement(target.position().subtract(owner.getX(), owner.getY(), owner.getZ()).scale(SPEED));

            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                cap.scheduleTickEvent(() -> {
                    if (owner.distanceTo(target) <= 2.0D) {
                        owner.swing(InteractionHand.MAIN_HAND);
                        owner.level.explode(owner, JJKDamageSources.indirectJujutsuAttack(owner, owner, this), null,
                                owner.getX(), owner.getY(), owner.getZ(), 1.0F, false, Level.ExplosionInteraction.NONE);
                        target.setDeltaMovement(owner.getLookAngle().multiply(LAUNCH, 0.0D, LAUNCH).add(0.0D, JUMP, 0.0D));
                        return true;
                    }
                    return false;
                }, 20);

                cap.scheduleTickEvent(() -> {
                    if (owner.hasLineOfSight(target)) {
                        owner.setDeltaMovement(target.position().subtract(owner.getX(), owner.getY(), owner.getZ()));
                    }
                    return false;
                }, 20);
            });
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public int getCooldown() {
        return 3 * 20;
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
    public Classification getClassification() {
        return Classification.MELEE;
    }
}