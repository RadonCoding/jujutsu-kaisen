package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.RotationUtil;

import javax.annotation.Nullable;

public class ThrownChainProjectile extends AbstractArrow {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(ThrownChainProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ThrownChainProjectile.class, EntityDataSerializers.ITEM_STACK);

    private static final int DURATION = 2 * 20;

    private boolean released;
    private boolean dealtDamage;

    private Entity pulled;

    public ThrownChainProjectile(EntityType<? extends AbstractArrow> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public ThrownChainProjectile(LivingEntity pShooter, ItemStack stack) {
        super(JJKEntities.THROWN_CHAIN.get(), pShooter, pShooter.level());

        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ())
                .add(RotationUtil.getTargetAdjustedLookAngle(pShooter));
        this.setPos(spawn.x, spawn.y, spawn.z);

        this.entityData.set(DATA_ITEM, stack);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_TIME, 0);
        this.entityData.define(DATA_ITEM, ItemStack.EMPTY);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("time", this.getTime());
        pCompound.putBoolean("released", this.released);
        pCompound.putBoolean("dealt_damage", this.dealtDamage);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.setTime(pCompound.getInt("time"));
        this.released = pCompound.getBoolean("released");
        this.dealtDamage = pCompound.getBoolean("dealt_damage");
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    @Override
    @Nullable
    protected EntityHitResult findHitEntity(@NotNull Vec3 pStartVec, @NotNull Vec3 pEndVec) {
        return this.dealtDamage ? null : super.findHitEntity(pStartVec, pEndVec);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);

        if (this.dealtDamage) return;

        Entity owner = this.getOwner();

        if (owner == null) return;

        if (!this.getStack().isEmpty()) return;

        this.pulled = owner;
        this.dealtDamage = true;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        Entity target = pResult.getEntity();

        Entity owner = this.getOwner();

        if (this.getStack().isEmpty()) {
            if (owner != null) {
                if (target.isPushable()) {
                    this.pulled = target;
                    this.dealtDamage = true;

                    this.setDeltaMovement(Vec3.ZERO);
                }
            }
        } else {
            DamageSource source = this.damageSources().arrow(this, owner == null ? this : owner);
            this.dealtDamage = true;

            double speed = this.getDeltaMovement().lengthSqr();

            SwordItem sword = (SwordItem) this.getStack().getItem();
            target.hurt(source, (float) (sword.getDamage() * speed));

            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        if (this.getOwner() instanceof LivingEntity owner && !owner.isRemoved() && !owner.isDeadOrDying()) {
            if (owner.getOffhandItem().isEmpty()) {
                owner.setItemInHand(InteractionHand.OFF_HAND, this.getStack());
                return;
            }
        }
        this.spawnAtLocation(this.getStack(), 0.1F);
    }

    @Override
    public void tick() {
        this.setTime(this.getTime() + 1);

        Entity owner = this.getOwner();

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive() || this.inGroundTime > DURATION)) {
            this.discard();
        } else {
            if (!(owner instanceof LivingEntity living)) return;

            if (this.released) {
                super.tick();

                if (this.inGroundTime > 4) {
                    this.dealtDamage = true;
                }

                if (this.dealtDamage && this.pulled != null) {
                    if (this.pulled == living) {
                        living.setDeltaMovement(this.position().subtract(living.position()).normalize());

                        if (living.distanceTo(this) <= 1.0D) {
                            this.discard();
                            return;
                        }
                    } else {
                        this.setPos(this.pulled.position().add(0.0D, this.pulled.getBbHeight() / 2.0F, 0.0D));

                        this.pulled.setDeltaMovement(living.position().subtract(this.pulled.position()).normalize());

                        if (this.pulled.distanceTo(living) <= 1.0D) {
                            this.discard();
                            return;
                        }
                    }
                    this.pulled.hurtMarked = true;
                }
            } else {
                double angle = Math.toRadians(this.getTime() * this.getTime());
                double radius = 2.0D;
                float yaw = (float) Math.toRadians(living.getYRot());
                float pitch = (float) Math.toRadians(90.0F);

                Vec3 offset = new Vec3(Math.cos(angle) * radius, 0.0D, Math.sin(angle) * radius)
                        .xRot(pitch).yRot(-yaw);
                Vec3 position = living.position().add(RotationUtil.getTargetAdjustedLookAngle(living)).add(offset);

                if (living.isUsingItem()) {
                    this.setPos(position.x, position.y, position.z);
                    this.setRot((float) Math.toDegrees(Math.atan2(offset.x, offset.z)),
                            -(float) Math.toDegrees(Math.asin(offset.y / offset.length())));

                    if (this.random.nextInt(5) == 0) {
                        this.playSound(SoundEvents.CHAIN_PLACE);
                    }
                } else {
                    Vec3 spawn = new Vec3(living.getX(), living.getEyeY() - (this.getBbHeight() / 2.0F), living.getZ())
                            .add(RotationUtil.getTargetAdjustedLookAngle(living));
                    this.setPos(spawn.x, spawn.y, spawn.z);
                    this.setRot(-RotationUtil.getTargetAdjustedYRot(living), RotationUtil.getTargetAdjustedXRot(living));

                    this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(living).scale(new Vec3(this.xOld, this.yOld, this.zOld).subtract(position).length()));
                    this.released = true;
                }
            }
        }
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    public ItemStack getStack() {
        return this.entityData.get(DATA_ITEM);
    }
}
