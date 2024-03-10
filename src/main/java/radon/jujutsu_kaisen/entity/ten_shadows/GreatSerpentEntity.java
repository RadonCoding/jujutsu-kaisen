package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class GreatSerpentEntity extends TenShadowsSummon {
    private static final EntityDataAccessor<Boolean> DATA_GRABBING = SynchedEntityData.defineId(GreatSerpentEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation BITE = RawAnimation.begin().thenPlay("attack.bite");
    private static final RawAnimation GRAB = RawAnimation.begin().thenPlayAndHold("misc.grab");

    private static final int MAX_SEGMENTS = 24;

    private final GreatSerpentSegmentEntity[] segments;

    @Nullable
    private LivingEntity target;

    public GreatSerpentEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);

        this.segments = new GreatSerpentSegmentEntity[MAX_SEGMENTS];

        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i] = new GreatSerpentSegmentEntity(this);
        }
        this.setId(ENTITY_COUNTER.getAndAdd(this.segments.length + 1) + 1);
    }

    public GreatSerpentEntity(LivingEntity owner, boolean tame) {
        this(JJKEntities.GREAT_SERPENT.get(), owner.level());

        this.setTame(tame);
        this.setOwner(owner);

        Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
        Vec3 pos = owner.position()
                .subtract(direction.multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());

        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yHeadRot;

        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_GRABBING, false);
    }

    private boolean isGrabbing() {
        return this.entityData.get(DATA_GRABBING);
    }

    private void setGrabbing(boolean grabbing) {
        this.entityData.set(DATA_GRABBING, grabbing);
    }

    public void grab(LivingEntity target) {
        this.target = target;
        this.setGrabbing(false);
    }

    @Override
    public void setId(int id) {
        super.setId(id);

        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i].setId(id + i + 1);
        }
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

    private PlayState bitePredicate(AnimationState<GreatSerpentEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(BITE);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private PlayState grabPredicate(AnimationState<GreatSerpentEntity> animationState) {
        if (this.isGrabbing()) {
            return animationState.setAndContinue(GRAB);
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Bite", this::bitePredicate));
        controllerRegistrar.add(new AnimationController<>(this, "Grab", this::grabPredicate));
    }

    @Override
    public float getStepHeight() {
        return 2.0F;
    }

    @Override
    public @Nullable PartEntity<?>[] getParts() {
        return this.segments;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);

        if (!this.level().isClientSide) {
            for (GreatSerpentSegmentEntity seg : this.segments) {
                seg.kill();
            }
        }
    }

    private void moveSegments() {
        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i].tick();

            Entity leader = i == 0 ? this : this.segments[i - 1];
            Vec3 follow = leader.position();

            float angle = (((leader.getYRot() + 180.0F) * Mth.PI) / 180.0F);

            double f = (leader.getBbWidth() / 2) + (this.segments[i].getBbWidth() / 2);

            double force = 0.05D + (1.0D / (i + 1)) * 0.5D;

            double idealX = -Mth.sin(angle) * force;
            double idealZ = Mth.cos(angle) * force;

            double groundY = this.segments[i].isInWall() ? follow.y + f : follow.y;
            double idealY = (groundY - follow.y) * force;

            Vec3 diff = new Vec3(this.segments[i].getX() - follow.x, this.segments[i].getY() - follow.y, this.segments[i].getZ() - follow.z)
                    .normalize().add(idealX, idealY, idealZ).normalize();

            double destX = follow.x + f * diff.x;
            double destY = follow.y + f * diff.y;
            double destZ = follow.z + f * diff.z;

            this.segments[i].setPos(destX, destY, destZ);

            double distance = Mth.sqrt((float) (diff.x * diff.x + diff.z * diff.z));
            this.segments[i].setRot((float) (Math.atan2(diff.z, diff.x) * 180.0D / Math.PI) + 90.0F, -(float) (Math.atan2(diff.y, distance) * 180.0D / Math.PI));
        }
    }

    @Override
    public boolean isPushable() {
        return !this.isGrabbing() && super.isPushable();
    }

    @Override
    public void tick() {
        super.tick();

        this.yHeadRot = this.getYRot();
        this.moveSegments();

        if (this.level().isClientSide) return;

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        if (this.target == null || this.target.isDeadOrDying() || this.target.isRemoved()) {
            this.setGrabbing(false);
            return;
        }

        if (!this.isGrabbing()) {
            this.lookAt(EntityAnchorArgument.Anchor.EYES, this.target.position().add(0.0D, this.target.getBbHeight() / 2.0F, 0.0D));

            this.setDeltaMovement(this.target.position().subtract(this.position()).normalize().scale(2.0D));

            if (this.distanceTo(this.target) < 1.0D) {
                this.setGrabbing(true);
            }
        } else {
            this.setYRot(this.target.yBodyRot);

            Vec3 pos = this.position().add(0.0D, this.getBbHeight() / 2.0F, 0.0D);
            this.target.teleportTo(pos.x, pos.y, pos.z);
        }
    }

    @Override
    public boolean isMultipartEntity() {
        return this.segments != null;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 3 * 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 3 * 2.0D);
    }

    @Override
    public float getExperience() {
        return this.isTame() ? super.getExperience() : SorcererGrade.GRADE_2.getRequiredExperience();
    }

    @Override
    public Summon<?> getAbility() {
        return JJKAbilities.GREAT_SERPENT.get();
    }
}
