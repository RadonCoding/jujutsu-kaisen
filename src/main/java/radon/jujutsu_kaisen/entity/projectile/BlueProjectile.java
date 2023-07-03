package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.SpinningParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class BlueProjectile extends JujutsuProjectile {
    private static final double RANGE = 10.0D;

    private static final double RING_RADIUS = 1.25D;
    private static final double BALL_RADIUS = 1.0D;
    private static final double PULL_RADIUS = 10.0D;
    private static final float PARTICLE_SIZE = 0.075F;
    private static final float DAMAGE = 2.5F;
    private static final int DURATION = 3 * 20;

    public BlueProjectile(EntityType<? extends BlueProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public BlueProjectile(LivingEntity pShooter) {
        super(JJKEntities.BLUE.get(), pShooter.level, pShooter);

        Vec3 start = pShooter.getEyePosition();
        Vec3 view = pShooter.getLookAngle();
        Vec3 end = start.add(view.scale(RANGE));

        HitResult result = HelperMethods.getHitResult(this.level, this, start, end);

        Vec3 pos = result == null ? end : result.getLocation();
        this.moveTo(pos.x(), pos.y(), pos.z(), pShooter.getYRot(), pShooter.getXRot());
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    private void createBall() {
        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        double xStep = 0.25D;
        double yStep = 0.35D;

        for (double phi = -Math.PI; phi < Math.PI; phi += xStep) {
            float angle = (float) (Math.cos(phi) * 360.0F);

            SpinningParticle.SpinningParticleOptions options = new SpinningParticle.SpinningParticleOptions(
                    SpinningParticle.SpinningParticleOptions.BLUE_COLOR, RING_RADIUS, angle, PARTICLE_SIZE);

            this.level.addParticle(options, true, center.x(), center.y() + (yStep / 2.0D), center.z(),
                    0.0D, 0.0D, 0.0D);
        }

        for (double phi = -Math.PI; phi < Math.PI; phi += xStep) {
            float angle = (float) (Math.cos(phi) * 360.0F);

            SpinningParticle.SpinningParticleOptions options = new SpinningParticle.SpinningParticleOptions(
                    SpinningParticle.SpinningParticleOptions.BLUE_COLOR, RING_RADIUS, angle, PARTICLE_SIZE);

            this.level.addParticle(options, true, center.x(), center.y() - (yStep / 2.0D), center.z(),
                    0.0D, 0.0D, 0.0D);
        }

        for (double theta = -2.0D * Math.PI; theta < 2.0D * Math.PI; theta += yStep) {
            float radius = (float) (BALL_RADIUS * Math.cos(theta));

            for (double phi = -Math.PI; phi < Math.PI; phi += xStep) {
                float angle = (float) (Math.cos(phi) * 360.0F);

                SpinningParticle.SpinningParticleOptions options = new SpinningParticle.SpinningParticleOptions(
                        SpinningParticle.SpinningParticleOptions.BLUE_COLOR, radius, angle, PARTICLE_SIZE);

                double x = center.x();
                double y = center.y() + radius * Math.tan(theta);
                double z = center.z();

                this.level.addParticle(options, true, x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private void pullEntities() {
        AABB bounds = new AABB(this.getX() - PULL_RADIUS, this.getY() - PULL_RADIUS, this.getZ() - PULL_RADIUS,
                this.getX() + PULL_RADIUS, this.getY() + PULL_RADIUS, this.getZ() + PULL_RADIUS);

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        double strength = 0.25D;

        if (this.getOwner() instanceof LivingEntity owner) {
            for (Entity entity : this.level.getEntities(this, bounds)) {
                if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner) continue;
                if (entity instanceof Projectile projectile && projectile.getOwner() == owner) continue;

                Vec3 direction = center.subtract(entity.getX(), entity.getY() + (entity.getBbHeight() / 2.0D), entity.getZ())
                        .normalize()
                        .scale(strength);
                entity.setDeltaMovement(direction);
            }
        }
    }

    private void hurtEntities() {
        AABB bounds = new AABB(this.getX() - BALL_RADIUS, this.getY() - BALL_RADIUS, this.getZ() - BALL_RADIUS,
                this.getX() + BALL_RADIUS, this.getY() + BALL_RADIUS, this.getZ() + BALL_RADIUS);

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (Entity entity : this.level.getEntities(this, bounds)) {
                    if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner) continue;

                    entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner), DAMAGE * cap.getGrade().getPower());
                }
            });
        }
    }

    private void breakBlocks() {
        if (this.level.isClientSide) return;

        AABB bounds = new AABB(this.getX() - BALL_RADIUS, this.getY() - BALL_RADIUS, this.getZ() - BALL_RADIUS,
                this.getX() + BALL_RADIUS, this.getY() + BALL_RADIUS, this.getZ() + BALL_RADIUS);

        for (double x = bounds.minX; x <= bounds.maxX; x++) {
            for (double y = bounds.minY; y <= bounds.maxY; y++) {
                for (double z = bounds.minZ; z <= bounds.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level.getBlockState(pos);

                    if (!state.isAir() && state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > -1.0F) {
                        this.level.destroyBlock(pos, false);
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        if (this.getTime() >= DURATION) {
            this.discard();
        } else {
            if (this.getTime() % 20 == 0) {
                this.createBall();
            }
            this.pullEntities();
            this.hurtEntities();
            this.breakBlocks();
        }
        super.tick();
    }
}