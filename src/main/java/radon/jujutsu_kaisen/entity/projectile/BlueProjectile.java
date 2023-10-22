package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class BlueProjectile extends JujutsuProjectile {
    private static final double RANGE = 15.0D;
    private static final int DELAY = 20;

    public BlueProjectile(EntityType<? extends BlueProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public BlueProjectile(EntityType<? extends BlueProjectile> pEntityType, Level level, LivingEntity owner, float power) {
        super(pEntityType, level, owner, power);
    }

    public BlueProjectile(LivingEntity owner, float power) {
        this(JJKEntities.BLUE.get(), owner.level(), owner, power);

        Vec3 look = HelperMethods.getLookAngle(owner);
        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
    }

    public float getRadius() {
        return 3.0F;
    }
    protected int getDuration() {
        return 5 * 20;
    }

    protected float getDamage() {
        return 3.0F;
    }

    private void pullEntities() {
        AABB bounds = new AABB(this.getX() - this.getRadius(), this.getY() - this.getRadius(), this.getZ() - this.getRadius(),
                this.getX() + this.getRadius(), this.getY() + this.getRadius(), this.getZ() + this.getRadius());

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        if (this.getOwner() instanceof LivingEntity owner) {
            for (Entity entity : this.level().getEntities(owner, bounds)) {
                if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || (entity instanceof Projectile projectile && projectile.getOwner() == owner)) continue;

                Vec3 direction = center.subtract(entity.getX(), entity.getY() + (entity.getBbHeight() / 2.0D), entity.getZ()).normalize();
                entity.setDeltaMovement(direction);
                entity.hurtMarked = true;
            }
        }
    }

    private void breakBlocks() {
        AABB bounds = this.getBoundingBox().inflate(this.getRadius());
        double centerX = bounds.getCenter().x();
        double centerY = bounds.getCenter().y();
        double centerZ = bounds.getCenter().z();

        for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
            for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level().getBlockState(pos);

                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance <= this.getRadius()) {
                        if (state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                            this.level().destroyBlock(pos, false);
                        }
                    }
                }
            }
        }
    }

    private void hurtEntities() {
        AABB bounds = this.getBoundingBox();

        if (this.getOwner() instanceof LivingEntity owner) {
            for (Entity entity : HelperMethods.getEntityCollisions(this.level(), bounds)) {
                if (entity instanceof RedProjectile || (entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner || entity == this) continue;

                if (entity instanceof LivingEntity) {
                    entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.BLUE.get()), this.getDamage() * this.getPower());
                } else {
                    entity.discard();
                }
            }
        }
    }

    private void pullBlocks() {
        double radius = this.getRadius() * 2.0F;
        AABB bounds = new AABB(this.getX() - radius, this.getY() - radius, this.getZ() - radius,
                this.getX() + radius, this.getY() + radius, this.getZ() + radius);
        double centerX = bounds.getCenter().x();
        double centerY = bounds.getCenter().y();
        double centerZ = bounds.getCenter().z();

        for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
            for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                    if (this.random.nextInt((int) this.getRadius() * 2 * 20) != 0) continue;

                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level().getBlockState(pos);

                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance <= radius) {
                        if (!state.isAir() && state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                            if (this.level().destroyBlock(pos, false)) {
                                FallingBlockEntity entity = FallingBlockEntity.fall(this.level(), pos, state);
                                entity.noPhysics = true;

                                if (((ServerLevel) this.level()).getEntity(entity.getUUID()) == null) {
                                    this.level().addFreshEntity(entity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private void spawnParticles() {
        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        float radius = this.getRadius();
        int count = (int) (radius * Math.PI * 2);

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * Math.cos(phi);

            double x = center.x() + xOffset * (radius * 0.1F);
            double y = center.y() + yOffset * (radius * 0.1F);
            double z = center.z() + zOffset * (radius * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.DARK_BLUE_COLOR, 0.1F, 1.0F, 5),
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.5F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.5F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.5F * Math.cos(phi);

            double x = center.x() + xOffset * (radius * 0.5F * 0.1F);
            double y = center.y() + yOffset * (radius * 0.5F * 0.1F);
            double z = center.z() + zOffset * (radius * 0.5F * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.LIGHT_BLUE_COLOR, 0.1F, 1.0F, 5),
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return EntityDimensions.fixed(this.getRadius(), this.getRadius());
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (this.getTime() >= this.getDuration()) {
            this.discard();
        } else {
            this.spawnParticles();

            if (this.getOwner() instanceof LivingEntity owner) {
                if (this.getTime() < DELAY) {
                    if (!owner.isAlive()) {
                        this.discard();
                    } else {
                        if (this.getTime() % 5 == 0) {
                            owner.swing(InteractionHand.MAIN_HAND);
                        }
                        Vec3 look = HelperMethods.getLookAngle(owner);
                        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
                        this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
                    }
                } else {
                    if (this.getTime() == DELAY) {
                        Vec3 start = owner.getEyePosition();
                        Vec3 look = HelperMethods.getLookAngle(owner);
                        Vec3 end = start.add(look.scale(RANGE));
                        HitResult result = HelperMethods.getHitResult(owner, start, end);

                        Vec3 pos = result.getType() == HitResult.Type.MISS ? end : result.getLocation();
                        this.setPos(pos.subtract(0.0D, this.getBbHeight() / 2.0F, 0.0D));
                    }
                    this.pullEntities();
                    this.hurtEntities();

                    if (!this.level().isClientSide) {
                        if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                            this.pullBlocks();
                            this.breakBlocks();
                        }
                    }
                }
            }
        }
    }
}