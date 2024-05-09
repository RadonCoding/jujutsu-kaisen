package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.cursed_technique.ICursedTechnique;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.*;

public class RugbyFieldCurseEntity extends CursedSpirit implements PlayerRideable {
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

    public RugbyFieldCurseEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    @Override
    protected boolean isCustom() {
        return false;
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
        return SorcererGrade.SEMI_GRADE_2.getRequiredExperience();
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return null;
    }

    private PlayState walkRunPredicate(AnimationState<RugbyFieldCurseEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        }
        return PlayState.STOP;
    }

    private PlayState swingPredicate(AnimationState<RugbyFieldCurseEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run", this::walkRunPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", 2, this::swingPredicate));
    }
}
