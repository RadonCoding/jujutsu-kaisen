package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.ai.goal.BetterFollowOwnerGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.entity.projectile.ToadTongueProjectile;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.UUID;

public class ToadEntity extends TenShadowsSummon {

    private static final EntityDataAccessor<Integer> DATA_RITUAL = SynchedEntityData.defineId(ToadEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_CAN_SHOOT = SynchedEntityData.defineId(ToadEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    private static final RawAnimation TONGUE = RawAnimation.begin().thenPlayAndHold("attack.tongue");
    private static final RawAnimation HOWL = RawAnimation.begin().thenPlayAndHold("misc.howl");

    private static final int COUNT = 3;
    private static final int RANGE = 20;
    private static final int SHOOT_INTERVAL = 20;

    @Nullable
    private UUID leaderUUID;
    @Nullable
    private ToadEntity cachedLeader;

    private boolean original;

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

    public ToadEntity(EntityType<? extends TamableAnimal> type, ToadEntity leader, LivingEntity owner) {
        this(type, owner, leader.isTame(), false);

        this.setLeader(leader);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity pTarget) {
        if (this.getLeader() == pTarget || pTarget instanceof ToadEntity toad && toad.getLeader() == pTarget) {
            return false;
        }
        return super.canAttack(pTarget);
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

        ToadEntity leader = this.getLeader();

        if (!this.original) {
            if (leader == null || leader.isRemoved() || !leader.isAlive()) {
                this.discard();
                return;
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

            double d0 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
            AABB bounds = AABB.unitCubeFromLowerCorner(this.position()).inflate(d0, 10.0D, d0);
            this.level().getEntitiesOfClass(ToadEntity.class, bounds, EntitySelector.NO_SPECTATORS).stream()
                    .filter(entity -> entity != this)
                    .filter(entity -> entity.getTarget() == null)
                    .filter(entity -> !entity.isAlliedTo(this.getTarget()))
                    .filter(entity -> (entity.getLeader() != null && entity.getLeader() == this.getLeader()) || this.getLeader() == entity || entity.getLeader() == this)
                    .forEach(entity -> entity.setTarget(this.getTarget()));
        } else if (leader != null) {
            if (this.distanceTo(leader) >= 1.0D) {
                this.lookControl.setLookAt(leader, 10.0F, (float) this.getMaxHeadXRot());
                this.navigation.moveTo(leader, 1.0D);
            }
        }
    }

    public void setLeader(@Nullable ToadEntity leader) {
        if (leader != null) {
            this.leaderUUID = leader.getUUID();
            this.cachedLeader = leader;
        }
    }

    @Nullable
    public ToadEntity getLeader() {
        if (this.cachedLeader != null && !this.cachedLeader.isRemoved()) {
            return this.cachedLeader;
        } else if (this.leaderUUID != null && this.level() instanceof ServerLevel) {
            this.cachedLeader = (ToadEntity) ((ServerLevel) this.level()).getEntity(this.leaderUUID);
            return this.cachedLeader;
        } else {
            return null;
        }
    }

    @Override
    protected boolean shouldDespawn() {
        return this.original && super.shouldDespawn();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (this.getRitual() > 0) return;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        if (this.getLeader() == null) {
            this.original = true;

            for (int i = 0; i < COUNT; i++) {
                @SuppressWarnings("unchecked")
                ToadEntity entity = new ToadEntity((EntityType<? extends TamableAnimal>) this.getType(), this, owner);
                entity.setPos(this.position());
                this.level().addFreshEntity(entity);
            }
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return this.getRitual() == 0 && super.hurt(pSource, pAmount);
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
        return false;
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
        this.goalSelector.addGoal(goal++, new SorcererGoal(this));

        this.targetSelector.addGoal(target++, new HurtByTargetGoal(this));

        if (this.isTame()) {
            this.goalSelector.addGoal(goal++, new BetterFollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, this.canFly()));

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

    public int getRitual() {
        return this.entityData.get(DATA_RITUAL);
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

        if (this.leaderUUID != null) {
            pCompound.putUUID("leader", this.leaderUUID);
        }
        pCompound.putBoolean("original", this.original);

        pCompound.putInt("ritual", this.getRitual());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("leader")) {
            this.leaderUUID = pCompound.getUUID("leader");
        }
        this.original = pCompound.getBoolean("original");

        this.setRitual(pCompound.getInt("ritual"));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
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
        if (this.getRitual() > 0) {
            return animationState.setAndContinue(HOWL);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk", this::walkPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", 2, this::swingPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Tongue", this::tonguePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Howl", this::howlPredicate));
    }

    @Override
    public float getExperience() {
        return this.isTame() ? super.getExperience() : SorcererGrade.GRADE_4.getRequiredExperience();
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
    public void tick() {
        int ritual = this.getRitual();

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
        float yaw = Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * (double) (180.0F / Mth.PI)) - 90.0F);
        float pitch = Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * (double) (180.0F / Mth.PI))));

        Vec3 speed = calculateViewVector(pitch, yaw).scale(ToadTongueProjectile.SPEED * (this.hasWings() ? 5.0D : 1.0D));

        ToadTongueProjectile tongue = new ToadTongueProjectile(this, RANGE, target.getUUID());
        tongue.setDeltaMovement(speed);
        this.level().addFreshEntity(tongue);

        this.entityData.set(DATA_CAN_SHOOT, false);
    }
}
