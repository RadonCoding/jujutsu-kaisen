package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.misc.Summon;
import radon.jujutsu_kaisen.entity.TenShadowsSummon;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class DivineDogEntity extends TenShadowsSummon {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(DivineDogEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_LEAP = SynchedEntityData.defineId(DivineDogEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_RITUAL = SynchedEntityData.defineId(DivineDogEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation LEAP = RawAnimation.begin().thenPlay("attack.leap");
    private static final RawAnimation HOWL = RawAnimation.begin().thenPlayAndHold("misc.howl");

    private static final int LEAP_DURATION = 10;

    public DivineDogEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DivineDogEntity(EntityType<? extends TamableAnimal> type, LivingEntity owner, Variant variant, boolean ritual) {
        super(type, owner.level);

        this.setTame(true);
        this.setOwner(owner);

        Vec3 pos = ritual ? owner.position() : owner.position()
                .subtract(owner.getLookAngle().multiply(this.getBbWidth(), 0.0D, this.getBbWidth()))
                .add(owner.getLookAngle().yRot(90.0F).scale(variant == Variant.WHITE ? -0.45D : 0.45D));
        this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.entityData.set(DATA_VARIANT, variant.ordinal());
    }

    public void setRitual(int index, int duration) {
        this.setNoAi(true);
        this.entityData.set(DATA_RITUAL, duration);

        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        double distance = this.getBbWidth() * 2;
        Vec3 look = this.getLookAngle();
        Vec3 up = new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 side = look.cross(up);
        Vec3 offset = side.scale(distance * (index < 3 ? 1 : -1))
                .add(look.scale(1.5D + (index % 3) * 3.0D));
        this.setPos(x + offset.x(), y, z + offset.z());

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
    protected boolean shouldToggleOnDeath() {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new CustomLeapAtTargetGoal(this, 0.6F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.4D, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, false));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.MAX_HEALTH, 4 * 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 2 * 2.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_VARIANT, -1);
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
        this.setSprinting(this.moveControl.getSpeedModifier() > 1.0D);

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
