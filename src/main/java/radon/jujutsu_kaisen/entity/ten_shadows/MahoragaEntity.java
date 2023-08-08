package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.TenShadowsSummon;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MahoragaEntity extends TenShadowsSummon implements ISorcerer {
    public static EntityDataAccessor<Integer> DATA_SLASH = SynchedEntityData.defineId(MahoragaEntity.class, EntityDataSerializers.INT);
    public static EntityDataAccessor<Boolean> DATA_POSITIVE_SWORD = SynchedEntityData.defineId(MahoragaEntity.class, EntityDataSerializers.BOOLEAN);

    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    public static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    public static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    public static final RawAnimation SLASH = RawAnimation.begin().thenPlay("attack.slash");

    private static final double SWING_LAUNCH = 10.0D;
    private static final float SWING_EXPLOSION = 2.5F;

    private static final int SLASH_DURATION = 20;

    public MahoragaEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.setPathfindingMalus(BlockPathTypes.BLOCKED, 0.0F);
    }

    public MahoragaEntity(LivingEntity owner, boolean tame) {
        super(JJKEntities.MAHORAGA.get(), owner.level);

        this.setTame(tame);
        this.setOwner(owner);

        Vec3 pos = owner.position()
                .subtract(owner.getLookAngle()
                        .multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x(), pos.y(), pos.z());

        this.createGoals();
    }

    private void createGoals() {
        int target = 1;
        int goal = 1;

        this.goalSelector.addGoal(goal++, new FloatGoal(this));
        this.goalSelector.addGoal(goal++, new CustomLeapAtTargetGoal(this, 0.6F));
        this.goalSelector.addGoal(goal++, new SorcererGoal(this));
        this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(goal++, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        this.targetSelector.addGoal(target++, new HurtByTargetGoal(this));

        if (this.isTame()) {
            this.goalSelector.addGoal(goal++, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, false));

            this.targetSelector.addGoal(target++, new OwnerHurtByTargetGoal(this));
            this.targetSelector.addGoal(target, new OwnerHurtTargetGoal(this));
        } else {
            this.targetSelector.addGoal(target, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false,
                    entity -> this.participants.contains(entity.getUUID())));
        }
        this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 5 * 2.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_SLASH, 0);
        this.entityData.define(DATA_POSITIVE_SWORD, false);
    }

    public boolean isPositiveSword() {
        return this.entityData.get(DATA_POSITIVE_SWORD);
    }

    private PlayState walkRunIdlePredicate(AnimationState<MahoragaEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        } else {
            return animationState.setAndContinue(IDLE);
        }
    }

    private PlayState swingPredicate(AnimationState<MahoragaEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private PlayState slashPredicate(AnimationState<MahoragaEntity> animationState) {
        int slash = this.entityData.get(DATA_SLASH);

        if (slash > 0) {
            return animationState.setAndContinue(SLASH);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk/Run/Idle", this::walkRunIdlePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Slash", this::slashPredicate));
    }

    @Override
    public double getAttributeValue(@NotNull Holder<Attribute> pAttribute) {
        double d0 = super.getAttributeValue(pAttribute);

        LivingEntity target = this.getTarget();

        if (target != null) {
            AtomicBoolean result = new AtomicBoolean();

            target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(cap.isCurse()));

            if (result.get() && this.entityData.get(DATA_POSITIVE_SWORD)) {
                d0 *= 3.0D;
            } else if (!result.get() && !this.entityData.get(DATA_POSITIVE_SWORD)) {
                d0 *= 1.5D;
            }
        }
        return d0;
    }

    @Override
    protected void customServerAiStep() {
        LivingEntity target = this.getTarget();

        if (target != null) {
            AtomicBoolean result = new AtomicBoolean();

            target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(cap.isCurse()));
            this.entityData.set(DATA_POSITIVE_SWORD, result.get());
        }

        this.setSprinting(this.moveControl.getSpeedModifier() > 1.0D);

        this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            for (ClosedDomainExpansionEntity domain : HelperMethods.getEntityCollisionsOfClass(ClosedDomainExpansionEntity.class, this.level, this.getBoundingBox())) {
                if (cap.isAdaptedTo(domain.getAbility())) {
                    domain.discard();
                }
            }
        });

        int slash = this.entityData.get(DATA_SLASH);

        if (slash > 0) {
            this.entityData.set(DATA_SLASH, --slash);
        } else {
            if (target != null) {
                if (this.isOnGround() && this.distanceTo(target) < 3.0D) {
                    this.entityData.set(DATA_SLASH, SLASH_DURATION);

                    target.setDeltaMovement(this.getLookAngle().scale(SWING_LAUNCH));

                    Vec3 explosionPos = new Vec3(this.getX(), this.getEyeY() - 0.2D, this.getZ()).add(this.getLookAngle());
                    this.level.explode(this, JJKDamageSources.indirectJujutsuAttack(this, null, null), null, explosionPos, SWING_EXPLOSION, false, Level.ExplosionInteraction.NONE);
                }
            }
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(srcCap -> {
                this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(dstCap -> {
                    dstCap.adaptAll(srcCap.getAdapted());
                });
            });
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(srcCap -> {
                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(dstCap -> {
                    dstCap.adaptAll(srcCap.getAdapted());
                });
            });
        }
    }

    private void breakBlocks() {
        AABB bounds = this.getBoundingBox();

        BlockPos.betweenClosedStream(bounds).forEach(pos -> {
            BlockState state = this.level.getBlockState(pos);

            if (state.getFluidState().isEmpty() && state.canOcclude() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                this.level.destroyBlock(pos, false);
            }
        });
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide) {
            if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                this.breakBlocks();
            }
        }
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public List<Trait> getTraits() {
        return List.of();
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    @Override
    protected Ability getAbility() {
        return JJKAbilities.MAHORAGA.get();
    }

    private static class CustomLeapAtTargetGoal extends Goal {
        private final Mob mob;
        private LivingEntity target;
        private final float yd;

        public CustomLeapAtTargetGoal(Mob pMob, float pYd) {
            this.mob = pMob;
            this.yd = pYd;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.mob.isVehicle()) {
                return false;
            } else {
                this.target = this.mob.getTarget();
                if (this.target == null) {
                    return false;
                } else {
                    double d0 = this.mob.distanceToSqr(this.target);

                    if (!(d0 < 4.0D) && !(d0 > 32.0D)) {
                        if (!this.mob.isOnGround()) {
                            return false;
                        } else {
                            return this.mob.getRandom().nextInt(reducedTickDelay(5)) == 0;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !this.mob.isOnGround();
        }

        @Override
        public void start() {
            Vec3 vec3 = this.mob.getDeltaMovement();
            Vec3 vec31 = new Vec3(this.target.getX() - this.mob.getX(), 0.0D, this.target.getZ() - this.mob.getZ());

            if (vec31.lengthSqr() > 1.0E-7D) {
                vec31 = vec31.normalize().scale(0.4D).add(vec3.scale(0.2D));
            }
            this.mob.setDeltaMovement(vec31.x, this.yd, vec31.z);
        }
    }
}
