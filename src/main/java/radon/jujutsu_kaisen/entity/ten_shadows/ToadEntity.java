package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.ai.goal.BetterFollowOwnerGoal;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.base.TenShadowsSummon;
import radon.jujutsu_kaisen.entity.projectile.ToadTongueProjectile;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class ToadEntity extends TenShadowsSummon {
    private static final EntityDataAccessor<Integer> DATA_RITUAL = SynchedEntityData.defineId(ToadEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_CAN_SHOOT = SynchedEntityData.defineId(ToadEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    private static final RawAnimation TONGUE = RawAnimation.begin().thenPlayAndHold("attack.tongue");
    private static final RawAnimation HOWL = RawAnimation.begin().thenPlayAndHold("misc.howl");

    private static final int RANGE = 20;
    private static final int SHOOT_INTERVAL = 20;

    public ToadEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ToadEntity(EntityType<? extends TamableAnimal> type, LivingEntity owner, boolean tame, boolean ritual) {
        this(type, owner.level());

        this.setTame(tame);
        this.setOwner(owner);

        Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
        Vec3 pos = ritual ? owner.position() : owner.position()
                .subtract(direction.multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;
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
    public boolean canChant() {
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
    protected void registerGoals() {
        int goal = 1;
        int target = 1;

        this.goalSelector.addGoal(goal++, new FloatGoal(this));
        this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 1.1D, true));

        this.targetSelector.addGoal(target++, new HurtByTargetGoal(this));

        if (this.isTame()) {
            this.goalSelector.addGoal(goal++, new BetterFollowOwnerGoal(this, 1.0D, 25.0F, 10.0F, this.canFly()));

            this.targetSelector.addGoal(target++, new OwnerHurtByTargetGoal(this));
            this.targetSelector.addGoal(target, new OwnerHurtTargetGoal(this));
        } else {
            this.targetSelector.addGoal(target, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false,
                    entity -> this.participants.contains(entity.getUUID())));
        }
        this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));
    }

    public boolean hasWings() {
        return false;
    }

    public void setRitual(int duration) {
        this.entityData.set(DATA_RITUAL, duration);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_RITUAL, 0);
        this.entityData.define(DATA_CAN_SHOOT, true);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("ritual", this.entityData.get(DATA_RITUAL));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_RITUAL, pCompound.getInt("ritual"));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.MAX_HEALTH, 10.0D);
    }

    private PlayState walkPredicate(AnimationState<ToadEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(WALK);
        }
        return PlayState.STOP;
    }

    private PlayState swingPredicate(AnimationState<ToadEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private PlayState tonguePredicate(AnimationState<ToadEntity> animationState) {
        if (!this.canShoot()) {
            return animationState.setAndContinue(TONGUE);
        }
        return PlayState.STOP;
    }

    private PlayState howlPredicate(AnimationState<ToadEntity> animationState) {
        if (this.entityData.get(DATA_RITUAL) > 0) {
            return animationState.setAndContinue(HOWL);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk", this::walkPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Tongue", this::tonguePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Howl", this::howlPredicate));
    }

    @Override
    public Summon<?> getAbility() {
        return this.hasWings() ? JJKAbilities.TOAD_FUSION.get() : JJKAbilities.TOAD.get();
    }

    public boolean canShoot() {
        return this.entityData.get(DATA_CAN_SHOOT);
    }

    public void setCanShoot(boolean canShoot) {
        this.entityData.set(DATA_CAN_SHOOT, canShoot);
    }

    @Override
    protected void customServerAiStep() {
        if (!this.canShoot()) {
            this.moveControl.setWantedPosition(this.getX(), this.getY(), this.getZ(), this.getSpeed());
        }
        
        LivingEntity owner = this.getOwner();

        if (this.isTame() && owner != null && this.hasWings()) {
            for (AbstractArrow arrow : this.level().getEntitiesOfClass(AbstractArrow.class, owner.getBoundingBox().inflate(1.0D))) {
                if (arrow.getOwner() != this.getOwner()) {
                    this.shoot(arrow);
                    arrow.discard();
                }
            }
        }

        LivingEntity target = this.getTarget();

        if (target != null && target.isAlive() && !target.isRemoved()) {
            this.lookControl.setLookAt(target, 30.0F, 30.0F);

            if (this.hasLineOfSight(target) && this.distanceTo(target) <= RANGE) {
                if (this.getTime() % SHOOT_INTERVAL == 0) {
                    this.shoot(target);
                }
            }
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

    private void shoot(Entity target) {
        if (!this.canShoot()) return;

        this.lookAt(EntityAnchorArgument.Anchor.EYES, target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D));

        Vec3 start = this.getEyePosition();
        Vec3 end = target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D);
        double d0 = end.x - start.x;
        double d1 = end.y - start.y;
        double d2 = end.z - start.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float yaw = Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * (double) (180.0F / (float) Math.PI)) - 90.0F);
        float pitch = Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * (double) (180.0F / (float) Math.PI))));

        Vec3 speed = calculateViewVector(pitch, yaw).scale(ToadTongueProjectile.SPEED * (this.hasWings() ? 5.0D : 1.0D));

        ToadTongueProjectile tongue = new ToadTongueProjectile(this, RANGE, target.getUUID());
        tongue.setDeltaMovement(speed);
        this.level().addFreshEntity(tongue);

        this.entityData.set(DATA_CAN_SHOOT, false);
    }
}
