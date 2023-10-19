package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.ai.goal.*;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class RugbyFieldCurseEntity extends CursedSpirit {
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");

    public RugbyFieldCurseEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        int target = 1;
        int goal = 1;

        this.goalSelector.addGoal(goal++, new BetterFloatGoal(this));

        if (this.hasMeleeAttack()) {
            this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 1.2D, true));
        }
        this.goalSelector.addGoal(goal++, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(goal++, this.canPerformSorcery() ? new SorcererGoal(this) : new HealingGoal(this));

        this.targetSelector.addGoal(target++, new HurtByTargetGoal(this));

        if (this.isTame()) {
            this.goalSelector.addGoal(goal++, new BetterFollowOwnerGoal(this, 1.0D, 25.0F, 10.0F, this.canFly()));

            this.targetSelector.addGoal(target++, new OwnerHurtByTargetGoal(this));
            this.targetSelector.addGoal(target, new OwnerHurtTargetGoal(this));
        } else {
            this.targetSelector.addGoal(target++, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
            this.targetSelector.addGoal(target++, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
            this.targetSelector.addGoal(target, new NearestAttackableSorcererGoal(this, true));
        }
        this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected boolean canFly() {
        return false;
    }

    @Override
    protected boolean canPerformSorcery() {
        return false;
    }

    @Override
    protected boolean hasMeleeAttack() {
        return true;
    }

    @Override
    public @NotNull SorcererGrade getGrade() {
        return SorcererGrade.SEMI_GRADE_2;
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    private PlayState walkRunPredicate(AnimationState<RugbyFieldCurseEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run", this::walkRunPredicate));
    }

    @Override
    protected void customServerAiStep() {
        this.setSprinting(this.getDeltaMovement().lengthSqr() > 0.01D && this.moveControl.getSpeedModifier() > 1.0D);
    }

    @Override
    public float getStepHeight() {
        return 1.0F;
    }
}
