package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.UUID;

public class RabbitEscapeEntity extends TenShadowsSummon {
    private static final int COUNT = 16;

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

    @Nullable
    private UUID leaderUUID;
    @Nullable
    private RabbitEscapeEntity cachedLeader;

    private boolean original;

    public RabbitEscapeEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public RabbitEscapeEntity(LivingEntity owner, boolean tame) {
        this(JJKEntities.RABBIT_ESCAPE.get(), owner.level());

        this.setOwner(owner);
        this.setTame(tame);

        Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
        Vec3 pos = owner.position()
                .subtract(direction.multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

        this.setDeltaMovement(look.x * (this.random.nextDouble() * 2.0D + 1.0D) + (this.random.nextDouble() * (this.random.nextBoolean() ? 1 : -1) * 0.5D),
                look.y * (this.random.nextDouble() * 4.0D + 2.0D) + (this.random.nextDouble() * (this.random.nextBoolean() ? 1 : -1) * 0.5D),
                look.z * (this.random.nextDouble() * 2.0D + 1.0D) + (this.random.nextDouble() * (this.random.nextBoolean() ? 1 : -1) * 0.5D));

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;
    }

    public RabbitEscapeEntity(RabbitEscapeEntity leader) {
        this(leader, leader.isTame());

        this.setLeader(leader);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity pTarget) {
        if (this.getLeader() == pTarget || pTarget instanceof RabbitEscapeEntity rabbit && rabbit.getLeader() == pTarget) {
            return false;
        }
        return super.canAttack(pTarget);
    }

    @Override
    protected void customServerAiStep() {
        RabbitEscapeEntity leader = this.getLeader();

        if (!this.original) {
            if (leader == null || leader.isRemoved() || !leader.isAlive()) {
                this.discard();
                return;
            }
        }

        if (this.getTarget() != null) {
            double d0 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
            AABB bounds = AABB.unitCubeFromLowerCorner(this.position()).inflate(d0, 10.0D, d0);
            this.level().getEntitiesOfClass(RabbitEscapeEntity.class, bounds, EntitySelector.NO_SPECTATORS).stream()
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

    public void setLeader(@Nullable RabbitEscapeEntity leader) {
        if (leader != null) {
            this.leaderUUID = leader.getUUID();
            this.cachedLeader = leader;
        }
    }

    @Nullable
    public RabbitEscapeEntity getLeader() {
        if (this.cachedLeader != null && !this.cachedLeader.isRemoved()) {
            return this.cachedLeader;
        } else if (this.leaderUUID != null && this.level() instanceof ServerLevel) {
            this.cachedLeader = (RabbitEscapeEntity) ((ServerLevel) this.level()).getEntity(this.leaderUUID);
            return this.cachedLeader;
        } else {
            return null;
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.leaderUUID != null) {
            pCompound.putUUID("leader", this.leaderUUID);
        }
        pCompound.putBoolean("original", this.original);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("leader")) {
            this.leaderUUID = pCompound.getUUID("leader");
        }
        this.original = pCompound.getBoolean("original");
    }

    @Override
    protected boolean shouldDespawn() {
        return this.original && super.shouldDespawn();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        if (this.getLeader() == null) {
            this.original = true;

            for (int i = 0; i < COUNT; i++) {
                RabbitEscapeEntity entity = new RabbitEscapeEntity(this);
                entity.setPos(this.position());
                this.level().addFreshEntity(entity);
            }
        }
    }

    private PlayState walkPredicate(AnimationState<RabbitEscapeEntity> animationState) {
        if (animationState.isMoving()) {
            return animationState.setAndContinue(WALK);
        }
        return PlayState.STOP;
    }

    private PlayState swingPredicate(AnimationState<RabbitEscapeEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(SWING);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Walk", this::walkPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Swing", this::swingPredicate));
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
    public boolean canChant() {
        return false;
    }

    @Override
    protected void doPush(@NotNull Entity p_20971_) {

    }

    @Override
    public float getExperience() {
        return this.isTame() ? super.getExperience() : SorcererGrade.GRADE_4.getRequiredExperience();
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.RABBIT_ESCAPE.get();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 2 * 0.33D)
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }
}
