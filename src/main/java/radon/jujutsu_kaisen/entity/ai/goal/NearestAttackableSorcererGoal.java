package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class NearestAttackableSorcererGoal extends TargetGoal {
    private static final int DEFAULT_RANDOM_INTERVAL = 10;
    protected final int randomInterval;

    @Nullable
    protected LivingEntity target;

    protected TargetingConditions targetConditions;

    public NearestAttackableSorcererGoal(Mob pMob, boolean pMustSee) {
        this(pMob, DEFAULT_RANDOM_INTERVAL, pMustSee, false, null);
    }

    public NearestAttackableSorcererGoal(Mob pMob, boolean pMustSee, Predicate<LivingEntity> pTargetPredicate) {
        this(pMob, DEFAULT_RANDOM_INTERVAL, pMustSee, false, pTargetPredicate);
    }

    public NearestAttackableSorcererGoal(Mob pMob, boolean pMustSee, boolean pMustReach) {
        this(pMob, DEFAULT_RANDOM_INTERVAL, pMustSee, pMustReach, null);
    }

    public NearestAttackableSorcererGoal(Mob pMob, int pRandomInterval, boolean pMustSee, boolean pMustReach, @Nullable Predicate<LivingEntity> pTargetPredicate) {
        super(pMob, pMustSee, pMustReach);

        this.randomInterval = reducedTickDelay(pRandomInterval);
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(pTargetPredicate);
    }

    @Override
    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        } else {
            this.findTarget();
            return this.target != null;
        }
    }

    protected AABB getTargetSearchArea(double pTargetDistance) {
        return this.mob.getBoundingBox().inflate(pTargetDistance, 4.0D, pTargetDistance);
    }

    protected void findTarget() {
        this.target = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(LivingEntity.class, this.getTargetSearchArea(this.getFollowDistance()), entity -> {
            AtomicBoolean result = new AtomicBoolean();
            entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> result.set(cap.getType() == JujutsuType.SORCERER));
            return result.get();
        }), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
    }

    @Override
    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }

    public void setTarget(@Nullable LivingEntity pTarget) {
        this.target = pTarget;
    }
}