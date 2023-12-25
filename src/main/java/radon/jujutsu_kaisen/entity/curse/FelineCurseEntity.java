package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ai.goal.*;
import radon.jujutsu_kaisen.entity.base.PackCursedSpirit;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class FelineCurseEntity extends PackCursedSpirit {
    private static final EntityDataAccessor<Integer> DATA_LEAP = SynchedEntityData.defineId(FelineCurseEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation LEAP = RawAnimation.begin().thenPlay("attack.leap");

    private static final int LEAP_DURATION = 10;

    public FelineCurseEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public FelineCurseEntity(FelineCurseEntity leader) {
        this(JJKEntities.FELINE_CURSE.get(), leader.level());

        this.setLeader(leader);
    }

    @Override
    protected int getMinCount() {
        return 2;
    }

    @Override
    protected int getMaxCount() {
        return 6;
    }

    @Override
    protected PackCursedSpirit spawn() {
        return new FelineCurseEntity(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_LEAP, 0);
    }

    @Override
    protected void registerGoals() {
        int target = 1;
        int goal = 1;

        this.goalSelector.addGoal(goal++, new WaterWalkingFloatGoal(this));

        if (this.hasMeleeAttack()) {
            this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 1.1D, true));
        }
        this.goalSelector.addGoal(goal++, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(goal++, new CustomLeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(goal++, this.canPerformSorcery() || !this.getCustom().isEmpty() ? new SorcererGoal(this) : new HealingGoal(this));
        this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(target++, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(target++, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
        this.targetSelector.addGoal(target++, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));

        if (this.targetsSorcerers()) {
            this.targetSelector.addGoal(target++, new NearestAttackableSorcererGoal(this, true));
        }
        if (this.targetsCurses()) {
            this.targetSelector.addGoal(target, new NearestAttackableCurseGoal(this, true));
        }
    }

    @Override
    protected boolean isCustom() {
        return true;
    }

    @Override
    public boolean canPerformSorcery() {
        return false;
    }

    @Override
    public float getExperience() {
        return SorcererGrade.GRADE_3.getRequiredExperience();
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    private PlayState walkRunIdlePredicate(AnimationState<FelineCurseEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        } else {
            return animationState.setAndContinue(IDLE);
        }
    }

    private PlayState leapPredicate(AnimationState<FelineCurseEntity> animationState) {
        if (this.entityData.get(DATA_LEAP) > 0) {
            return animationState.setAndContinue(LEAP);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run/Idle", this::walkRunIdlePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Leap", this::leapPredicate));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        this.setSprinting(this.getDeltaMovement().lengthSqr() > 0.01D && this.moveControl.getSpeedModifier() > 1.0D);

        int leap = this.entityData.get(DATA_LEAP);

        if (leap > 0) {
            this.entityData.set(DATA_LEAP, --leap);
        }
    }

    @Override
    public float getStepHeight() {
        return 1.0F;
    }

    private class CustomLeapAtTargetGoal extends LeapAtTargetGoal {
        public CustomLeapAtTargetGoal(Mob pMob, float pYd) {
            super(pMob, pYd);
        }

        @Override
        public void start() {
            super.start();

            FelineCurseEntity.this.entityData.set(DATA_LEAP, LEAP_DURATION);
        }
    }
}
