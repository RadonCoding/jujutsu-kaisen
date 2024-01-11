package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class AvoidProjectilesGoal extends Goal {
    protected final PathfinderMob mob;
    private final double walkSpeedModifier;
    private final double sprintSpeedModifier;
    @Nullable
    protected Projectile toAvoid;
    protected final float maxDist;
    @Nullable
    protected Path path;
    protected final PathNavigation pathNav;

    protected final Predicate<Entity> avoidPredicate;

    public AvoidProjectilesGoal(PathfinderMob pMob, float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier) {
        this(pMob, entity -> true, pMaxDistance, pWalkSpeedModifier, pSprintSpeedModifier, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
    }

    public AvoidProjectilesGoal(PathfinderMob pMob, Predicate<Entity> pAvoidPredicate, float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier, Predicate<LivingEntity> pPredicateOnAvoidEntity) {
        this.mob = pMob;
        this.avoidPredicate = pAvoidPredicate;
        this.maxDist = pMaxDistance;
        this.walkSpeedModifier = pWalkSpeedModifier;
        this.sprintSpeedModifier = pSprintSpeedModifier;
        this.pathNav = pMob.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Nullable
    private  <T extends Entity> T getNearestEntity(List<? extends T> pEntities, double pX, double pY, double pZ) {
        double d0 = -1.0D;
        T t = null;

        for (T t1 : pEntities) {
            double d1 = t1.distanceToSqr(pX, pY, pZ);

            if (d0 == -1.0D || d1 < d0) {
                d0 = d1;
                t = t1;
            }
        }
        return t;
    }

    @Override
    public boolean canUse() {
        this.toAvoid = this.getNearestEntity(this.mob.level().getEntitiesOfClass(Projectile.class, this.mob.getBoundingBox()
                .inflate(this.maxDist, 3.0D, this.maxDist), entity -> true), this.mob.getX(), this.mob.getY(), this.mob.getZ());

        if (this.toAvoid == null) {
            return false;
        } else {
            Vec3 escape = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());

            if (escape == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr(escape.x, escape.y, escape.z) < this.toAvoid.distanceToSqr(this.mob)) {
                return false;
            } else {
                this.path = this.pathNav.createPath(escape.x, escape.y, escape.z, 0);
                return this.path != null;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.pathNav.isDone();
    }

    @Override
    public void start() {
        this.pathNav.moveTo(this.path, this.walkSpeedModifier);
    }

    @Override
    public void stop() {
        this.toAvoid = null;
    }

    @Override
    public void tick() {
        if (this.mob.distanceToSqr(this.toAvoid) < 49.0D) {
            this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
        } else {
            this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
        }
    }
}