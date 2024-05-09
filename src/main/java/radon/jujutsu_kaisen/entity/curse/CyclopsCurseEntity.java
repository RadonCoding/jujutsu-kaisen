package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.cursed_technique.ICursedTechnique;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.*;

public class CyclopsCurseEntity extends CursedSpirit {
    public static EntityDataAccessor<Integer> DATA_SMASH = SynchedEntityData.defineId(CyclopsCurseEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

    public CyclopsCurseEntity(EntityType<? extends CursedSpirit> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.STEP_HEIGHT, 2.0F);
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
        return true;
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public boolean canChant() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_SMASH, 0);
    }

    private PlayState walkPredicate(AnimationState<CyclopsCurseEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        }
        return PlayState.STOP;
    }

    private PlayState swingPredicate(AnimationState<CyclopsCurseEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk", this::walkPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", 2, this::swingPredicate));
    }

    @Override
    public float getExperience() {
        return SorcererGrade.GRADE_2.getRequiredExperience();
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return JJKCursedTechniques.SHOCKWAVE.get();
    }
}
