package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.List;

public class MeteorEntity extends JujutsuProjectile {
    private static final EntityDataAccessor<Integer> DATA_EXPLOSION_TIME = SynchedEntityData.defineId(MeteorEntity.class, EntityDataSerializers.INT);

    public static final int SIZE = 5;
    public static final int HEIGHT = 30;
    private static final int MAX_SIZE = 15;
    public static final int DELAY = 3 * 20;
    private static final double SPEED = 3.0D;
    private static final float DAMAGE = 10.0F;

    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    public MeteorEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public MeteorEntity(LivingEntity owner, float power) {
        super(JJKEntities.METEOR.get(), owner.level(), owner, power);

        owner.setPos(owner.position().add(0.0D, MeteorEntity.HEIGHT + MeteorEntity.getSize(power), 0.0D));

        this.applyOffset();
    }

    @Override
    protected int getDuration() {
        return DELAY + 5 * 20;
    }

    @Override
    public void push(@NotNull Entity pEntity) {

    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_EXPLOSION_TIME, 0);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    protected float ridingOffset(@NotNull Entity pEntity) {
        return this.getBbHeight() * 1.5F;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.setExplosionTime(pCompound.getInt("explosion_time"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("explosion_time", this.getExplosionTime());
    }

    private float getFrictionInfluencedSpeed(float pFriction) {
        return this.onGround() ? 0.7F * (0.21600002F / (pFriction * pFriction * pFriction)) : 0.02F;
    }

    public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 pDeltaMovement, float pFriction) {
        this.moveRelative(this.getFrictionInfluencedSpeed(pFriction), pDeltaMovement);
        this.move(MoverType.SELF, this.getDeltaMovement());
        return this.getDeltaMovement();
    }

    private void doPush(Entity entity) {
        entity.push(this);
    }

    private void pushEntities() {
        AABB bounds = AABB.ofSize(this.position(), this.getSize() * 2, this.getSize() * 2, this.getSize() * 2);

        if (this.level().isClientSide) {
            this.level().getEntities(EntityTypeTest.forClass(Player.class), bounds, EntitySelector.pushableBy(this)).forEach(this::doPush);
        } else {
            List<Entity> entities = this.level().getEntities(this, bounds, EntitySelector.pushableBy(this));

            if (!entities.isEmpty()) {
                int i = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);

                if (i > 0 && entities.size() > i - 1 && this.random.nextInt(4) == 0) {
                    int j = 0;

                    for (Entity entity : entities) {
                        if (!entity.isPassenger()) {
                            ++j;
                        }
                    }

                    if (j > i - 1) {
                        this.hurt(this.damageSources().cramming(), 6.0F);
                    }
                }

                for (Entity entity : entities) {
                    this.doPush(entity);
                }
            }
        }
    }

    @Override
    public void lerpTo(double pX, double pY, double pZ, float pYaw, float pPitch, int pPosRotationIncrements) {
        this.lerpX = pX;
        this.lerpY = pY;
        this.lerpZ = pZ;
        this.lerpYRot = pYaw;
        this.lerpXRot = pPitch;
        this.lerpSteps = pPosRotationIncrements;
    }

    public void travel() {
        if (this.isControlledByLocalInstance()) {
            double d0 = 0.08D;

            BlockPos pos = this.getBlockPosBelowThatAffectsMyMovement();
            float f2 = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getFriction(level(), this.getBlockPosBelowThatAffectsMyMovement(), this);
            float f3 = this.onGround() ? f2 * 0.91F : 0.91F;
            Vec3 vec35 = this.handleRelativeFrictionAndCalculateMovement(Vec3.ZERO, f2);

            double d2 = vec35.y;

            if (this.level().isClientSide && !this.level().hasChunkAt(pos)) {
                if (this.getY() > (double) this.level().getMinBuildHeight()) {
                    d2 = -0.1D;
                } else {
                    d2 = 0.0D;
                }
            } else if (!this.isNoGravity()) {
                d2 -= d0;
            }
            this.setDeltaMovement(vec35.x * (double) f3, d2 * (double) 0.98F, vec35.z * (double) f3);
        }
    }

