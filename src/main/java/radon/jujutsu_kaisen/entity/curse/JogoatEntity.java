package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class JogoatEntity extends CursedSpirit {
    public static EntityDataAccessor<Integer> DATA_SMASH = SynchedEntityData.defineId(JogoatEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    private static final RawAnimation SMASH = RawAnimation.begin().thenPlay("attack.smash");

    private static final double SMASH_LAUNCH = 5.0D;
    private static final float SMASH_EXPLOSION = 2.5F;

    private static final int SMASH_DURATION = 20;

    public JogoatEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_SMASH, 0);
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
        return true;
    }

    @Override
    public float getExperience() {
        return 1000000.0F;
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of(Trait.SIX_EYES);
    }

    @Override
    public float getMaxEnergy() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public @Nullable CursedEnergyNature getNature() {
        return CursedEnergyNature.LIGHTNING;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        LivingEntity target = this.getTarget();

        int slash = this.entityData.get(DATA_SMASH);

        if (slash > 0) {
            this.entityData.set(DATA_SMASH, --slash);
        } else {
            if (target != null) {
                if (this.onGround() && this.distanceTo(target) < 3.0D) {
                    this.entityData.set(DATA_SMASH, SMASH_DURATION);

                    target.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(this).scale(SMASH_LAUNCH));
                    target.hurtMarked = true;

                    Vec3 explosionPos = new Vec3(this.getX(), this.getEyeY() - 0.2D, this.getZ()).add(RotationUtil.getTargetAdjustedLookAngle(this));
                    this.level().explode(this, explosionPos.x, explosionPos.y, explosionPos.z, SMASH_EXPLOSION, false, Level.ExplosionInteraction.NONE);
                }
            }
        }
    }

    private PlayState walkRunPredicate(AnimationState<JogoatEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        }
        return PlayState.STOP;
    }

    private PlayState swingPredicate(AnimationState<JogoatEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private PlayState smashPredicate(AnimationState<JogoatEntity> animationState) {
        int smash = this.entityData.get(DATA_SMASH);

        if (smash > 0) {
            return animationState.setAndContinue(SMASH);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run", this::walkRunPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", 2, this::swingPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Smash", this::smashPredicate));
    }
}
