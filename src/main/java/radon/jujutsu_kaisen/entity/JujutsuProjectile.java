package radon.jujutsu_kaisen.entity;

import net.minecraft.nbt.CompoundTag;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class JujutsuProjectile extends Projectile implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(JujutsuProjectile.class, EntityDataSerializers.INT);

    public JujutsuProjectile(EntityType<? extends JujutsuProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public JujutsuProjectile(EntityType<? extends JujutsuProjectile> pEntityType, Level pLevel, Entity pShooter) {
        super(pEntityType, pLevel);

        this.setOwner(pShooter);
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();

        if (this.level.isClientSide || (owner == null || !owner.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
            super.tick();

            int time = this.getTime();
            this.setTime(++time);

            HitResult result = ProjectileUtil.getHitResult(this, this::canHitEntity);

            if (result.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, result)) {
                this.onHit(result);
            }

            this.checkInsideBlocks();

            Vec3 movement = this.getDeltaMovement();
            double d0 = this.getX() + movement.x();
            double d1 = this.getY() + movement.y();
            double d2 = this.getZ() + movement.z();
            this.setPos(d0, d1, d2);
        } else {
            this.discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("time", this.entityData.get(DATA_TIME));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_TIME, pCompound.getInt("time"));
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_TIME, 0);
    }

    public int getTime() {
        return this.entityData.get(DATA_TIME);
    }

    private void setTime(int time) {
        this.entityData.set(DATA_TIME, time);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object o) {
        return RenderUtils.getCurrentTick();
    }
}
