package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.ai.goal.WaterWalkingFloatGoal;
import radon.jujutsu_kaisen.entity.ai.goal.BetterFollowOwnerGoal;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class DivineDogEntity extends TenShadowsSummon implements PlayerRideable {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(DivineDogEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_LEAP = SynchedEntityData.defineId(DivineDogEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_RITUAL = SynchedEntityData.defineId(DivineDogEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation LEAP = RawAnimation.begin().thenPlay("attack.leap");
    private static final RawAnimation HOWL = RawAnimation.begin().thenPlayAndHold("misc.howl");

    private static final int LEAP_DURATION = 10;

    public DivineDogEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public DivineDogEntity(EntityType<? extends TamableAnimal> type, LivingEntity owner, boolean ritual, Variant variant) {
        super(type, owner.level());

        this.setTame(true);
        this.setOwner(owner);

        Vec3 pos = ritual ? owner.position() : owner.position()
                .subtract(RotationUtil.getTargetAdjustedLookAngle(owner).multiply(this.getBbWidth(), 0.0D, this.getBbWidth()))
                .add(RotationUtil.getTargetAdjustedLookAngle(owner).yRot(90.0F).scale(this.getVariant() == Variant.WHITE ? -0.45D : 0.45D));
        this.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.setVariant(variant);
    }

    @Override
    public boolean isInvulnerable() {
        return this.entityData.get(DATA_RITUAL) > 0 || super.isInvulnerable();
    }

    @Override
    protected boolean isCustom() {
        return true;
    }

    @Override
    protected boolean canFly() {
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

    protected void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.ordinal());
    }

    public void setRitual(int index, int duration) {
        this.setNoAi(true);
        this.entityData.set(DATA_RITUAL, duration);

        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        double distance = this.getBbWidth() * 2;
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(this);
        Vec3 up = new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 side = look.cross(up);
        Vec3 offset = side.scale(distance * (index < 3 ? 1 : -1))
                .add(look.scale(1.5D + (index % 3) * 3.0D));
        this.setPos(x + offset.x, y, z + offset.z);

        float yRot = this.getYRot();

        if (index < 3) {
            yRot -= 90.0F;
        } else {
            yRot += 90.0F;
        }
        this.setYRot(yRot);
        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (pPlayer == this.getOwner() && !this.isVehicle()) {
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
        return 1.0F;
    }

    @Override
    protected boolean shouldToggleOnDeath() {
        return false;
    }

    @Override
    protected void registerGoals() {
        int goal = 1;
        int target = 1;

        this.goalSelector.addGoal(goal++, new WaterWalkingFloatGoal(this));
        this.goalSelector.addGoal(goal++, new CustomLeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(goal++, new BetterFollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, false));
        this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(target++, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(target++, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(target, new OwnerHurtTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.MAX_HEALTH, 2 * 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_VARIANT, Variant.WHITE.ordinal());
        this.entityData.define(DATA_LEAP, 0);
        this.entityData.define(DATA_RITUAL, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("variant", this.entityData.get(DATA_VARIANT));
        pCompound.putInt("ritual", this.entityData.get(DATA_RITUAL));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_VARIANT, pCompound.getInt("variant"));
        this.entityData.set(DATA_RITUAL, pCompound.getInt("ritual"));
    }

    private PlayState walkRunIdlePredicate(AnimationState<DivineDogEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        } else {
            return animationState.setAndContinue(IDLE);
        }
    }

    private PlayState leapPredicate(AnimationState<DivineDogEntity> animationState) {
        if (this.entityData.get(DATA_LEAP) > 0) {
            return animationState.setAndContinue(LEAP);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private PlayState howlPredicate(AnimationState<DivineDogEntity> animationState) {
        if (this.entityData.get(DATA_RITUAL) > 0) {
            return animationState.setAndContinue(HOWL);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run/Idle", this::walkRunIdlePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Leap", this::leapPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Howl", this::howlPredicate));
    }

    public Variant getVariant() {
        return Variant.values()[this.entityData.get(DATA_VARIANT)];
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.DIVINE_DOGS.get();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        int leap = this.entityData.get(DATA_LEAP);

        if (leap > 0) {
            this.entityData.set(DATA_LEAP, --leap);
        }
    }

    @Override
    public void tick() {
        int ritual = this.entityData.get(DATA_RITUAL);

        if (ritual > 0) {
            this.entityData.set(DATA_RITUAL, --ritual);

            if (ritual == 0) {
                this.discard();
            }
        } else {
            super.tick();
        }
    }

    public enum Variant {
        WHITE,
        BLACK
    }

    private class CustomLeapAtTargetGoal extends LeapAtTargetGoal {
        public CustomLeapAtTargetGoal(Mob pMob, float pYd) {
            super(pMob, pYd);
        }

        @Override
        public void start() {
            super.start();

            DivineDogEntity.this.entityData.set(DATA_LEAP, LEAP_DURATION);
        }
    }
}
