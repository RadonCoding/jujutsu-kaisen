package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import radon.jujutsu_kaisen.entity.HollowPurpleExplosion;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.projectile.MaximumBlueProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaximumRedBeam extends JujutsuProjectile {
    public static final int FRAMES = 3;
    public static final float SCALE = 1.0F;
    private static final double RADIUS = 20;
    private static final float DAMAGE = 25.0F;
    public static final int CHARGE = 20;
    public static final int DURATION = 20;

    public double endPosX, endPosY, endPosZ;
    public double collidePosX, collidePosY, collidePosZ;
    public double prevCollidePosX, prevCollidePosY, prevCollidePosZ;
    public float renderYaw, renderPitch;

    public boolean on = true;

    public @Nullable Direction side = null;

    private static final EntityDataAccessor<Float> DATA_YAW = SynchedEntityData.defineId(MaximumRedBeam.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_PITCH = SynchedEntityData.defineId(MaximumRedBeam.class, EntityDataSerializers.FLOAT);

    public float prevYaw;
    public float prevPitch;

    public int animation;

    public MaximumRedBeam(EntityType<? extends MaximumRedBeam> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public MaximumRedBeam(LivingEntity pShooter, float yaw, float pitch) {
        this(JJKEntities.MAXIMUM_RED.get(), pShooter.level);

        this.setOwner(pShooter);

        this.setYaw(yaw);
        this.setPitch(pitch);

        Vec3 look = HelperMethods.getLookAngle(pShooter);
        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ()).add(look);
        this.setPos(spawn.x(), spawn.y(), spawn.z());

        this.calculateEndPos();
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

        if (!this.level.isClientSide) {
            this.update();
        }

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() % 5 == 0) {
                owner.swing(InteractionHand.MAIN_HAND);
            }
            this.renderYaw = (float) ((owner.getYRot() + 90.0D) * Math.PI / 180.0D);
            this.renderPitch = (float) (-owner.getXRot() * Math.PI / 180.0D);

            if (!this.on && this.animation == 0) {
                this.discard();
            }

            if (this.on) {
                if (this.animation < FRAMES) {
                    this.animation++;
                }
            } else {
                if (this.animation > 0) {
                    this.animation--;
                }
            }

            if (this.getTime() > CHARGE) {
                this.calculateEndPos();

                List<Entity> entities = this.checkCollisions(new Vec3(this.getX(), this.getY(), this.getZ()),
                        new Vec3(this.endPosX, this.endPosY, this.endPosZ));

                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    for (Entity entity : entities) {
                        if (this.getTime() - 1 > CHARGE && entity instanceof MaximumBlueProjectile projectile) {
                            if (cap.isCooldownDone(JJKAbilities.HOLLOW_PURPLE.get()) && cap.isCooldownDone(JJKAbilities.MAXIMUM_HOLLOW_PURPLE.get())) {
                                cap.addCooldown(owner, JJKAbilities.MAXIMUM_RED.get());
                                cap.addCooldown(owner, JJKAbilities.MAXIMUM_BLUE.get());
                                cap.addCooldown(owner, JJKAbilities.HOLLOW_PURPLE.get());
                                cap.addCooldown(owner, JJKAbilities.MAXIMUM_HOLLOW_PURPLE.get());

                                this.level.addFreshEntity(new HollowPurpleExplosion(owner, projectile.position()));

                                projectile.discard();
                                this.discard();
                                return;
                            }
                        }

                        if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner) continue;

                        if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.MAXIMUM_RED.get()),
                                DAMAGE * cap.getGrade().getPower(owner))) {
                            entity.setDeltaMovement(this.position().subtract(entity.position()).normalize().reverse());
                            entity.hurtMarked = true;
                        }
                    }
                });

                if (!this.level.isClientSide) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                        double radius = SCALE * 2.0F;

                        AABB bounds = new AABB(this.collidePosX - radius, this.collidePosY - radius, this.collidePosZ - radius,
                                this.collidePosX + radius, this.collidePosY + radius, this.collidePosZ + radius);
                        double centerX = bounds.getCenter().x();
                        double centerY = bounds.getCenter().y();
                        double centerZ = bounds.getCenter().z();

                        for (int x = (int) bounds.minX; x <= bounds.maxX; x++) {
                            for (int y = (int) bounds.minY; y <= bounds.maxY; y++) {
                                for (int z = (int) bounds.minZ; z <= bounds.maxZ; z++) {
                                    BlockPos pos = new BlockPos(x, y, z);
                                    BlockState state = this.level.getBlockState(pos);

                                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                                    if (distance <= radius) {
                                        if (state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                                            this.level.destroyBlock(pos, false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (this.getTime() >= DURATION) {
                this.on = false;
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
        if (this.level.isClientSide) {
            this.endPosX = this.getX() + RADIUS * Math.cos(this.renderYaw) * Math.cos(this.renderPitch);
            this.endPosZ = this.getZ() + RADIUS * Math.sin(this.renderYaw) * Math.cos(this.renderPitch);
            this.endPosY = this.getY() + RADIUS * Math.sin(this.renderPitch);
        } else {
            this.endPosX = this.getX() + RADIUS * Math.cos(this.getYaw()) * Math.cos(this.getPitch());
            this.endPosZ = this.getZ() + RADIUS * Math.sin(this.getYaw()) * Math.cos(this.getPitch());
            this.endPosY = this.getY() + RADIUS * Math.sin(this.getPitch());
        }
    }

    public List<Entity> checkCollisions(Vec3 from, Vec3 to) {
        BlockHitResult result = this.level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

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
                .inflate(SCALE);

        for (Entity entity : HelperMethods.getEntityCollisions(this.level, bounds)) {
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