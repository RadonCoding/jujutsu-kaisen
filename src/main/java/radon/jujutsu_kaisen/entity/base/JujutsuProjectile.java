package radon.jujutsu_kaisen.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class JujutsuProjectile extends Projectile {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(JujutsuProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_POWER = SynchedEntityData.defineId(JujutsuProjectile.class, EntityDataSerializers.FLOAT);

    public JujutsuProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public JujutsuProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel, Entity pShooter, float power) {
        super(pEntityType, pLevel);

        this.setOwner(pShooter);
        this.setPower(power);
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    protected void setPower(float power) {
        this.entityData.set(DATA_POWER, power);
    }

    protected float getPower() {
        return this.entityData.get(DATA_POWER);
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    protected boolean isProjectile() {
        return true;
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();

        if (!this.level().isClientSide && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();

            int time = this.getTime();
            this.setTime(++time);

            if (this.isProjectile()) {
                HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

                if (hit.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hit)) {
                    this.onHit(hit);
                }

                this.checkInsideBlocks();

                Vec3 movement = this.getDeltaMovement();
                double d0 = this.getX() + movement.x();
                double d1 = this.getY() + movement.y();
                double d2 = this.getZ() + movement.z();
                this.setPos(d0, d1, d2);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("time", this.entityData.get(DATA_TIME));
        pCompound.putFloat("power", this.entityData.get(DATA_POWER));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_TIME, pCompound.getInt("time"));
        this.entityData.set(DATA_POWER, pCompound.getFloat("power"));
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_TIME, 0);
        this.entityData.define(DATA_POWER, 0.0F);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    private void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = this.getBoundingBox().getSize() * 10.0D;

        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }
        d0 *= 64.0D * getViewScale();
        return pDistance < d0 * d0;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        int i = entity == null ? 0 : entity.getId();
        return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(),
                this.getXRot(), this.getYRot(), this.getType(), i, this.getDeltaMovement(), 0.0D);
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        this.moveTo(pPacket.getX(), pPacket.getY(), pPacket.getZ(), pPacket.getYRot(), pPacket.getXRot());
        this.setDeltaMovement(pPacket.getXa(), pPacket.getYa(), pPacket.getZa());
    }
}
