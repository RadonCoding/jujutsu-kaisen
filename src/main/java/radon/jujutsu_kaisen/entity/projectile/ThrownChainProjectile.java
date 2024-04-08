package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

import javax.annotation.Nullable;

public class ThrownChainProjectile extends AbstractArrow {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(ThrownChainProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ThrownChainProjectile.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> DATA_RELEASED = SynchedEntityData.defineId(ThrownChainProjectile.class, EntityDataSerializers.BOOLEAN);

    private static final int DURATION = 2 * 20;

    private boolean dealtDamage;

    private Entity pulled;

    public ThrownChainProjectile(EntityType<? extends AbstractArrow> pType, Level pLevel) {
        super(pType, pLevel, ItemStack.EMPTY);

        this.noCulling = true;
    }

    public ThrownChainProjectile(LivingEntity pShooter, ItemStack stack) {
        super(JJKEntities.THROWN_CHAIN.get(), pShooter, pShooter.level(), stack);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(pShooter);
        this.setPos(new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ()).add(look));

        this.entityData.set(DATA_ITEM, stack);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_TIME, 0);
        this.entityData.define(DATA_ITEM, ItemStack.EMPTY);
        this.entityData.define(DATA_RELEASED, false);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    public boolean isReleased() {
        return this.entityData.get(DATA_RELEASED);
    }

    public void setReleased(boolean released) {
        this.entityData.set(DATA_RELEASED, released);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("time", this.getTime());
        pCompound.putBoolean("released", this.isReleased());
        pCompound.putBoolean("dealt_damage", this.dealtDamage);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.setTime(pCompound.getInt("time"));
        this.setReleased(pCompound.getBoolean("released"));
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

    public Vec3 calculateOffset(float time) {
        Entity owner = this.getOwner();

        if (owner == null) return this.position();

        float angle = (float) Math.toRadians(time * time * 0.5F);
        double radius = 2.0D;

        double x = Math.cos(angle) * radius;
        double z = Math.sin(angle) * radius;

        return new Vec3(x, 0.0D, z);
    }

    @Override
    public void tick() {
        this.setTime(this.getTime() + 1);

        Entity owner = this.getOwner();

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive() || this.inGroundTime > DURATION)) {
            this.discard();
        } else {
            if (owner == null) return;

            if (this.isReleased()) {
                super.tick();

                if (this.inGroundTime > 4) {
                    this.dealtDamage = true;
                }

                if (this.dealtDamage && this.pulled != null) {
                    if (this.pulled == owner) {
                        owner.setDeltaMovement(this.position().subtract(owner.position()).normalize());

                        if (owner.distanceTo(this) <= 1.0D) {
                            this.discard();
                            return;
                        }
                    } else {
                        this.setPos(this.pulled.position().add(0.0D, this.pulled.getBbHeight() / 2.0F, 0.0D));

                        this.pulled.setDeltaMovement(owner.position().subtract(this.pulled.position()).normalize());

                        if (this.pulled.distanceTo(owner) <= 1.0D) {
                            this.discard();
                            return;
                        }
                    }
                    this.pulled.hurtMarked = true;
                }
                return;
            }

            float pitch = (float) Math.toRadians(90.0F);

            Vec3 current = this.calculateOffset(this.getTime()).xRot(pitch).yRot(-(float) Math.toRadians(owner.getYRot()));
            Vec3 previous = this.calculateOffset(this.getTime() - 1).xRot(pitch).yRot(-(float) Math.toRadians(owner.yRotO));

            if (!(owner instanceof LivingEntity living)) return;

            Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

            if (living.isUsingItem()) {
                this.setPos(new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look));

                if (this.random.nextInt(5) == 0) {
                    this.playSound(SoundEvents.CHAIN_PLACE);
                }
            } else {
                this.setDeltaMovement(look.scale(previous.subtract(current).length()));

                EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                        .add(look));

                this.setReleased(true);
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
