package radon.jujutsu_kaisen.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.ability.idle_transfiguration.IdleTransfiguration;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.ai.goal.*;
import radon.jujutsu_kaisen.entity.base.ICommandable;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.base.SummonEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class TransfiguredSoulEntity extends SummonEntity implements ISorcerer, ICommandable {
    public static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(TransfiguredSoulEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

    protected TransfiguredSoulEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public TransfiguredSoulEntity(EntityType<? extends TamableAnimal> pType, LivingEntity owner) {
        super(pType, owner.level());

        this.setTame(true);
        this.setOwner(owner);

        Vec3 pos = owner.position()
                .subtract(RotationUtil.getTargetAdjustedLookAngle(owner).multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.setVariant(HelperMethods.randomEnum(Variant.class));
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        LivingEntity owner = this.getOwner();

        if (owner != null && pSource.getEntity() == owner) {
            IdleTransfiguration.absorb(owner, this);
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void registerGoals() {
        int target = 1;
        int goal = 1;

        this.goalSelector.addGoal(goal++, new WaterWalkingFloatGoal(this));
        this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(goal++, new BetterFollowOwnerGoal(this, 1.0D, 25.0F, 10.0F, false));
        this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(target++, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(target, new OwnerHurtTargetGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_VARIANT, -1);
    }

    public Variant getVariant() {
        return Variant.values()[this.entityData.get(DATA_VARIANT)];
    }

    private void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.ordinal());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("variant", this.getVariant().ordinal());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.setVariant(Variant.values()[pCompound.getInt("variant")]);
    }

    private PlayState walkRunPredicate(AnimationState<TransfiguredSoulEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        }
        return PlayState.STOP;
    }

    private PlayState swingPredicate(AnimationState<TransfiguredSoulEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run", this::walkRunPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
    }

    @Override
    public Summon<?> getAbility() {
        return null;
    }

    @Override
    public boolean canChangeTarget() {
        return true;
    }

    @Override
    public void changeTarget(LivingEntity target) {
        this.setTarget(target);
    }

    @Override
    public boolean hasMeleeAttack() {
        return true;
    }

    @Override
    public boolean hasArms() {
        return false;
    }

    @Override
    public boolean canJump() {
        return false;
    }

    @Override
    public boolean canChant() {
        return false;
    }

    @Override
    public float getExperience() {
        return SorcererGrade.GRADE_2.getRequiredExperience();
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }

    public enum Variant {
        ORANGE,
        YELLOW,
        PURPLE
    }
}
