package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

public class TransfiguredSoulProjectile extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(TransfiguredSoulProjectile.class, EntityDataSerializers.INT);

    private static final double SPEED = 5.0D;
    private static final float DAMAGE = 10.0F;
    private static final int DURATION = 5 * 20;

    public TransfiguredSoulProjectile(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public TransfiguredSoulProjectile(LivingEntity pShooter) {
        super(JJKEntities.TRANSFIGURED_SOUL.get(), pShooter, pShooter.level());

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(pShooter);
        EntityUtil.offset(this, look, new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ()).add(look));

        this.setDeltaMovement(look.scale(SPEED));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_TIME, 0);
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
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        Entity entity = pResult.getEntity();

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        entity.hurt(this.damageSources().thrown(this, owner), DAMAGE);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getTime() >= DURATION) {
            this.discard();
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return JJKItems.TRANSFIGURED_SOUL.get();
    }
}
