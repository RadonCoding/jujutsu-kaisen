package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.ai.max_elephant.Water;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.IRightClickInputListener;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class MaxElephantEntity extends TenShadowsSummon implements PlayerRideable, IRightClickInputListener {
    private static final double EXPLOSION_FALL_DISTANCE = 10.0D;
    private static final int EXPLOSION_DURATION = 20;
    private static final float EXPLOSION_POWER = 10.0F;

    private static final EntityDataAccessor<Boolean> DATA_SHOOTING = SynchedEntityData.defineId(MaxElephantEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
    private static final RawAnimation SHOOT = RawAnimation.begin().thenLoop("attack.shoot");

    private int riding;

    public MaxElephantEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected boolean canFly() {
        return false;
    }

    @Override
    public boolean canPerformSorcery() {
        return false;
    }

    @Override
    protected boolean hasMeleeAttack() {
        return true;
    }

    public MaxElephantEntity(LivingEntity owner, boolean tame) {
        this(JJKEntities.MAX_ELEPHANT.get(), owner.level());

        this.setTame(tame);
        this.setOwner(owner);

        Vec3 pos = owner.position()
                .subtract(owner.getLookAngle()
                        .multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight();
    }

    private void breakBlocks() {
        AABB bounds = this.getBoundingBox();

        BlockPos.betweenClosedStream(bounds).forEach(pos -> {
            BlockState state = this.level().getBlockState(pos);

            if (state.getFluidState().isEmpty() && state.canOcclude() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                this.level().destroyBlock(pos, false);
            }
        });
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_SHOOTING, false);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
        boolean result = super.causeFallDamage(pFallDistance, pMultiplier, pSource);

        if (result && pFallDistance >= EXPLOSION_FALL_DISTANCE) {
            ExplosionHandler.spawn(this.level().dimension(), this.blockPosition(), EXPLOSION_POWER, EXPLOSION_DURATION, this, null);
        }
        return result;
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
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 2.0F;
    }

    @Override
    protected void tickRidden(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
        super.tickRidden(pPlayer, pTravelVector);

        Vec2 vec2 = this.getRiddenRotation(pPlayer);
        this.setRot(vec2.y, vec2.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.yHeadRotO = this.getYRot();
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                this.breakBlocks();
            }
        }
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

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 3 * 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 3 * 2.0D);
    }

    private PlayState walkRunIdlePredicate(AnimationState<MaxElephantEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(this.isSprinting() ? RUN : WALK);
        } else {
            return animationState.setAndContinue(IDLE);
        }
    }

    private PlayState swingPredicate(AnimationState<MaxElephantEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private PlayState shootPredicate(AnimationState<MaxElephantEntity> animationState) {
        if (this.entityData.get(DATA_SHOOTING)) {
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
        this.entityData.set(DATA_SHOOTING, JJKAbilities.isChanneling(this, JJKAbilities.WATER.get()));

        LivingEntity passenger = this.getControllingPassenger();

        if (passenger != null) {
            this.setSprinting(passenger.getDeltaMovement().lengthSqr() > 0.0D);
        } else {
            this.setSprinting(this.getDeltaMovement().lengthSqr() > 0.01D && this.moveControl.getSpeedModifier() > 1.0D);
        }

        if (passenger == null) {
            LivingEntity target = this.getTarget();

            boolean trigger = target != null && this.distanceTo(target) <= Water.RANGE && this.hasLineOfSight(target);

            if (trigger) {
                if (!JJKAbilities.isChanneling(this, JJKAbilities.WATER.get())) {
                    AbilityHandler.trigger(this, JJKAbilities.WATER.get());
                }
            } else if (JJKAbilities.isChanneling(this, JJKAbilities.WATER.get())) {
                AbilityHandler.trigger(this, JJKAbilities.WATER.get());
            }
        }
    }

    @Override
    public @NotNull List<Ability> getCustom() {
        return List.of(JJKAbilities.WATER.get());
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.MAX_ELEPHANT.get();
    }

    @Override
    public void setDown(boolean down) {
        if (this.level().isClientSide) return;
        if (this.tickCount - this.riding < 20) return;

        boolean channelling = JJKAbilities.isChanneling(this, JJKAbilities.WATER.get());

        if (down) {
            if (!channelling) {
                AbilityHandler.trigger(this, JJKAbilities.WATER.get());
            }
        } else {
            if (channelling) {
                AbilityHandler.trigger(this, JJKAbilities.WATER.get());
            }
        }
    }
}
