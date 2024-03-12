package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ai.goal.WaterWalkingFloatGoal;
import radon.jujutsu_kaisen.entity.ai.goal.BetterFollowOwnerGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.ICommandable;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.base.SummonEntity;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class RikaEntity extends SummonEntity implements ICommandable, ISorcerer {
    private static final int DURATION = 5 * 60 * 20;

    public static EntityDataAccessor<Integer> DATA_OPEN = SynchedEntityData.defineId(RikaEntity.class, EntityDataSerializers.INT);
    public static EntityDataAccessor<Integer> DATA_SHOOTING = SynchedEntityData.defineId(RikaEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation SHOOT = RawAnimation.begin().thenPlayAndHold("misc.shoot");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

    public RikaEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public RikaEntity(LivingEntity owner) {
        this(JJKEntities.RIKA.get(), owner.level());

        this.setTame(true);
        this.setOwner(owner);

        Vec3 pos = owner.position()
                .subtract(RotationUtil.getTargetAdjustedLookAngle(owner)
                        .multiply(this.getBbWidth() / 2.0F, 0.0D, this.getBbWidth() / 2.0F));
        this.moveTo(pos.x, pos.y, pos.z);

        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);

        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected float getFlyingSpeed() {
        float speed = super.getFlyingSpeed();

        if (this.getTarget() != null) {
            speed *= 25.0F;
        } else {
            speed *= 5.0F;
        }
        return speed;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.FLYING_SPEED)
                .add(Attributes.MAX_HEALTH, 5 * 20.0D)
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.FOLLOW_RANGE);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        int goal = 1;
        int target = 1;

        this.goalSelector.addGoal(goal++, new WaterWalkingFloatGoal(this));
        this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(goal++, new SorcererGoal(this));
        this.goalSelector.addGoal(goal++, new BetterFollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, true));
        this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(target, new HurtByTargetGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_OPEN, 0);
        this.entityData.define(DATA_SHOOTING, 0);
    }

    public void setOpen(int duration) {
        this.entityData.set(DATA_OPEN, duration);
    }

    public int getOpen() {
        return this.entityData.get(DATA_OPEN);
    }

    public boolean isOpen() {
        return this.getOpen() > 0;
    }

    public void setShooting(int duration) {
        this.entityData.set(DATA_SHOOTING, duration);
    }

    public int getShooting() {
        return this.entityData.get(DATA_SHOOTING);
    }

    public boolean isShooting() {
        return this.getShooting() > 0;
    }

    private PlayState openPredicate(AnimationState<RikaEntity> animationState) {
        if (this.isShooting()) {
            return animationState.setAndContinue(SHOOT);
        }
        return PlayState.STOP;
    }

    private PlayState swingPredicate(AnimationState<RikaEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Idle", state -> state.setAndContinue(IDLE)));
        controllerRegistrar.add(new AnimationController<>(this, "Open", this::openPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.RIKA.get();
    }

    @Override
    public void tick() {
        LivingEntity owner = this.getOwner();

        if (!this.level().isClientSide && owner != null) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasToggled(JJKAbilities.RIKA.get())) {
                this.discard();
                return;
            }
        }

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();

            LivingEntity target = this.getTarget();
            this.setOrderedToSit(target != null && !target.isRemoved() && target.isAlive());

            if (owner != null && this.isOpen()) {
                Vec3 pos = owner.position()
                        .subtract(RotationUtil.getTargetAdjustedLookAngle(owner).multiply(this.getBbWidth(), 0.0D, this.getBbWidth()))
                        .add(RotationUtil.getTargetAdjustedLookAngle(owner).yRot(90.0F).scale(-0.45D));
                this.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

                this.yHeadRot = this.getYRot();
                this.yHeadRotO = this.yHeadRot;
            }

            if (!this.level().isClientSide) {
                int open = this.getOpen();

                if (open > 0) {
                    if (--open == 0) {
                        this.discard();
                    }
                    this.setOpen(open);
                }

                int shooting = this.getShooting();

                if (shooting > 0) {
                    this.setShooting(--shooting);
                }

                if (this.getTime() >= DURATION) {
                    this.discard();
                }
            }
        }
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.SHOOT_PURE_LOVE.get());
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
        return false;
    }

    @Override
    public boolean canChant() {
        return true;
    }

    @Override
    public float getExperience() {
        LivingEntity owner = this.getOwner();

        if (owner == null) return 0.0F;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISorcererData data = cap.getSorcererData();

        return data.getExperience();
    }

    @Override
    public float getMaxEnergy() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return null;
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SHIKIGAMI;
    }

    @Override
    public boolean canChangeTarget() {
        return true;
    }

    @Override
    public void changeTarget(LivingEntity target) {
        this.setTarget(target);
    }
}
