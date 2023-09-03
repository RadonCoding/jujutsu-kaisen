package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;
import java.util.UUID;

public class FishCurseEntity extends CursedSpirit {
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");

    @Nullable
    private UUID leaderUUID;
    @Nullable
    private LivingEntity cachedLeader;

    public FishCurseEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected boolean canFly() {
        return true;
    }

    @Override
    protected boolean canPerformSorcery() {
        return false;
    }

    @Override
    protected boolean hasMeleeAttack() {
        return true;
    }

    public FishCurseEntity(FishCurseEntity leader) {
        this(JJKEntities.FISH_CURSE.get(), leader.level);

        this.setLeader(leader);
    }

    @Override
    protected void customServerAiStep() {
        if (this.getTarget() != null) return;

        LivingEntity leader = this.getLeader();

        if (leader != null) {
            if (this.distanceTo(leader) >= 1.0D) {
                this.lookControl.setLookAt(leader, 10.0F, (float) this.getMaxHeadXRot());
                this.navigation.moveTo(leader, 1.0D);
            }
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (this.getLeader() == null) {
            for (int i = 0; i < this.random.nextInt(4, 16); i++) {
                FishCurseEntity entity = new FishCurseEntity(this);
                entity.setPos(this.position());
                this.level.addFreshEntity(entity);
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public void setLeader(@Nullable LivingEntity pOwner) {
        if (pOwner != null) {
            this.leaderUUID = pOwner.getUUID();
            this.cachedLeader = pOwner;
        }
    }

    @Nullable
    public LivingEntity getLeader() {
        if (this.cachedLeader != null && !this.cachedLeader.isRemoved()) {
            return this.cachedLeader;
        } else if (this.leaderUUID != null && this.level instanceof ServerLevel) {
            this.cachedLeader = (LivingEntity) ((ServerLevel) this.level).getEntity(this.leaderUUID);
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

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.FLYING_SPEED);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.GRADE_4;
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of();
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    private PlayState walkPredicate(AnimationState<FishCurseEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(SWIM);
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk", this::walkPredicate));
    }
}
