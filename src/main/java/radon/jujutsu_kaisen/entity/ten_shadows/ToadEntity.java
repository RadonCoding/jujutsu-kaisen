package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.TenShadowsSummon;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.projectile.ToadTongueProjectile;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class ToadEntity extends TenShadowsSummon implements RangedAttackMob {
    private static final EntityDataAccessor<Integer> DATA_TONGUE = SynchedEntityData.defineId(ToadEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_RITUAL = SynchedEntityData.defineId(ToadEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation TONGUE = RawAnimation.begin().thenLoop("attack.tongue");
    private static final RawAnimation HOWL = RawAnimation.begin().thenPlayAndHold("misc.howl");

    private static final int TONGUE_DURATION = 10;
    private static final int PULL_INTERVAL = 20;
    private static final int RANGE = 20;

    public ToadEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ToadEntity(LivingEntity owner, boolean ritual) {
        super(JJKEntities.TOAD.get(), owner.level);

        this.setTame(true);
        this.setOwner(owner);

        Vec3 pos = ritual ? owner.position() : owner.position()
                .subtract(owner.getLookAngle().multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;
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
                .add(look.scale((index % 3) * 3.0D));
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
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_TONGUE, 0);
        this.entityData.define(DATA_RITUAL, 0);
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
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.MAX_HEALTH, 20.0D);
    }

    @Override
    protected void registerGoals() {
        int goal = 1;

        this.goalSelector.addGoal(goal++, new FloatGoal(this));
        this.goalSelector.addGoal(goal++, new SorcererGoal(this));
        this.goalSelector.addGoal(goal++, new RangedAttackGoal(this, 1.0D, PULL_INTERVAL, RANGE));
        this.goalSelector.addGoal(goal++, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(goal++, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, false));
        this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));
    }

    private PlayState walkPredicate(AnimationState<ToadEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(WALK);
        }
        return PlayState.STOP;
    }

    private PlayState tonguePredicate(AnimationState<ToadEntity> animationState) {
        int slash = this.entityData.get(DATA_TONGUE);

        if (slash > 0) {
            return animationState.setAndContinue(TONGUE);
        }
        animationState.getController().forceAnimationReset();
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
        controllerRegistrar.add(new AnimationController<>(this, "Tongue", this::tonguePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Howl", this::howlPredicate));
    }

    @Override
    protected Ability getAbility() {
        return JJKAbilities.TOAD.get();
    }

    @Override
    protected void customServerAiStep() {
        int tongue = this.entityData.get(DATA_TONGUE);

        if (tongue > 0) {
            this.entityData.set(DATA_TONGUE, --tongue);
        }

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            if (this.hasLineOfSight(owner) && this.distanceTo(owner) <= RANGE && owner.tickCount - owner.getLastHurtByMobTimestamp() < 20) {
                this.lookControl.setLookAt(owner, 30.0F, 30.0F);
                this.shoot(owner);
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

    private void shoot(LivingEntity target) {
        this.entityData.set(DATA_TONGUE, TONGUE_DURATION);

        ToadTongueProjectile tongue = new ToadTongueProjectile(this, RANGE);
        double d0 = target.getX() - tongue.getX();
        double d1 = target.getY() + (target.getBbHeight() / 2.0F) - tongue.getY();
        double d2 = target.getZ() - tongue.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        tongue.shootFromRotation(this, Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * (double) (180.0F / Mth.PI)))),
                Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * (double) (180.0F / Mth.PI)) - 90.0F),
                0.0F, ToadTongueProjectile.SPEED, 0.0F);
        this.level.addFreshEntity(tongue);
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity pTarget, float pVelocity) {
        this.shoot(pTarget);

        this.setTarget(null);
    }
}
