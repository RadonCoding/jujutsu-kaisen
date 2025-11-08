package radon.jujutsu_kaisen.entity.ten_shadows;


import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.Summon;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.entity.IControllableFlyingRide;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.sorcerer.SorcererEntity;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.*;

import java.util.List;

public class NueTotalityEntity extends TenShadowsSummon implements PlayerRideable, IControllableFlyingRide {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");
    private static final RawAnimation SWING = RawAnimation.begin().thenLoop("attack.swing");
    private static final RawAnimation FLIGHT_FEET = RawAnimation.begin().thenLoop("misc.flight_feet");
    private static final RawAnimation GRAB_FEET = RawAnimation.begin().thenLoop("misc.grab_feet");

    private boolean jump;

    public NueTotalityEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public NueTotalityEntity(LivingEntity owner) {
        this(JJKEntities.NUE_TOTALITY.get(), owner.level());

        this.setTame(true, false);
        this.setOwner(owner);

        Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
        Vec3 pos = owner.position()
                .subtract(direction.multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.FLYING_SPEED, 2.0F)
                .add(Attributes.MAX_HEALTH, 4 * 20.0D);
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
    public boolean canChant() {
        return false;
    }

    @Override
    public boolean hasMeleeAttack() {
        return false;
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
    protected void customServerAiStep() {
        super.customServerAiStep();

        LivingEntity target = this.getTarget();

        if (target != null && !target.isRemoved() && target.isAlive()) {
            if (this.getY() >= target.getY() + (this.getBbHeight() * 3.0F) && Math.sqrt(this.distanceToSqr(target.getX(), this.getY(), target.getZ())) <= 5.0D) {
                if (this.random.nextInt(5) != 0) return;

                if (AbilityHandler.trigger(this, JJKAbilities.NUE_TOTALITY_LIGHTNING.get()) == Ability.Status.SUCCESS) {
                    this.swing(InteractionHand.MAIN_HAND);
                }
            } else if (!this.isVehicle()) {
                this.moveControl.setWantedPosition(target.getX(), target.getY() + (this.getBbHeight() * 3.0F), target.getZ(), this.getFlyingSpeed());
            }
        }
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        if (super.doHurtTarget(pEntity)) {
            if (pEntity instanceof LivingEntity living) {
                Ability lightning = JJKAbilities.NUE_LIGHTNING.get();
                ((ITenShadowsAttack) lightning).perform(this, living);
            }
            return true;
        }
        return false;
    }

    @Override
    protected float getFlyingSpeed() {
        return this.getSpeed() * 0.5F;
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    private PlayState feetPredicate(AnimationState<NueTotalityEntity> animationState) {
        if (this.isVehicle()) {
            return animationState.setAndContinue(GRAB_FEET);
        }
        return animationState.setAndContinue(FLIGHT_FEET);
    }

    private PlayState swingPredicate(AnimationState<NueTotalityEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private PlayState flyIdlePredicate(AnimationState<NueTotalityEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(FLY);
        }
        return animationState.setAndContinue(IDLE);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Fly/Idle", this::flyIdlePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", 2, this::swingPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Feet", this::feetPredicate));
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.NUE_TOTALITY.get();
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.NUE_TOTALITY_LIGHTNING.get());
    }

    @Override
    protected float getRiddenSpeed(@NotNull Player pPlayer) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (pPlayer == this.getOwner() && this.isTame()) {
            return pPlayer.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public @NotNull Vec3 getPassengerRidingPosition(Entity pEntity) {
        return new Vec3(
                new Vector3f(0.0F, -pEntity.getBbHeight() + 0.8F, 0.0F)
                        .add(0.0F, 0.5F, 0.0F)
                        .rotateY(-this.yBodyRot * (float) (Math.PI / 180.0D)))
                .add(this.position());
    }

    @Override
    public @NotNull EntityDimensions getDefaultDimensions(@NotNull Pose pPose) {
        EntityDimensions dimensions = super.getDefaultDimensions(pPose);

        LivingEntity passenger = this.getControllingPassenger();

        if (passenger != null) {
            return EntityDimensions.fixed(dimensions.width(), dimensions.height() + passenger.getBbHeight());
        }
        return dimensions;
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        AABB bounds = super.makeBoundingBox();

        LivingEntity passenger = this.getControllingPassenger();

        if (passenger != null) {
            return bounds.setMinY(bounds.minY - passenger.getBbHeight() / 2 - 0.4D)
                    .setMaxY(bounds.maxY - passenger.getBbHeight() + 0.4D);
        }
        return bounds;
    }

    @Override
    protected @NotNull Vec3 getRiddenInput(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
        if (this.onGround()) {
            return Vec3.ZERO;
        } else {
            float f = pPlayer.xxa * 0.5F;
            float f1 = pPlayer.zza;

            if (f1 <= 0.0F) {
                f1 *= 0.25F;
            }
            return new Vec3(f, 0.0D, f1);
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
    public boolean isNoGravity() {
        return !this.isVehicle() && super.isNoGravity();
    }

    @Override
    protected void tickRidden(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
        super.tickRidden(pPlayer, pTravelVector);

        Vec2 vec2 = this.getRiddenRotation(pPlayer);
        this.setRot(vec2.y, vec2.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

        Vec3 movement = this.getDeltaMovement();

        if (this.jump) {
            this.setDeltaMovement(movement.add(0.0D, this.getFlyingSpeed(), 0.0D));
        }
    }

    @Override
    public void setJump(boolean jump) {
        this.jump = jump;
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();
    }
}
