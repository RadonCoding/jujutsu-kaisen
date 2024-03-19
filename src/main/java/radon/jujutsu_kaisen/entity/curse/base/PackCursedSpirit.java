package radon.jujutsu_kaisen.entity.curse.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
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

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (this.getLeader() == null) {
            for (int i = 0; i < this.random.nextInt(this.getMinCount(), this.getMaxCount()); i++) {
                PackCursedSpirit entity = this.spawn();
                entity.setPos(this.position());
                this.level().addFreshEntity(entity);
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
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
