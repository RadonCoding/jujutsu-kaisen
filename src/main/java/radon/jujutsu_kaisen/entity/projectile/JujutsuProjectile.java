package radon.jujutsu_kaisen.entity.projectile;


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
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class JujutsuProjectile extends Projectile {
    private static final int MAX_DURATION = 10 * 20;

    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(JujutsuProjectile.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_POWER = SynchedEntityData.defineId(JujutsuProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_DOMAIN = SynchedEntityData.defineId(JujutsuProjectile.class, EntityDataSerializers.BOOLEAN);

    public JujutsuProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public JujutsuProjectile(EntityType<? extends Projectile> pType, Level pLevel, Entity pShooter) {
        super(pType, pLevel);

        this.setOwner(pShooter);
    }

    public JujutsuProjectile(EntityType<? extends Projectile> pType, Level pLevel, Entity pShooter, float power) {
        this(pType, pLevel, pShooter);

        this.setPower(power);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        pBuilder.define(DATA_TIME, 0);
        pBuilder.define(DATA_POWER, 0.0F);
        pBuilder.define(DATA_DOMAIN, false);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    public void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    public float getPower() {
        return this.entityData.get(DATA_POWER);
    }

    protected void setPower(float power) {
        this.entityData.set(DATA_POWER, power);
    }

    public boolean isDomain() {
        return this.entityData.get(DATA_DOMAIN);
    }

    public void setDomain(boolean domain) {
        this.entityData.set(DATA_DOMAIN, domain);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("time", this.getTime());
        pCompound.putFloat("power", this.getPower());
        pCompound.putBoolean("domain", this.isDomain());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.setTime(pCompound.getInt("time"));
        this.setPower(pCompound.getFloat("power"));
        this.setDomain(pCompound.getBoolean("domain"));
    }

    @Override
    public boolean ignoreExplosion(@NotNull Explosion pExplosion) {
        return true;
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
        this.setTime(this.getTime() + 1);

        Entity owner = this.getOwner();

        if (!this.level().isClientSide && (this.getTime() >= MAX_DURATION || (owner == null || owner.isRemoved() || !owner.isAlive()))) {
            this.discard();
            return;
        }

        super.tick();

        if (this.isProjectile()) {
            HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

            if (hit.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hit)) {
                this.onHit(hit);
            }

            this.checkInsideBlocks();

            Vec3 movement = this.getDeltaMovement();
            double d0 = this.getX() + movement.x;
            double d1 = this.getY() + movement.y;
            double d2 = this.getZ() + movement.z;
            this.setPos(d0, d1, d2);
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
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
