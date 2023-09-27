package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LightningEntity extends JujutsuProjectile {
    public static final float SCALE = 1.0F;
    private static final double RADIUS = 20;
    private static final float DAMAGE = 10.0F;

    public double endPosX, endPosY, endPosZ;
    public double collidePosX, collidePosY, collidePosZ;
    public double prevCollidePosX, prevCollidePosY, prevCollidePosZ;
    public float renderYaw, renderPitch;

    public long seed;
    private int life;
    private int flashes;

    public @Nullable Direction side = null;

    private static final EntityDataAccessor<Float> DATA_YAW = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PITCH = SynchedEntityData.defineId(LightningEntity.class, EntityDataSerializers.FLOAT);

    public float prevYaw;
    public float prevPitch;

    public LightningEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
        this.seed = this.random.nextLong();
        this.life = 2;
        this.flashes = this.random.nextInt(3) + 1;
    }

    public LightningEntity(LivingEntity pShooter, float yaw, float pitch) {
        this(JJKEntities.LIGHTNING.get(), pShooter.level());

        this.setOwner(pShooter);

        this.setYaw(yaw);
        this.setPitch(pitch);

        Vec3 look = HelperMethods.getLookAngle(pShooter);
        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ()).add(look);
        this.setPos(spawn.x(), spawn.y(), spawn.z());

        this.calculateEndPos();
    }

    public float getScale() {
        if (!(this.getOwner() instanceof RikaEntity rika)) return SCALE;
        return SCALE * (rika.isOpen() ? 1.0F : 0.5F);
    }

    public double getRadius() {
        if (!(this.getOwner() instanceof RikaEntity rika)) return RADIUS;
        return RADIUS * (rika.isOpen() ? 1.0D : 0.5D);
    }

    public float getDamage() {
        if (!(this.getOwner() instanceof RikaEntity rika)) return DAMAGE;
        return DAMAGE * (rika.isOpen() ? 1.0F : 0.5F);
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

        if (!this.level().isClientSide) {
            this.update();
        }

        if (this.life == 2) {
            if (this.getOwner() instanceof LivingEntity owner) {
                this.renderYaw = (float) ((owner.getYRot() + 90.0D) * Math.PI / 180.0D);
                this.renderPitch = (float) (-owner.getXRot() * Math.PI / 180.0D);

                this.calculateEndPos();

                List<Entity> entities = this.checkCollisions(new Vec3(this.getX(), this.getY(), this.getZ()),
                        new Vec3(this.endPosX, this.endPosY, this.endPosZ));

                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    for (Entity entity : entities) {
                        if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner)
                            continue;

                        entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.SHOOT_PURE_LOVE.get()),
                                this.getDamage() * cap.getGrade().getRealPower(owner));
                    }
                });

                if (!this.level().isClientSide) {
                    if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                        double radius = this.getScale() * 2.0F;

                        AABB bounds = new AABB(this.collidePosX - radius, this.collidePosY - radius, this.collidePosZ - radius,
                                this.collidePosX + radius, this.collidePosY + radius, this.collidePosZ + radius);
                        double centerX = bounds.getCenter().x();
                        double centerY = bounds.getCenter().y();
                        double centerZ = bounds.getCenter().z();

                        for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
                            for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                                for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                                    BlockPos pos = new BlockPos(x, y, z);
                                    BlockState state = this.level().getBlockState(pos);

                                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                                    if (distance <= radius) {
                                        if (state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                                            this.level().destroyBlock(pos, false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (this.life == 2) {
            if (this.level().isClientSide) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
            }
        }

        --this.life;

        if (this.life < 0) {
            if (this.flashes == 0) {
                this.discard();
            } else if (this.life < -this.random.nextInt(10)) {
                --this.flashes;
                this.life = 1;
                this.seed = this.random.nextLong();
            }
        }

        if (this.life >= 0) {
            if (!(this.level() instanceof ServerLevel)) {
                this.level().setSkyFlashTime(2);
            }
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

    private void calculateEndPos() {
        if (this.level().isClientSide) {
            this.endPosX = this.getX() + this.getRadius() * Math.cos(this.renderYaw) * Math.cos(this.renderPitch);
            this.endPosZ = this.getZ() + this.getRadius() * Math.sin(this.renderYaw) * Math.cos(this.renderPitch);
            this.endPosY = this.getY() + this.getRadius() * Math.sin(this.renderPitch);
        } else {
            this.endPosX = this.getX() + this.getRadius() * Math.cos(this.getYaw()) * Math.cos(this.getPitch());
            this.endPosZ = this.getZ() + this.getRadius() * Math.sin(this.getYaw()) * Math.cos(this.getPitch());
            this.endPosY = this.getY() + this.getRadius() * Math.sin(this.getPitch());
        }
    }

    public List<Entity> checkCollisions(Vec3 from, Vec3 to) {
        BlockHitResult result = this.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

        if (result.getType() != HitResult.Type.MISS) {
            Vec3 pos = result.getLocation();
            this.collidePosX = pos.x();
            this.collidePosY = pos.y();
            this.collidePosZ = pos.z();
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

        for (Entity entity : HelperMethods.getEntityCollisions(this.level(), bounds)) {
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
            this.setYaw((float) ((owner.getYRot() + 90.0F) * Math.PI / 180.0D));
            this.setPitch((float) (-owner.getXRot() * Math.PI / 180.0D));
            Vec3 look = HelperMethods.getLookAngle(owner);
            Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
            this.setPos(spawn.x(), spawn.y(), spawn.z());
        }
    }
}