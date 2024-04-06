package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EmberInsectProjectile extends JujutsuProjectile implements GeoEntity {
    private static final EntityDataAccessor<Float> DATA_OFFSET_X = SynchedEntityData.defineId(EmberInsectProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_OFFSET_Y = SynchedEntityData.defineId(EmberInsectProjectile.class, EntityDataSerializers.FLOAT);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final float DAMAGE = 5.0F;
    private static final float SPEED = 3.0F;
    private static final float EXPLOSIVE_POWER = 1.0F;
    private static final float MAX_EXPLOSION = 5.0F;
    private static final int DELAY = 20;

    public EmberInsectProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public EmberInsectProjectile(LivingEntity owner, float power, float xOffset, float yOffset) {
        super(JJKEntities.EMBER_INSECT.get(), owner.level(), owner, power);

        this.entityData.set(DATA_OFFSET_X, xOffset);
        this.entityData.set(DATA_OFFSET_Y, yOffset);

        this.applyOffset();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_OFFSET_X, 0.0F);
        this.entityData.define(DATA_OFFSET_Y, 0.0F);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putFloat("x_offset", this.entityData.get(DATA_OFFSET_X));
        pCompound.putFloat("y_offset", this.entityData.get(DATA_OFFSET_Y));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_OFFSET_X, pCompound.getFloat("x_offset"));
        this.entityData.set(DATA_OFFSET_Y, pCompound.getFloat("y_offset"));
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        Entity entity = pResult.getEntity();

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        if (entity == owner) return;

        entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.EMBER_INSECTS.get()), DAMAGE * this.getPower());
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        if (this.level().isClientSide) return;

        Vec3 dir = this.getDeltaMovement().normalize();

        for (int i = 0; i < 50; i++) {
            Vec3 yaw = dir.yRot(this.random.nextFloat() * 360.0F);
            Vec3 pitch = yaw.xRot(this.random.nextFloat() * 180.0F - 90.0F);

            double dx = pitch.x + (this.random.nextDouble() - 0.5D) * 0.2D;
            double dy = pitch.y + (this.random.nextDouble() - 0.5D) * 0.2D;
            double dz = pitch.z + (this.random.nextDouble() - 0.5D) * 0.2D;

            ((ServerLevel) this.level()).sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 0, dx, dy, dz, 1.0D);
        }

        if (this.getOwner() instanceof LivingEntity owner) {
            Vec3 location = result.getLocation();
            ExplosionHandler.spawn(this.level().dimension(), location, Math.min(MAX_EXPLOSION, EXPLOSIVE_POWER * this.getPower()),
                    20, this.getPower() * 0.1F, owner, JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.EMBER_INSECTS.get()), true);
        }
        this.discard();
    }

    private void applyOffset() {
        if (this.getOwner() instanceof LivingEntity owner) {
            float xOffset = this.entityData.get(DATA_OFFSET_X);
            float yOffset = this.entityData.get(DATA_OFFSET_Y);

            Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
            EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                    .add(RotationUtil.calculateViewVector(0.0F, owner.getYRot() + 90.0F).scale(xOffset))
                    .add(RotationUtil.calculateViewVector(owner.getXRot() - 90.0F, owner.getYRot()).scale(yOffset))
                    .add(look));
        }
    }

    @Override
    public void tick() {
        super.tick();

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
            if (this.getTime() == DELAY) {
                this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Idle", animationState -> animationState.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
