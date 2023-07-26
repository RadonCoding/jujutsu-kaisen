package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class BlueProjectile extends JujutsuProjectile {
    private static final double RANGE = 10.0D;

    private static final double PULL_STRENGTH = 0.25D;
    private static final double X_STEP = 0.15D;
    private static final double Y_STEP = 0.25D;

    public BlueProjectile(EntityType<? extends BlueProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public BlueProjectile(EntityType<? extends BlueProjectile> pEntityType, Level level, LivingEntity pShooter) {
        super(pEntityType, level, pShooter);
    }

    public BlueProjectile(LivingEntity pShooter) {
        this(JJKEntities.BLUE.get(), pShooter.level, pShooter);

        Vec3 start = pShooter.getEyePosition();
        Vec3 look = pShooter.getLookAngle();
        Vec3 end = start.add(look.scale(RANGE));
        HitResult result = HelperMethods.getHitResult(pShooter, start, end);

        Vec3 pos = result.getType() == HitResult.Type.MISS ? end : result.getLocation();
        this.setPos(pos);
    }

    public float getRadius() {
        return 3.0F;
    }

    protected double getPullRadius() {
        return this.getRadius() * 2;
    }

    protected int getInterval() {
        return 20;
    }

    protected int getDuration() {
        return 3 * 20;
    }

    protected float getDamage() {
        return 2.5F;
    }

    private void pullEntities() {
        AABB bounds = new AABB(this.getX() - this.getPullRadius(), this.getY() - this.getPullRadius(), this.getZ() - this.getPullRadius(),
                this.getX() + this.getPullRadius(), this.getY() + this.getPullRadius(), this.getZ() + this.getPullRadius());

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        if (this.getOwner() instanceof LivingEntity owner) {
            for (Entity entity : this.level.getEntities(this, bounds)) {
                if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner) continue;
                if (entity instanceof Projectile projectile && projectile.getOwner() == owner) continue;

                Vec3 direction = center.subtract(entity.getX(), entity.getY() + (entity.getBbHeight() / 2.0D), entity.getZ()).scale(PULL_STRENGTH);
                entity.setDeltaMovement(direction);
            }
        }
    }

    private void hurtEntities() {
        AABB bounds = new AABB(this.getX() - this.getRadius(), this.getY() - this.getRadius(), this.getZ() - this.getRadius(),
                this.getX() + this.getRadius(), this.getY() + this.getRadius(), this.getZ() + this.getRadius());

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (Entity entity : HelperMethods.getEntityCollisions(this.level, bounds)) {
                    if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner) continue;

                    entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner), this.getDamage() * cap.getGrade().getPower());
                }
            });
        }
    }

    private void breakBlocks() {
        AABB bounds = this.getBoundingBox().inflate(this.getRadius());
        double centerX = bounds.getCenter().x;
        double centerY = bounds.getCenter().y;
        double centerZ = bounds.getCenter().z;

        for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
            for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level.getBlockState(pos);

                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance <= this.getRadius()) {
                        if (state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                            this.level.destroyBlock(pos, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getTime() >= this.getDuration()) {
            this.discard();
        } else {
            this.pullEntities();
            this.hurtEntities();

            if (!this.level.isClientSide) {
                this.breakBlocks();
            }
        }
    }
}