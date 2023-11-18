package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FireballProjectile extends JujutsuProjectile implements GeoEntity {
    private static final RawAnimation SPIN = RawAnimation.begin().thenLoop("misc.spin");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final float DAMAGE = 20.0F;
    private static final float EXPLOSIVE_POWER = 2.5F;
    private static final float MAX_EXPLOSION = 15.0F;
    private static final float SPEED = 5.0F;
    public static final int DELAY = 20;

    public FireballProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FireballProjectile(LivingEntity owner, float power) {
        super(JJKEntities.FIREBALL.get(), owner.level(), owner, power);

        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                .add(owner.getLookAngle());
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        Entity entity = pResult.getEntity();

        if (this.getOwner() instanceof LivingEntity owner) {
            if ((entity instanceof LivingEntity living && owner.canAttack(living)) && entity != owner) {
                entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.FIRE_ARROW.get()), DAMAGE * this.getPower());
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        if (this.level().isClientSide) return;

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        int pillarCount = (int) (this.getFlamePillarRadius() * Math.PI * 2) * 32;

        for (int i = 0; i < pillarCount; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = this.getFlamePillarRadius() * Math.sin(phi) * Math.cos(theta);
            double yOffset = this.getFlamePillarRadius() * Math.sin(phi) * Math.sin(theta);
            double zOffset = this.getFlamePillarRadius() * Math.cos(phi);

            double x = center.x() + xOffset * this.getFlamePillarRadius();
            double y = center.y() + yOffset * (this.getFlamePillarRadius() * 10.0F);
            double z = center.z() + zOffset * this.getFlamePillarRadius();

            HelperMethods.sendParticles((ServerLevel) this.level(), new TravelParticle.TravelParticleOptions(new Vec3(x, y, z).toVector3f(), ParticleColors.RED_FIRE_COLOR, this.getFlamePillarRadius() * 0.3F, 1.0F, true, 20),
                    true, center.x() + xOffset * (this.getFlamePillarRadius() * 0.1F), center.y(), center.z() + zOffset * (this.getFlamePillarRadius() * 0.1F));
            HelperMethods.sendParticles((ServerLevel) this.level(), new TravelParticle.TravelParticleOptions(new Vec3(x, y, z).toVector3f(), ParticleColors.YELLOW_FIRE_COLOR, this.getFlamePillarRadius() * 0.3F, 1.0F, true, 20),
                    true, center.x() + xOffset * (this.getFlamePillarRadius() * 0.1F), center.y(), center.z() + zOffset * (this.getFlamePillarRadius() * 0.1F));
        }

        for (int i = 0; i < pillarCount / 2; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = this.getFlamePillarRadius() * Math.sin(phi) * Math.cos(theta);
            double yOffset = this.getFlamePillarRadius() * Math.sin(phi) * Math.sin(theta);
            double zOffset = this.getFlamePillarRadius() * Math.cos(phi);

            double startX = center.x() + xOffset * (this.getFlamePillarRadius() * 0.1F);
            double startZ = center.z() + zOffset * (this.getFlamePillarRadius() * 0.1F);

            double x = center.x() + xOffset * this.getFlamePillarRadius();
            double y = center.y() + yOffset * (this.getFlamePillarRadius() * 10.0F);
            double z = center.z() + zOffset * this.getFlamePillarRadius();

            HelperMethods.sendParticles((ServerLevel) this.level(), new TravelParticle.TravelParticleOptions(new Vec3(x, y, z).toVector3f(), ParticleColors.SMOKE_COLOR, this.getFlamePillarRadius() * 0.3F, 1.0F, false, 20),
                    true, startX, center.y(), startZ);
        }

        int shockwaveCount = (int) (this.getFlamePillarRadius() * 2 * Math.PI * 2) * 32;

        for (int i = 0; i < shockwaveCount; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = this.getFlamePillarRadius() * 2 * Math.sin(phi) * Math.cos(theta);
            double zOffset = this.getFlamePillarRadius() * 2 * Math.cos(phi);

            double x = center.x() + xOffset * this.getFlamePillarRadius() * 2;
            double z = center.z() + zOffset * this.getFlamePillarRadius() * 2;

            HelperMethods.sendParticles((ServerLevel) this.level(), new TravelParticle.TravelParticleOptions(new Vec3(x, center.y(), z).toVector3f(), ParticleColors.RED_FIRE_COLOR, this.getFlamePillarRadius() * 0.3F, 1.0F, true, 20),
                    true, center.x() + (this.random.nextDouble() - 0.5D), center.y(), center.z() + (this.random.nextDouble() - 0.5D));
            HelperMethods.sendParticles((ServerLevel) this.level(), new TravelParticle.TravelParticleOptions(new Vec3(x, center.y(), z).toVector3f(), ParticleColors.SMOKE_COLOR, this.getFlamePillarRadius() * 0.3F, 1.0F, false, 20),
                    true, center.x() + (this.random.nextDouble() - 0.5D), center.y(), center.z() + (this.random.nextDouble() - 0.5D));
        }

        if (this.getOwner() instanceof LivingEntity owner) {
            Vec3 location = result.getLocation();
            ExplosionHandler.spawn(this.level().dimension(), location, this.getExplosionRadius(),
                    20, this.getPower(), owner, JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.FIREBALL.get()), true);
        }
        this.discard();
    }

    private float getFlamePillarRadius() {
        return this.getExplosionRadius() * 0.25F;
    }

    private float getExplosionRadius() {
        return Math.min(MAX_EXPLOSION, EXPLOSIVE_POWER * this.getPower());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getOwner() instanceof LivingEntity owner) {
            for (int i = 0; i < 2; i++) {
                Vec3 dir = owner.getLookAngle().reverse().scale(0.1D);
                double dx = dir.x() + ((this.random.nextDouble() - 0.5D) * 0.5D);
                double dy = dir.y() + ((this.random.nextDouble() - 0.5D) * 0.5D);
                double dz = dir.z() + ((this.random.nextDouble() - 0.5D) * 0.5D);

                this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ(), dx, dy, dz);
            }

            Vec3 look = owner.getLookAngle();
            double d0 = look.horizontalDistance();
            this.setYRot((float) (Mth.atan2(look.x(), look.z()) * (double) (180.0F / (float) Math.PI)));
            this.setXRot((float) (Mth.atan2(look.y(), d0) * (double) (180.0F / (float) Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();

            if (this.getTime() < DELAY) {
                if (!owner.isAlive()) {
                    this.discard();
                } else {
                    if (this.getTime() % 5 == 0) {
                        owner.swing(InteractionHand.MAIN_HAND);
                    }
                    Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                            .add(owner.getLookAngle());
                    this.setPos(spawn.x(), spawn.y(), spawn.z());
                }
            } else if (this.getTime() >= DELAY) {
                if (this.getTime() == DELAY) {
                    this.setDeltaMovement(owner.getLookAngle().scale(SPEED));
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Spin", animationState -> animationState.setAndContinue(SPIN)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
