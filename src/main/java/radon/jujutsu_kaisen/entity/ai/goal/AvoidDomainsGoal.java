package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class AvoidDomainsGoal extends Goal {
    protected final PathfinderMob mob;
    private final double walkSpeedModifier;
    private final double sprintSpeedModifier;
    @Nullable
    protected DomainExpansionEntity toAvoid;
    @Nullable
    protected Path path;
    protected final PathNavigation pathNav;
    protected final Predicate<LivingEntity> avoidPredicate;
    protected final Predicate<LivingEntity> predicateOnAvoidEntity;

    public AvoidDomainsGoal(PathfinderMob pMob, double pWalkSpeedModifier, double pSprintSpeedModifier) {
        this(pMob, (entity) -> true, pWalkSpeedModifier, pSprintSpeedModifier, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
    }
    
    public AvoidDomainsGoal(PathfinderMob pMob, Predicate<LivingEntity> pAvoidPredicate, double pWalkSpeedModifier, double pSprintSpeedModifier, Predicate<LivingEntity> pPredicateOnAvoidEntity) {
        this.mob = pMob;
        this.avoidPredicate = pAvoidPredicate;
        this.walkSpeedModifier = pWalkSpeedModifier;
        this.sprintSpeedModifier = pSprintSpeedModifier;
        this.predicateOnAvoidEntity = pPredicateOnAvoidEntity;
        this.pathNav = pMob.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public AvoidDomainsGoal(PathfinderMob pMob, double pWalkSpeedModifier, double pSprintSpeedModifier, Predicate<LivingEntity> pPredicateOnAvoidEntity) {
        this(pMob, entity -> true, pWalkSpeedModifier, pSprintSpeedModifier, pPredicateOnAvoidEntity);
    }
    
    public boolean canUse() {
        this.mob.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            DomainExpansionEntity current = null;

            for (DomainExpansionEntity domain : cap.getDomains((ServerLevel) this.mob.level())) {
                double distance = this.mob.distanceTo(domain);

                if (current == null || distance < this.mob.distanceTo(current)) {
                    current = domain;
                }
            }
            this.toAvoid = current;
        });

        if (this.toAvoid == null) {
            return false;
        } else {
            AABB bounds = this.toAvoid.getBounds();
            Vec3 pos = DefaultRandomPos.getPosAway(this.mob, (int) bounds.getXsize(), (int) bounds.getYsize(), this.toAvoid.position());

            if (pos == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr(pos.x(), pos.y(), pos.z()) < this.toAvoid.distanceToSqr(this.mob)) {
                return false;
            } else {
                this.path = this.pathNav.createPath(pos.x(), pos.y(), pos.z(), 0);
                return this.path != null;
            }
        }
    }
    
    public boolean canContinueToUse() {
        return !this.pathNav.isDone();
    }
    
    public void start() {
        this.pathNav.moveTo(this.path, this.walkSpeedModifier);
    }
    
    public void stop() {
        this.toAvoid = null;
    }
    
    public void tick() {
        assert this.toAvoid != null;

        if (this.mob.distanceToSqr(this.toAvoid) < 49.0D) {
            this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
        } else {
            this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
        }
    }
}
