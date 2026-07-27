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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

public class TransfiguredSoulProjectile extends Projectile {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(TransfiguredSoulProjectile.class, EntityDataSerializers.INT);

    private static final double SPEED = 5.0D;
    private static final float DAMAGE = 10.0F;

    public TransfiguredSoulProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public TransfiguredSoulProjectile(LivingEntity pShooter) {
        super(JJKEntities.TRANSFIGURED_SOUL.get(), pShooter.level());

        this.setOwner(pShooter);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(pShooter);
        EntityUtil.offset(this, look, new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2), pShooter.getZ()).add(look));

        this.setDeltaMovement(look.scale(SPEED));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        pBuilder.define(DATA_TIME, 0);
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
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.setTime(pCompound.getInt("time"));
    }

    @Override
    public boolean ignoreExplosion(@NotNull Explosion pExplosion) {
        return true;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public void tick() {
        this.setTime(this.getTime() + 1);

        Entity owner = this.getOwner();

        if (!this.level().isClientSide && (owner == null || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();

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

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        Entity entity = pResult.getEntity();

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        if (entity == owner) return;

        entity.hurt(this.damageSources().thrown(this, owner), DAMAGE);

        this.discard();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);

        this.discard();
    }
}
