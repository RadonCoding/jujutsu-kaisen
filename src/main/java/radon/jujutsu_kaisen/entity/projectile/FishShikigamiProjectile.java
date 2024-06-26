package radon.jujutsu_kaisen.entity.projectile;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class FishShikigamiProjectile extends JujutsuProjectile implements GeoEntity {
    private static final EntityDataAccessor<Float> DATA_OFFSET_X = SynchedEntityData.defineId(FishShikigamiProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_OFFSET_Y = SynchedEntityData.defineId(FishShikigamiProjectile.class, EntityDataSerializers.FLOAT);
    private static final float DAMAGE = 10.0F;
    private static final int DELAY = 20;
    private static final int DURATION = 20;
    private static final double SPEED = 2.0D;
    private static final float EXPLOSIVE_POWER = 2.5F;
    private static final float MAX_EXPLOSION = 10.0F;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Nullable
    private UUID targetUUID;
    @Nullable
    private LivingEntity cachedTarget;

    public FishShikigamiProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public FishShikigamiProjectile(EntityType<? extends Projectile> pType, LivingEntity owner, float power, float xOffset, float yOffset, LivingEntity target) {
        this(pType, owner, power, xOffset, yOffset);

        this.setTarget(target);
    }

    public FishShikigamiProjectile(EntityType<? extends Projectile> pType, LivingEntity owner, float power, float xOffset, float yOffset) {
        super(pType, owner.level(), owner, power);

        this.setOffsetX(xOffset);
        this.setOffsetY(yOffset);

        this.applyOffset();
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

    public void setTarget(@Nullable LivingEntity target) {
        if (target != null) {
            this.targetUUID = target.getUUID();
            this.cachedTarget = target;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_OFFSET_X, 0.0F);
        pBuilder.define(DATA_OFFSET_Y, 0.0F);
    }

    private float getOffsetX() {
        return this.entityData.get(DATA_OFFSET_X);
    }

    private void setOffsetX(float offsetX) {
        this.entityData.set(DATA_OFFSET_X, offsetX);
    }

    private float getOffsetY() {
        return this.entityData.get(DATA_OFFSET_Y);
    }

    private void setOffsetY(float offsetY) {
        this.entityData.set(DATA_OFFSET_Y, offsetY);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putFloat("x_offset", this.getOffsetX());
        pCompound.putFloat("y_offset", this.getOffsetY());

        if (this.targetUUID != null) {
            pCompound.putUUID("target", this.targetUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.setOffsetX(pCompound.getFloat("x_offset"));
        this.setOffsetY(pCompound.getFloat("y_offset"));

        if (pCompound.hasUUID("target")) {
            this.targetUUID = pCompound.getUUID("target");
        }
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity pTarget) {
        return !(pTarget instanceof FishShikigamiProjectile) && super.canHitEntity(pTarget);
    }

    private void applyRotation() {
        Entity owner = this.getOwner();

        if (owner == null) return;

        LivingEntity target = this.getTarget();

        if (target == null) {
            EntityUtil.rotation(this, RotationUtil.getTargetAdjustedLookAngle(owner));
        } else {
            EntityUtil.rotation(this, target.position().subtract(this.position()));
        }
    }

    private void applyOffset() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

        this.applyRotation();

        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2), owner.getZ())
                .subtract(look.multiply(this.getBbWidth() * 3.0D, 0.0D, this.getBbWidth() * 3.0D))
                .add(look)
                .add(look.yRot(-90.0F).scale(this.getOffsetX()))
                .add(new Vec3(0.0F, this.getOffsetY(), 0.0F));
        this.setPos(spawn.x, spawn.y, spawn.z);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        Vec3 location = pResult.getLocation();
        ExplosionHandler.spawn(this.level().dimension(), location, Math.min(MAX_EXPLOSION, EXPLOSIVE_POWER * this.getPower()),
                20, 1.0F, owner, this.damageSources().explosion(this, owner), false);

        this.discard();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (this.level().isClientSide) return;

        Entity entity = pResult.getEntity();

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        if (entity == owner) return;

        if (entity != this.getTarget()) return;

        if (this.isDomain()) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            DomainExpansionEntity domain = data.getSummonByClass(DomainExpansionEntity.class);
            entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain == null ? this : domain, owner, JJKAbilities.HORIZON_OF_THE_CAPTIVATING_SKANDHA.get()), DAMAGE * this.getPower());
        } else {
            entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.FISH_SHIKIGAMI.get()), DAMAGE * this.getPower());
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getTime() - DELAY >= DURATION) {
            this.discard();
            return;
        }

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

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
            if (this.isDomain()) {
                this.applyRotation();

                LivingEntity target = this.getTarget();

                if (target != null && !target.isDeadOrDying() && !target.isRemoved()) {
                    this.setDeltaMovement(target.position().add(0.0D, target.getBbHeight() / 2, 0.0D)
                            .subtract(this.position()).normalize().scale(SPEED));
                } else {
                    this.discard();
                }
            } else if (this.getTime() == DELAY) {
                this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
