package radon.jujutsu_kaisen.entity.curse.base;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class PackCursedSpirit extends CursedSpirit {
    @Nullable
    private UUID leaderUUID;
    @Nullable
    private PackCursedSpirit cachedLeader;

    protected PackCursedSpirit(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public abstract int getMinCount();

    public abstract int getMaxCount();

    protected abstract PackCursedSpirit spawn();

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pSpawnType, @Nullable SpawnGroupData pSpawnGroupData) {
        if (this.getLeader() == null) {
            int x = Mth.floor(this.getX());
            int y = Mth.floor(this.getY());
            int z = Mth.floor(this.getZ());

            for (int i = 0; i < this.random.nextInt(this.getMinCount(), this.getMaxCount()); i++) {
                BlockPos pos = BlockPos.containing(
                        x + Mth.nextFloat(this.random, this.getBbWidth(), this.getBbWidth() * 4) * Mth.nextInt(this.random, -1, 1),
                        y,
                        z + Mth.nextFloat(this.random, this.getBbWidth(), this.getBbWidth() * 4) * Mth.nextInt(this.random, -1, 1)
                );

                if (SpawnPlacements.getPlacementType(this.getType()).isSpawnPositionOk(this.level(), pos, this.getType())
                        && SpawnPlacements.checkSpawnRules(this.getType(), pLevel.getLevel(), MobSpawnType.REINFORCEMENT, pos, this.level().random)) {
                    PackCursedSpirit entity = this.spawn();
                    entity.moveTo(
                            (double) pos.getX() + 0.5D,
                            pos.getY(),
                            (double) pos.getZ() + 0.5D,
                            Mth.wrapDegrees(this.random.nextFloat() * 360.0F),
                            0.0F
                    );
                    this.level().addFreshEntity(entity);
                }
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pSpawnType, pSpawnGroupData);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.getTarget() != null) {
            double d0 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
            AABB bounds = AABB.unitCubeFromLowerCorner(this.position()).inflate(d0, 10.0D, d0);
            this.level().getEntitiesOfClass(PackCursedSpirit.class, bounds).stream()
                    .filter(entity -> entity != this)
                    .filter(entity -> entity.getTarget() == null)
                    .filter(entity -> !entity.isAlliedTo(this.getTarget()))
                    .filter(entity -> (entity.getLeader() != null && entity.getLeader() == this.getLeader()) || this.getLeader() == entity || entity.getLeader() == this)
                    .forEach(entity -> entity.setTarget(this.getTarget()));
        } else {
            PackCursedSpirit leader = this.getLeader();

            if (leader != null) {
                if (this.distanceTo(leader) >= 1.0D) {
                    this.lookControl.setLookAt(leader, 10.0F, (float) this.getMaxHeadXRot());
                    this.navigation.moveTo(leader, 1.0D);
                }
            }
        }
    }

    public void setLeader(@Nullable PackCursedSpirit leader) {
        if (leader != null) {
            this.leaderUUID = leader.getUUID();
            this.cachedLeader = leader;
        }
    }

    @Nullable
    public PackCursedSpirit getLeader() {
        if (this.cachedLeader != null && !this.cachedLeader.isRemoved()) {
            return this.cachedLeader;
        } else if (this.leaderUUID != null && this.level() instanceof ServerLevel) {
            this.cachedLeader = (PackCursedSpirit) ((ServerLevel) this.level()).getEntity(this.leaderUUID);
            return this.cachedLeader;
        } else {
            return null;
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.leaderUUID != null) {
            pCompound.putUUID("leader", this.leaderUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("leader")) {
            this.leaderUUID = pCompound.getUUID("leader");
        }
    }
}
