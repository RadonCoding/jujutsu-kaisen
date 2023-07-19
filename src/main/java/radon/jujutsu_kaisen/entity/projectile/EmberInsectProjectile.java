package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
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

    private static final float DAMAGE = 1.0F;
    private static final float SPEED = 3.0F;
    private static final float EXPLOSIVE_POWER = 1.0F;
    private static final int DELAY = 20;

    public EmberInsectProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public EmberInsectProjectile(LivingEntity pShooter, float xOffset, float yOffset) {
        super(JJKEntities.EMBER_INSECT.get(), pShooter.level, pShooter);

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

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if ((entity instanceof LivingEntity living && owner.canAttack(living)) && entity != owner) {
                    entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner), DAMAGE * cap.getGrade().getPower());
                }
            });
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        Vec3 dir = this.getDeltaMovement().normalize();

        for (int i = 0; i < 50; i++) {
            Vec3 yaw = dir.yRot(this.random.nextFloat() * 360.0F);
            Vec3 pitch = yaw.xRot(this.random.nextFloat() * 180.0F - 90.0F);

            double dx = pitch.x() + (this.random.nextDouble() - 0.5D) * 0.2D;
            double dy = pitch.y() + (this.random.nextDouble() - 0.5D) * 0.2D;
            double dz = pitch.z() + (this.random.nextDouble() - 0.5D) * 0.2D;

            if (!this.level.isClientSide) {
                ((ServerLevel) this.level).sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 0, dx, dy, dz, 1.0D);
            }
        }

        Entity owner = this.getOwner();

        if (owner != null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                Vec3 location = result.getLocation();
                this.level.explode(owner, location.x(), location.y(), location.z(),
                        EXPLOSIVE_POWER * cap.getGrade().getPower(), Level.ExplosionInteraction.NONE);
            });
        }
        this.discard();
    }

    private void applyOffset() {
        if (this.getOwner() instanceof LivingEntity owner) {
            float xOffset = this.entityData.get(DATA_OFFSET_X);
            float yOffset = this.entityData.get(DATA_OFFSET_Y);

            Vec3 look = owner.getLookAngle();
            Vec3 spawn = new Vec3(owner.getX(),
                    owner.getEyeY() - (this.getBbHeight() / 2.0F),
                    owner.getZ())
                    .add(look)
                    .add(look.yRot(-90.0F).scale(xOffset))
                    .add(new Vec3(0.0F, yOffset, 0.0F));
            this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() < DELAY) {
                if (!owner.isAlive()) {
                    this.discard();
                } else {
                    owner.swing(InteractionHand.MAIN_HAND);

                    this.applyOffset();
                }
            } else if (this.getTime() >= DELAY) {
                if (this.getTime() > DELAY && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                    this.discard();
                } else {
                    this.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, SPEED, 1.0F);
                }
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