    public void aiStep() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double d2 = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double d4 = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double d6 = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
            this.setYRot(this.getYRot() + (float) d6 / (float) this.lerpSteps);
            this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d2, d4);
            this.setRot(this.getYRot(), this.getXRot());
        } else if (!this.isEffectiveAi()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }

        Vec3 vec31 = this.getDeltaMovement();
        double d1 = vec31.x;
        double d3 = vec31.y;
        double d5 = vec31.z;

        if (Math.abs(vec31.x) < 0.003D) {
            d1 = 0.0D;
        }
        if (Math.abs(vec31.y) < 0.003D) {
            d3 = 0.0D;
        }
        if (Math.abs(vec31.z) < 0.003D) {
            d5 = 0.0D;
        }
        this.setDeltaMovement(d1, d3, d5);
        this.travel();
        this.pushEntities();
    }

    public static int getSize(float power) {
        return Math.min(MAX_SIZE, Math.round(SIZE * power));
    }

    public int getSize() {
        return Math.round(getSize(this.getPower()) * ((float) Math.min(DELAY, this.getTime()) / DELAY));
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        int size = this.getSize();
        return EntityDimensions.fixed(size, size);
    }

    private void hurtEntities() {
        double radius = this.getSize();
        AABB bounds = this.getBoundingBox().inflate(radius);

        if (this.getOwner() instanceof LivingEntity owner) {
            for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, this.level(), owner, bounds)) {
                if (Math.sqrt(entity.distanceToSqr(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ())) >= this.getSize()) continue;
                if (!entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.MAXIMUM_METEOR.get()), DAMAGE * this.getPower())) continue;
                entity.setSecondsOnFire(10);
            }
        }
    }

    @Override
    protected boolean isProjectile() {
        return false;
    }

    private void spawnParticles() {
        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());

        float radius = this.getSize() * 1.1F;
        int count = (int) (radius * Math.PI * 2) / 2;

        for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0D;
            double phi = this.random.nextDouble() * Math.PI;

            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * Math.cos(phi);

            double x = center.x + xOffset;
            double y = center.y + yOffset;
            double z = center.z + zOffset;

            this.level().addParticle(new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.FIRE_ORANGE, radius * 0.4F, 0.25F, true, 20),
                    true, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    private void evaporateWater() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        float radius = this.getSize();
        AABB bounds = AABB.ofSize(this.position(), radius * 2, radius * 2, radius * 2);

        for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
            for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = this.level().getBlockState(pos);

                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance > radius) continue;

                    if (!HelperMethods.isDestroyable((ServerLevel) this.level(), this, owner, pos)) continue;

                    if (!state.getFluidState().isEmpty()) {
                        this.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        this.level().levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
                    }
                }
            }
        }
    }

    private void breakBlocks() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        float radius = this.getSize();
        AABB bounds = AABB.ofSize(this.position().add(0.0D, this.getBbHeight() / 2.0F, 0.0D), radius * 2, radius * 2, radius * 2);
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

                    if (!HelperMethods.isDestroyable((ServerLevel) this.level(), this, owner, pos)) continue;

                    if (state.getFluidState().isEmpty()) {
                        this.level().destroyBlock(pos, false);
                    }
                }
            }
        }
    }

    public int getExplosionTime() {
        return this.entityData.get(DATA_EXPLOSION_TIME);
    }

    private void setExplosionTime(int time) {
        this.entityData.set(DATA_EXPLOSION_TIME, time);
    }

    private void applyOffset() {
        Entity owner = this.getOwner();

        if (owner == null) return;

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        this.setPos(owner.position().subtract(look.multiply(this.getBbWidth() * 2, this.getSize() * 2, this.getBbWidth() * 2)));
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        this.spawnParticles();

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() >= DELAY && this.getExplosionTime() == 0) {
                this.move(MoverType.SELF, this.getDeltaMovement());
            } else {
                this.aiStep();
            }

            if (this.getTime() < DELAY) {
                Vec3 movement = owner.getDeltaMovement();

                if (movement.y < 0.0D) {
                    owner.setDeltaMovement(movement.x, 0.0D, movement.z);
                    owner.moveRelative(0.02F * 2, new Vec3(owner.xxa, 0.0D, owner.zza));
                }

                if (!this.level().isClientSide) {
                    this.applyOffset();
                }
            } else if (!this.level().isClientSide) {
                this.hurtEntities();

                if (this.getTime() == DELAY) {
                    this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
                }

                int duration = this.getSize() * 5;

                int time = this.getExplosionTime();

                if (time > 0) {
                    if (time >= duration) {
                        this.discard();
                    } else {
                        this.setExplosionTime(++time);
                    }
                } else {
                    Vec3 start = this.position();
                    Vec3 end = start.add(this.getDeltaMovement().scale((double) this.getSize() / 2));

                    BlockHitResult clip = this.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this));

                    if (!this.level().getBlockState(clip.getBlockPos()).isAir()) {
                        this.setExplosionTime(1);

                        ExplosionHandler.spawn(this.level().dimension(), clip.getLocation(), this.getSize() * 1.5F, duration, this.getPower() * 0.5F, owner,
                                JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.MAXIMUM_METEOR.get()), true);
                    }
                    this.breakBlocks();
                }
                this.evaporateWater();
            }
        }
    }
}
