package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.base.IRightClickInputListener;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class DinoCurseEntity extends CursedSpirit implements PlayerRideable, IRightClickInputListener {
    private static final EntityDataAccessor<Boolean> DATA_SHOOTING = SynchedEntityData.defineId(DinoCurseEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    private static final RawAnimation SHOOT = RawAnimation.begin().thenLoop("attack.shoot");

    private int riding;

    public DinoCurseEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
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
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return null;
    }

    @Override
    protected float ridingOffset(@NotNull Entity pEntity) {
        return this.getBbHeight();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_SHOOTING, false);
    }

    private boolean isShooting() {
        return this.entityData.get(DATA_SHOOTING);
    }

    private void setShooting(boolean shooting) {
        this.entityData.set(DATA_SHOOTING, shooting);
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (pPlayer == this.getOwner() && this.isTame() && !this.isVehicle()) {
            this.riding = this.tickCount;

            if (pPlayer.startRiding(this)) {
                pPlayer.setYRot(this.getYRot());
                pPlayer.setXRot(this.getXRot());
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();

        if (entity instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    private Vec2 getRiddenRotation(LivingEntity pEntity) {
        return new Vec2(pEntity.getXRot() * 0.5F, pEntity.getYRot());
    }

    @Override
    protected float getRiddenSpeed(@NotNull Player pPlayer) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.5F;
    }

    @Override
    protected void tickRidden(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
        super.tickRidden(pPlayer, pTravelVector);

        Vec2 vec2 = this.getRiddenRotation(pPlayer);
        this.setRot(vec2.y, vec2.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.yHeadRotO = this.getYRot();
    }

    @Override
    protected @NotNull Vec3 getRiddenInput(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
        float f = pPlayer.xxa * 0.5F;
        float f1 = pPlayer.zza;

        if (f1 <= 0.0F) {
            f1 *= 0.25F;
        }
        return new Vec3(f, 0.0D, f1);
    }

    @Override
    public float getStepHeight() {
        return 2.0F;
    }

    private PlayState walkRunIdlePredicate(AnimationState<DinoCurseEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        } else {
            return animationState.setAndContinue(IDLE);
        }
    }

    private PlayState swingPredicate(AnimationState<DinoCurseEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private PlayState shootPredicate(AnimationState<DinoCurseEntity> animationState) {
        if (this.isShooting()) {
            return animationState.setAndContinue(SHOOT);
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run/Idle", this::walkRunIdlePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Shoot", this::shootPredicate));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        IJujutsuCapability cap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        this.setShooting(data.isChanneling(JJKAbilities.BLUE_FIRE.get()));
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.BLUE_FIRE.get());
    }
    
    @Override
    public void setDown(boolean down) {
        if (this.level().isClientSide) return;
        if (this.tickCount - this.riding < 20) return;

        IJujutsuCapability cap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        boolean channelling = data.isChanneling(JJKAbilities.BLUE_FIRE.get());

        if (down) {
            if (!channelling) {
                AbilityHandler.trigger(this, JJKAbilities.BLUE_FIRE.get());
            }
        } else {
            if (channelling) {
                AbilityHandler.untrigger(this, JJKAbilities.BLUE_FIRE.get());
            }
        }
    }
}
