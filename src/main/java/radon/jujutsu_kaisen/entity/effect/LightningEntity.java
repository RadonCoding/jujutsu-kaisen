package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LightningEntity extends JujutsuProjectile {
    public static final float SCALE = 1.0F;
    public static final double RANGE = 20;

    public double endPosX, endPosY, endPosZ;
    public double collidePosX, collidePosY, collidePosZ;

    public int life;

    public @Nullable Direction side = null;

    private static final EntityDataAccessor<Float> DATA_YAW = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PITCH = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);

    public int animation;

    public LightningEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
        this.life = 4;
    }

    public LightningEntity(EntityType<? extends Projectile> pType, LivingEntity owner, float power) {
        this(pType, owner.level());

        this.setOwner(owner);
        this.setPower(power);
    }

    public LightningEntity(LivingEntity owner, float power) {
        this(JJKEntities.LIGHTNING.get(), owner, power);

        this.setOwner(owner);
        this.setPower(power);
    }

    protected float getDamage() {
        return 20.0F;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (this.getOwner() instanceof LivingEntity owner) {
            this.setYaw((float) ((owner.getYRot() + 90.0F) * Math.PI / 180.0D));
            this.setPitch((float) (-owner.getXRot() * Math.PI / 180.0D));

            Vec3 spawn = this.calculateStartPos();
            this.setPos(spawn.x, spawn.y, spawn.z);
        }
        this.calculateEndPos();
        this.checkCollisions(new Vec3(this.getX(), this.getY(), this.getZ()), new Vec3(this.endPosX, this.endPosY, this.endPosZ));
    }

    @Override
    public void tick() {
        super.tick();

        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        if (this.life == 2) {
            if (this.getOwner() instanceof LivingEntity owner) {
                this.calculateEndPos();

                List<Entity> entities = this.checkCollisions(new Vec3(this.getX(), this.getY(), this.getZ()),
                        new Vec3(this.endPosX, this.endPosY, this.endPosZ));

                if (!this.level().isClientSide) {
                    for (Entity entity : entities) {
                        this.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);

                        entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.LIGHTNING.get()),
                                this.getDamage() * this.getPower());
                    }

                    double radius = SCALE * 2.0F;

                    AABB bounds = new AABB(this.collidePosX - radius, this.collidePosY - radius, this.collidePosZ - radius,
                            this.collidePosX + radius, this.collidePosY + radius, this.collidePosZ + radius);

                    for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
                        for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                            for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                                BlockPos pos = new BlockPos(x, y, z);

                                double distance = Math.sqrt(x * x + y * y + z * z);

                                if (distance <= radius) {
                                    if (HelperMethods.isDestroyable((ServerLevel) this.level(), this, owner, pos)) {
                                        this.level().destroyBlock(pos, false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (--this.life == 0) {
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_YAW, 0.0F);
        this.entityData.define(DATA_PITCH, 0.0F);
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

    protected Vec3 calculateStartPos() {
        Entity owner = this.getOwner();

        if (owner == null) return Vec3.ZERO;

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        return new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
    }

    protected void calculateEndPos() {
        this.endPosX = this.getX() + RANGE * Math.cos(this.getYaw()) * Math.cos(this.getPitch());
        this.endPosZ = this.getZ() + RANGE * Math.sin(this.getYaw()) * Math.cos(this.getPitch());
        this.endPosY = this.getY() + RANGE * Math.sin(this.getPitch());
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
                .inflate(SCALE);

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
        return distance < 1024.0D;
    }
}