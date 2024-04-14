package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class BlueProjectile extends JujutsuProjectile {
    private static final EntityDataAccessor<Boolean> DATA_MOTION = SynchedEntityData.defineId(BlueProjectile.class, EntityDataSerializers.BOOLEAN);

    public static final double RANGE = 15.0D;
    private static final int DELAY = 20;
    private static final float DAMAGE = 3.0F;
    private static final int DURATION = 5 * 20;
    private static final float RADIUS = 3.0F;
    private static final float MAX_RADIUS = 5.0F;
    private static final double OFFSET = 8.0D;

    public BlueProjectile(EntityType<? extends BlueProjectile> pType, Level level) {
        super(pType, level);
    }

    public BlueProjectile(EntityType<? extends BlueProjectile> pType, Level level, LivingEntity owner, float power) {
        super(pType, level, owner, power);
    }

    public BlueProjectile(LivingEntity owner, float power, boolean motion) {
        this(JJKEntities.BLUE.get(), owner.level(), owner, power);

        this.entityData.set(DATA_MOTION, motion);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_MOTION, false);
    }

    private void pullEntities() {
        float radius = this.getRadius();
        AABB bounds = new AABB(this.getX() - radius, this.getY() - radius, this.getZ() - radius,
                this.getX() + radius, this.getY() + radius, this.getZ() + radius);

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, this.level(), owner, bounds)) {
            if (!(entity instanceof LivingEntity) && !(entity instanceof FallingBlockEntity)) continue;

            Vec3 direction = center.subtract(entity.getX(), entity.getY() + (entity.getBbHeight() / 2.0D), entity.getZ()).normalize();
            entity.setDeltaMovement(direction);
            entity.hurtMarked = true;
        }
    }

    private float getRadius() {
        return Math.max(Mth.PI, Math.min(MAX_RADIUS, RADIUS * this.getPower()));
    }

    private void breakBlocks() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        float radius = this.getRadius();
        AABB bounds = this.getBoundingBox().inflate(radius);

        double centerX = bounds.getCenter().x;
        double centerY = bounds.getCenter().y;
        double centerZ = bounds.getCenter().z;

        for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
            for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level().getBlockState(pos);

                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance > radius) continue;

                    if (HelperMethods.isDestroyable((ServerLevel) this.level(), this, owner, pos)) {
                        if (state.getFluidState().isEmpty()) {
                            this.level().destroyBlock(pos, false);
                        } else {
                            this.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        }
    }

    private void hurtEntities() {
        AABB bounds = this.getBoundingBox();

        if (this.getOwner() instanceof LivingEntity owner) {
            for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, this.level(), owner, bounds)) {
                if (entity instanceof Projectile projectile && projectile.getOwner() == owner) continue;

                if (entity instanceof Attackable) {
                    entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, this.entityData.get(DATA_MOTION) ? JJKAbilities.BLUE_MOTION.get() : JJKAbilities.BLUE_STILL.get()), DAMAGE * this.getPower());
                } else if (entity instanceof AbstractArrow || entity instanceof FallingBlockEntity) {
                    entity.discard();
                }
            }
        }
    }

    private void pullBlocks() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        float radius = this.getRadius() * 2.0F;
        AABB bounds = new AABB(this.getX() - radius, this.getY() - radius, this.getZ() - radius,
                this.getX() + radius, this.getY() + radius, this.getZ() + radius);

        double centerX = bounds.getCenter().x;
        double centerY = bounds.getCenter().y;
        double centerZ = bounds.getCenter().z;

        for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
            for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                    if (this.random.nextInt(Math.round(radius * 2 * 20)) != 0) continue;

                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level().getBlockState(pos);

                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance > radius) continue;

                    if (HelperMethods.isDestroyable((ServerLevel) this.level(), this, owner, pos)) {
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

    private void spawnParticles() {
        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        float radius = this.getRadius() * (this.getTime() < DELAY ? 0.25F : 1.0F);
        int count = (int) (radius * Math.PI * 2) * 2;

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * Math.cos(phi);

            double x = center.x + xOffset * (radius * 0.1F);
            double y = center.y + yOffset * (radius * 0.1F);
            double z = center.z + zOffset * (radius * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.DARK_BLUE, radius * 0.075F, 1.0F, true, 5),
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * 0.5F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.5F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.5F * Math.cos(phi);

            double x = center.x + xOffset * (radius * 0.5F * 0.1F);
            double y = center.y + yOffset * (radius * 0.5F * 0.1F);
            double z = center.z + zOffset * (radius * 0.5F * 0.1F);

            this.level().addParticle(new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.LIGHT_BLUE, radius * 0.05F, 1.0F, true, 5),
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        float radius = this.getRadius();
        return EntityDimensions.fixed(radius, radius);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATA_MOTION, pCompound.getBoolean("motion"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putBoolean("motion", this.entityData.get(DATA_MOTION));
    }

    private void spin() {
        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() % 5 == 0) {
                owner.swing(InteractionHand.MAIN_HAND);
            }
            Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
            EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look.scale(OFFSET)));
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (this.entityData.get(DATA_MOTION)) {
            if (this.getTime() >= DELAY) {
                this.spin();
            }
        }

        if (this.getTime() >= DURATION) {
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
                        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
                        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look));
                    }
                } else {
                    if (this.getTime() == DELAY) {
                        Vec3 start = owner.getEyePosition();
                        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
                        Vec3 end = start.add(look.scale(RANGE));
                        HitResult result = RotationUtil.getHitResult(owner, start, end);

                        Vec3 pos = result.getType() == HitResult.Type.MISS ? end : result.getLocation();
                        this.setPos(pos.subtract(0.0D, this.getBbHeight() / 2.0F, 0.0D));
                    }
                    this.pullEntities();
                    this.hurtEntities();

                    if (!this.level().isClientSide) {
                        this.pullBlocks();
                        this.breakBlocks();
                    }
                }
            }
        }
    }
}