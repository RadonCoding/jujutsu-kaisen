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

import javax.annotation.Nullable;

public class ThrownChainItemProjectile extends AbstractArrow {
    private static final double PULL_STRENGTH = 5.0D;

    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ThrownChainItemProjectile.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(ThrownChainItemProjectile.class, EntityDataSerializers.INT);

    private boolean released;
    private boolean dealtDamage;

    public ThrownChainItemProjectile(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.noCulling = true;
    }

    public ThrownChainItemProjectile(LivingEntity pShooter, ItemStack stack) {
        super(JJKEntities.THROWN_CHAIN_ITEM.get(), pShooter, pShooter.level);

        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ())
                .add(pShooter.getLookAngle());
        this.setPos(spawn.x(), spawn.y(), spawn.z());

        this.entityData.set(DATA_ITEM, stack);
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    private void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    @Override
    @Nullable
    protected EntityHitResult findHitEntity(@NotNull Vec3 pStartVec, @NotNull Vec3 pEndVec) {
        return this.dealtDamage ? null : super.findHitEntity(pStartVec, pEndVec);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);

        Entity owner = this.getOwner();

        if (this.getStack().isEmpty()) {
            if (owner != null) {
                owner.setDeltaMovement(this.position().subtract(owner.position()).normalize().scale(PULL_STRENGTH));
                owner.hurtMarked = true;

                this.dealtDamage = true;
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity target = pResult.getEntity();

        Entity owner = this.getOwner();

        if (this.getStack().isEmpty()) {
            if (owner != null) {
                if (target.isPushable()) {
                    target.setDeltaMovement(owner.position().subtract(target.position()).normalize().scale(PULL_STRENGTH));
                    target.hurtMarked = true;

                    this.dealtDamage = true;
                }
            }
        } else {
            DamageSource source = this.damageSources().arrow(this, owner == null ? this : owner);
            this.dealtDamage = true;

            double speed = this.getDeltaMovement().length();

            SwordItem sword = (SwordItem) this.getStack().getItem();
            target.hurt(source, (float) (sword.getDamage() * speed));
        }
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 20) {
            if (this.getOwner() instanceof LivingEntity owner) {
                if (owner.getOffhandItem().isEmpty()) {
                    owner.setItemInHand(InteractionHand.OFF_HAND, this.getStack());
                } else {
                    owner.spawnAtLocation(this.getStack(), 0.1F);
                }
                this.discard();
            }
        } else if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        int time = this.getTime();
        this.setTime(++time);

        if (this.getOwner() instanceof LivingEntity owner) {
            if (!this.released) {
                double angle = Math.toRadians(this.getTime() * this.getTime());
                double radius = 2.0D;
                float yaw = (float) Math.toRadians(owner.getYRot());
                float pitch = (float) Math.toRadians(90.0F);

                Vec3 offset = new Vec3(Math.cos(angle) * radius, 0.0D, Math.sin(angle) * radius)
                        .xRot(pitch).yRot(-yaw);
                Vec3 position = owner.position().add(owner.getLookAngle().scale(2.5D)).add(offset);

                if (owner.isUsingItem()) {
                    this.setPos(position.x(), position.y(), position.z());
                    this.setRot((float) Math.toDegrees(Math.atan2(offset.x(), offset.z())),
                            -(float) Math.toDegrees(Math.asin(offset.y() / offset.length())));

                    if (this.random.nextInt(5) == 0) {
                        this.playSound(SoundEvents.CHAIN_PLACE);
                    }
                } else {
                    Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                            .add(owner.getLookAngle());
                    this.setPos(spawn.x(), spawn.y(), spawn.z());
                    this.setRot(-owner.getYRot(), owner.getXRot());

                    this.setDeltaMovement(owner.getLookAngle().scale(new Vec3(this.xOld, this.yOld, this.zOld).subtract(position).length()));
                    this.released = true;
                }
            } else {
                super.tick();
            }
        }
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putBoolean("released", this.released);
        pCompound.putBoolean("dealt_damage", this.dealtDamage);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.released = pCompound.getBoolean("released");
        this.dealtDamage = pCompound.getBoolean("dealt_damage");
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

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_ITEM, ItemStack.EMPTY);
        this.entityData.define(DATA_TIME, 0);
    }
}
