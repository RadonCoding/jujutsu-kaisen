package radon.jujutsu_kaisen.entity.ai.goal;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.tags.JJKStructureTags;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class MoveThroughMissionGoal extends Goal {
    protected final PathfinderMob mob;
    private final double speedModifier;
    @Nullable
    private Path path;
    private BlockPos poiPos;
    private final boolean onlyAtNight;
    private final List<BlockPos> visited = Lists.newArrayList();
    private final int stopDistance;

    public MoveThroughMissionGoal(PathfinderMob pMob, double pSpeedModifier, boolean pOnlyAtNight, int pStopDistance) {
        this.mob = pMob;
        this.speedModifier = pSpeedModifier;
        this.onlyAtNight = pOnlyAtNight;
        this.stopDistance = pStopDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!GoalUtils.hasGroundPathNavigation(this.mob)) {
            return false;
        } else {
            this.updateVisited();

            if (this.onlyAtNight && this.mob.level().isDay()) {
                return false;
            } else {
                ServerLevel level = (ServerLevel) this.mob.level();
                BlockPos pos = this.mob.blockPosition();

                if (!level.isCloseToVillage(pos, 6)) {
                    return false;
                } else {
                    Vec3 target = LandRandomPos.getPos(
                            this.mob,
                            15,
                            7,
                            offset -> {
                                if (level.structureManager().getStructureWithPieceAt(offset, JJKStructureTags.IS_MISSION) == StructureStart.INVALID_START) {
                                    return Double.NEGATIVE_INFINITY;
                                } else {
                                    return -offset.distSqr(pos);
                                }
                            }
                    );
                    
                    if (target == null) {
                        return false;
                    } else {
                            this.poiPos = BlockPos.containing(target);
                            PathNavigation navigation = this.mob.getNavigation();
                            this.path = navigation.createPath(this.poiPos, 0);

                            if (this.path == null) {
                                Vec3 towards = DefaultRandomPos.getPosTowards(this.mob, 10, 7, Vec3.atBottomCenterOf(this.poiPos), (float) (Math.PI / 2));

                                if (towards == null) {
                                    return false;
                                }

                                this.path = this.mob.getNavigation().createPath(towards.x, towards.y, towards.z, 0);

                                return this.path != null;
                            }
                            return true;
                        }
                }
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.getNavigation().isDone()) {
            return false;
        } else {
            return !this.poiPos.closerToCenterThan(this.mob.position(), this.mob.getBbWidth() + (float) this.stopDistance);
        }
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
    }

    @Override
    public void stop() {
        if (this.mob.getNavigation().isDone() || this.poiPos.closerToCenterThan(this.mob.position(), this.stopDistance)) {
            this.visited.add(this.poiPos);
        }
    }

    private boolean hasNotVisited(BlockPos target) {
        for (BlockPos pos : this.visited) {
            if (Objects.equals(target, pos)) {
                return false;
            }
        }
        return true;
    }

    private void updateVisited() {
        if (this.visited.size() > 15) {
            this.visited.remove(0);
        }
    }
}