package radon.jujutsu_kaisen.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class FishShikigamiProjectile extends JujutsuProjectile implements GeoEntity {
    private static final EntityDataAccessor<Float> DATA_OFFSET_X = SynchedEntityData.defineId(FishShikigamiProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_OFFSET_Y = SynchedEntityData.defineId(FishShikigamiProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_BITE = SynchedEntityData.defineId(FishShikigamiProjectile.class, EntityDataSerializers.INT);

    private static final RawAnimation BITE = RawAnimation.begin().thenPlay("attack.bite");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final float DAMAGE = 10.0F;
    private static final int DELAY = 20;
    private static final double SPEED = 2.0D;

    @Nullable
    private UUID targetUUID;
    @Nullable
    private LivingEntity cachedTarget;

    public FishShikigamiProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FishShikigamiProjectile(EntityType<? extends Projectile> pEntityType, LivingEntity owner, float power, LivingEntity target, float xOffset, float yOffset) {
        super(pEntityType, owner.level(), owner, power);

        this.setTarget(target);

        this.entityData.set(DATA_OFFSET_X, xOffset);
        this.entityData.set(DATA_OFFSET_Y, yOffset);

        this.applyOffset();
    }

    public void setTarget(@Nullable LivingEntity target) {
        if (target != null) {
            this.targetUUID = target.getUUID();
            this.cachedTarget = target;
        }
    }

    @Nullable
    public LivingEntity getTarget() {
        if (this.cachedTarget != null && !this.cachedTarget.isRemoved()) {
            return this.cachedTarget;
        } else if (this.targetUUID != null && this.level() instanceof ServerLevel) {
            this.cachedTarget = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.targetUUID);
            return this.cachedTarget;
        } else {
            return null;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_OFFSET_X, 0.0F);
        this.entityData.define(DATA_OFFSET_Y, 0.0F);
        this.entityData.define(DATA_BITE, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putFloat("x_offset", this.entityData.get(DATA_OFFSET_X));
        pCompound.putFloat("y_offset", this.entityData.get(DATA_OFFSET_Y));

        if (this.targetUUID != null) {
            pCompound.putUUID("target", this.targetUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_OFFSET_X, pCompound.getFloat("x_offset"));
        this.entityData.set(DATA_OFFSET_Y, pCompound.getFloat("y_offset"));

        if (pCompound.hasUUID("target")) {
            this.targetUUID = pCompound.getUUID("target");
        }
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity pTarget) {
        return !(pTarget instanceof FishShikigamiProjectile) && super.canHitEntity(pTarget);
    }

    private void applyRotation() {
        LivingEntity target = this.getTarget();

        if (target != null) {
            Vec3 delta = target.position().subtract(this.position());
            double d0 = delta.horizontalDistance();
            this.setYRot((float) (Mth.atan2(delta.x(), delta.z()) * (double) (180.0F / (float) Math.PI)));
            this.setXRot((float) (Mth.atan2(delta.y(), d0) * (double) (180.0F / (float) Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
    }

    private void applyOffset() {
        if (this.getOwner() instanceof LivingEntity owner) {
            float xOffset = this.entityData.get(DATA_OFFSET_X);
            float yOffset = this.entityData.get(DATA_OFFSET_Y);

            Vec3 look = owner.getLookAngle();

            this.applyRotation();

            Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                    .subtract(look.multiply(this.getBbWidth() * 3.0D, 0.0D, this.getBbWidth() * 3.0D))
                    .add(look)
                    .add(look.yRot(-90.0F).scale(xOffset))
                    .add(new Vec3(0.0F, yOffset, 0.0F));
            this.setPos(spawn.x(), spawn.y(), spawn.z());
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (this.level().isClientSide) return;

        Entity entity = pResult.getEntity();

        if (this.getOwner() instanceof LivingEntity owner) {
            if (entity == owner) return;

            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            DomainExpansionEntity domain = cap.getDomain((ServerLevel) this.level());
            entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain == null ? this : domain, owner, JJKAbilities.DEATH_SWARM.get()), DAMAGE * this.getPower());
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            int bite = this.entityData.get(DATA_BITE);

            if (bite > 0) {
                this.entityData.set(DATA_BITE, --bite);
            }
        }

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() < DELAY) {
                if (!owner.isAlive()) {
                    this.discard();
                } else {
                    if (this.getTime() % 5 == 0) {
                        owner.swing(InteractionHand.MAIN_HAND);
                    }
                    this.applyOffset();
                }
            } else if (this.getTime() >= DELAY) {
                this.applyRotation();

                if (!this.level().isClientSide) {
                    LivingEntity target = this.getTarget();

                    if (target != null && !target.isDeadOrDying() && !target.isRemoved()) {
                        this.setDeltaMovement(target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D)
                                .subtract(this.position()).normalize().scale(SPEED));
                    } else {
                        this.discard();
                    }
                }
            }
        }
    }

    private PlayState bitePredicate(AnimationState<FishShikigamiProjectile> animationState) {
        if (this.entityData.get(DATA_BITE) > 0) {
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
