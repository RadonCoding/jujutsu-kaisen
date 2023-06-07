package radon.jujutsu_kaisen.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;
import radon.jujutsu_kaisen.client.JujutsuParticles;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class RedProjectile extends AbstractHurtingProjectile implements GeoAnimatable {
    private static final float EXPLOSION_MULTIPLIER = 1.0F;
    private static final float DAMAGE_MULTIPLIER = 5.0F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RedProjectile(EntityType<? extends RedProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public RedProjectile(LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(JujutsuEntities.RED.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pShooter.level);

        this.moveTo(pShooter.getX(), pShooter.getEyeY() - 0.2D, pShooter.getZ(), this.getYRot(), this.getXRot());
        this.reapplyPosition();

        double d0 = Math.sqrt(pOffsetX * pOffsetX + pOffsetY * pOffsetY + pOffsetZ * pOffsetZ);

        if (d0 != 0.0D) {
            this.xPower = pOffsetX / d0 * 1.25D;
            this.yPower = pOffsetY / d0 * 1.25D;
            this.zPower = pOffsetZ / d0 * 1.25D;
        }
    }

    @Override
    protected @NotNull ParticleOptions getTrailParticle() {
        return JujutsuParticles.EMPTY.get();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    protected void onHit(@NotNull HitResult pResult) {
        super.onHit(pResult);

        if (!this.level.isClientSide) {
            Entity owner = this.getOwner();

            if (owner != null) {
                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    float explosion = EXPLOSION_MULTIPLIER * (cap.getGrade().ordinal() + 1);
                    this.level.explode(this, this.getX(), this.getY(), this.getZ(), explosion,
                            true, Level.ExplosionInteraction.MOB);
                    this.discard();
                });
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (!this.level.isClientSide) {
            Entity target = pResult.getEntity();
            Entity owner = this.getOwner();

            if (owner != null) {
                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    float damage = DAMAGE_MULTIPLIER * (cap.getGrade().ordinal() + 1);
                    target.hurt(DamageSource.indirectMagic(this, owner), damage);
                });
            }
        }
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
