package radon.jujutsu_kaisen.entity.effect.base;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.IAttack;
import radon.jujutsu_kaisen.ability.IChanneled;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.ICharged;
import radon.jujutsu_kaisen.ability.IDomainAttack;
import radon.jujutsu_kaisen.ability.IDurationable;
import radon.jujutsu_kaisen.ability.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.IToggled;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BeamEntity extends JujutsuProjectile {
    public double endPosX, endPosY, endPosZ;
    public double collidePosX, collidePosY, collidePosZ;
    public double prevCollidePosX, prevCollidePosY, prevCollidePosZ;
    public float renderYaw, renderPitch;

    public boolean on = true;

    @Nullable
    public Direction side = null;

    private static final EntityDataAccessor<Float> DATA_YAW = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PITCH = SynchedEntityData.defineId(BeamEntity.class, EntityDataSerializers.FLOAT);

    public float prevYaw;
    public float prevPitch;

    public int animation;

    protected BeamEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;

        this.update();
    }

    protected BeamEntity(EntityType<? extends Projectile> pType, LivingEntity owner, float power) {
        this(pType, owner.level());

        this.setOwner(owner);
        this.setPower(power);
    }

    public abstract int getFrames();

    public abstract float getScale();

    protected abstract double getRange();

    protected abstract float getDamage();

    protected abstract int getDuration();

    public abstract int getCharge();

    protected boolean causesFire() {
        return false;
    }

    protected boolean breaksBlocks() {
        return true;
    }

    protected boolean isStill() {
        return false;
    }

    protected boolean shouldSwing() {
        return true;
    }

    @Nullable
    protected abstract Ability getSource();

    protected Vec3 calculateSpawnPos(LivingEntity owner) {
        return new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                .add(RotationUtil.getTargetAdjustedLookAngle(owner));
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.update();
        this.calculateEndPos();
        this.checkCollisions(new Vec3(this.getX(), this.getY(), this.getZ()), new Vec3(this.endPosX, this.endPosY, this.endPosZ));
    }

    @Override
    public void tick() {
        super.tick();

        this.prevCollidePosX = this.collidePosX;
        this.prevCollidePosY = this.collidePosY;
        this.prevCollidePosZ = this.collidePosZ;
        this.prevYaw = this.renderYaw;
        this.prevPitch = this.renderPitch;
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        if (!this.isStill()) {
            this.update();
        }

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.shouldSwing()) {
                if (this.getTime() % 5 == 0) {
                    owner.swing(InteractionHand.MAIN_HAND);
                }
            }

            if (!this.on && this.animation == 0) {
                this.discard();
            }

            if (this.getFrames() > 0) {
                if (this.on) {
                    if (this.animation < this.getFrames()) {
                        this.animation++;
                    }
                } else {
                    if (this.animation > 0) {
                        this.animation--;
                    }
                }
            }

            if (this.getTime() >= this.getCharge()) {
                if (!this.isStill()) {
                    this.calculateEndPos();
                }

                List<Entity> entities = this.checkCollisions(new Vec3(this.getX(), this.getY(), this.getZ()),
                        new Vec3(this.endPosX, this.endPosY, this.endPosZ));

                for (Entity entity : entities) {
                    if (entity == owner) continue;
                    if (!entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, this.getSource()), this.getDamage() * this.getPower())) continue;

                    if (this.causesFire()) {
                        entity.setRemainingFireTicks(5 * 20);
                    }
                }

                if (!this.level().isClientSide) {
                    double radius = this.getScale() * 2.0F;

                    AABB bounds = new AABB(this.collidePosX - radius, this.collidePosY - radius, this.collidePosZ - radius,
                            this.collidePosX + radius, this.collidePosY + radius, this.collidePosZ + radius);

                    double centerX = bounds.getCenter().x;
                    double centerY = bounds.getCenter().y;
                    double centerZ = bounds.getCenter().z;

                    for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
                        for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                            for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                                BlockPos pos = new BlockPos(x, y, z);

                                if (!HelperMethods.isDestroyable((ServerLevel) this.level(), this, owner, pos)) continue;

                                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                                if (distance > radius) continue;

                                if (this.breaksBlocks()) {
                                    this.level().destroyBlock(pos, false);
                                }

                                if (!this.causesFire()) continue;

                                if (this.random.nextInt(3) == 0 && this.level().getBlockState(pos).isAir() &&
                                        this.level().getBlockState(pos.below()).isSolidRender(this.level(), pos.below())) {
                                    this.level().setBlockAndUpdate(pos, BaseFireBlock.getState(this.level(), pos));
                                }
                            }
                        }
                    }
                }
            }
            if (this.getTime() - this.getCharge() >= this.getDuration()) {
                this.on = false;
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_YAW, 0.0F);
        pBuilder.define(DATA_PITCH, 0.0F);
    }

    public float getYaw() {
        return this.entityData.get(DATA_YAW);
    }

    public void setYaw(float yaw) {
        this.entityData.set(DATA_YAW, yaw);
    }

    public float getPitch() {
        return this.entityData.get(DATA_PITCH);
    }

    public void setPitch(float pitch) {
        this.entityData.set(DATA_PITCH, pitch);
    }

    private void calculateEndPos() {
        if (this.level().isClientSide) {
            this.endPosX = this.getX() + this.getRange() * Math.cos(this.renderYaw) * Math.cos(this.renderPitch);
            this.endPosZ = this.getZ() + this.getRange() * Math.sin(this.renderYaw) * Math.cos(this.renderPitch);
            this.endPosY = this.getY() + this.getRange() * Math.sin(this.renderPitch);
        } else {
            this.endPosX = this.getX() + this.getRange() * Math.cos(this.getYaw()) * Math.cos(this.getPitch());
            this.endPosZ = this.getZ() + this.getRange() * Math.sin(this.getYaw()) * Math.cos(this.getPitch());
            this.endPosY = this.getY() + this.getRange() * Math.sin(this.getPitch());
        }
    }

    public List<Entity> checkCollisions(Vec3 from, Vec3 to) {
        if (!(this.getOwner() instanceof LivingEntity owner)) return List.of();

        BlockHitResult result = this.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

        if (result.getType() != HitResult.Type.MISS) {
            Vec3 pos = result.getLocation();
            this.collidePosX = pos.x;
            this.collidePosY = pos.y;
            this.collidePosZ = pos.z;
            this.side = result.getDirection();
        } else {
            this.collidePosX = this.endPosX;
            this.collidePosY = this.endPosY;
            this.collidePosZ = this.endPosZ;
            this.side = null;
        }
        List<Entity> entities = new ArrayList<>();

        AABB bounds = new AABB(Math.min(this.getX(), this.collidePosX), Math.min(this.getY(), this.collidePosY),
                Math.min(this.getZ(), this.collidePosZ), Math.max(this.getX(), this.collidePosX),
                Math.max(this.getY(), this.collidePosY), Math.max(this.getZ(), this.collidePosZ))
                .inflate(this.getScale());

        for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, this.level(), owner, bounds)) {
            float pad = entity.getPickRadius() + 0.5F;
            AABB padded = entity.getBoundingBox().inflate(pad, pad, pad);
            Optional<Vec3> hit = padded.clip(from, to);

            if (padded.contains(from)) {
                entities.add(entity);
            } else if (hit.isPresent()) {
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024;
    }

    private void update() {
        if (this.getOwner() instanceof LivingEntity owner) {
            this.renderYaw = (float) ((RotationUtil.getTargetAdjustedYRot(owner) + 90.0D) * Math.PI / 180.0D);
            this.renderPitch = (float) (-RotationUtil.getTargetAdjustedXRot(owner) * Math.PI / 180.0D);
            this.setYaw((float) ((RotationUtil.getTargetAdjustedYRot(owner) + 90.0F) * Math.PI / 180.0D));
            this.setPitch((float) (-RotationUtil.getTargetAdjustedXRot(owner) * Math.PI / 180.0D));
            Vec3 spawn = this.calculateSpawnPos(owner);
            this.setPos(spawn.x, spawn.y, spawn.z);
        }
    }
}
