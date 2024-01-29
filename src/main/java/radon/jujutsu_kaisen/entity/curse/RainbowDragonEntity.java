package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.base.IJumpInputListener;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class RainbowDragonEntity extends CursedSpirit implements PlayerRideable, IJumpInputListener {
    private static final RawAnimation BITE = RawAnimation.begin().thenPlay("attack.bite");

    private static final int MAX_SEGMENTS = 12;

    public static final int ARMS = 1;
    public static final int LEGS = 7;
    public static final int TAIL = 12;

    private boolean jump;

    private final RainbowDragonSegmentEntity[] segments;

    public RainbowDragonEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);

        this.moveControl = new FlyingMoveControl(this, 20, true);

        this.segments = new RainbowDragonSegmentEntity[MAX_SEGMENTS];

        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i] = new RainbowDragonSegmentEntity(this, i);
            this.segments[i].moveTo(this.getX() + 0.1D * i, this.getY() + 0.5D, this.getZ() + 0.1D * i, this.random.nextFloat() * 360.0F, 0.0F);
        }
        this.setId(ENTITY_COUNTER.getAndAdd(this.segments.length + 1) + 1);
    }

    @Override
    public void setId(int id) {
        super.setId(id);

        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i].setId(id + i + 1);
        }
    }

    private PlayState bitePredicate(AnimationState<RainbowDragonEntity> animationState) {
        if (this.swinging) {
            return animationState.setAndContinue(BITE);
        }
        animationState.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Bite", this::bitePredicate));
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
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected boolean canFly() {
        return true;
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
            for (RainbowDragonSegmentEntity seg : this.segments) {
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
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        if (pPlayer == this.getOwner() && this.isTame() && !this.isVehicle()) {
            if (pPlayer.startRiding(this)) {
                pPlayer.setYRot(this.getYRot());
                pPlayer.setXRot(this.getXRot());
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    @Override
    protected @NotNull Vec3 getRiddenInput(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
        if (this.onGround()) {
            return Vec3.ZERO;
        } else {
            float f = pPlayer.xxa * 0.5F;
            float f1 = pPlayer.zza;

            if (f1 <= 0.0F) {
                f1 *= 0.25F;
            }
            return new Vec3(f, 0.0D, f1);
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
    public boolean isNoGravity() {
        return !this.isVehicle() && super.isNoGravity();
    }

    @Override
    protected void tickRidden(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
        super.tickRidden(pPlayer, pTravelVector);

        Vec2 vec2 = this.getRiddenRotation(pPlayer);
        this.setRot(vec2.y, vec2.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

        Vec3 movement = this.getDeltaMovement();

        if (this.jump) {
            this.setDeltaMovement(movement.add(0.0D, this.getFlyingSpeed(), 0.0D));
        }
    }

    @Override
    public void setJump(boolean jump) {
        this.jump = jump;
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        this.yHeadRot = this.getYRot();
        this.moveSegments();
    }

    @Override
    public boolean isMultipartEntity() {
        return this.segments != null;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.FLYING_SPEED)
                .add(Attributes.ARMOR, 10.0D);
    }

    @Override
    protected float getFlyingSpeed() {
        return this.getTarget() == null || this.isControlledByLocalInstance() ? 0.15F : 1.0F;
    }

    @Override
    public float getExperience() {
        return SorcererGrade.GRADE_1.getRequiredExperience();
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }
}
