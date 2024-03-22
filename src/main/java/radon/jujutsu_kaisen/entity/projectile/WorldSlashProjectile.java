package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorldSlashProjectile extends JujutsuProjectile {
    private static final EntityDataAccessor<Float> DATE_ROLL = SynchedEntityData.defineId(WorldSlashProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_LENGTH = SynchedEntityData.defineId(WorldSlashProjectile.class, EntityDataSerializers.INT);

    public static final int MIN_LENGTH = 6;
    public static final int MAX_LENGTH = 24;
    private static final int SCALAR = 6;

    public WorldSlashProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public WorldSlashProjectile(LivingEntity owner, float power, float roll) {
        super(JJKEntities.WORLD_SLASH.get(), owner.level(), owner, power);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look));

        this.setRoll(roll);
    }

    public WorldSlashProjectile(LivingEntity owner, float power, float roll, Vec3 pos, int length) {
        super(JJKEntities.WORLD_SLASH.get(), owner.level(), owner, power);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, pos.subtract(0.0D, this.getBbHeight() / 2.0F, 0.0D));

        this.setRoll(roll);
        this.setLength(length);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATE_ROLL, 0.0F);
        this.entityData.define(DATA_LENGTH, 0);
    }

    public int getLength() {
        int length = this.entityData.get(DATA_LENGTH);
        return length > 0 ? length : Math.max(MIN_LENGTH, Math.min(MAX_LENGTH, Mth.floor(SCALAR * this.getPower())));
    }

    private void setLength(int length) {
        this.entityData.set(DATA_LENGTH, length);
    }

    public float getRoll() {
        return this.entityData.get(DATE_ROLL);
    }

    private void setRoll(float roll) {
        this.entityData.set(DATE_ROLL, roll);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putFloat("roll", this.getRoll());
        pCompound.putInt("length", this.getLength());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATE_ROLL, pCompound.getFloat("roll"));
        this.entityData.set(DATA_LENGTH, pCompound.getInt("length"));
    }

    @Override
    protected void onInsideBlock(@NotNull BlockState pState) {
        if (pState.getBlock().defaultDestroyTime() <= -1.0F) {
            this.discard();
        }
    }

    public Set<Entity> getHits() {
        if (!(this.getOwner() instanceof LivingEntity)) return Set.of();

        Vec3 center = this.position().add(0.0D, this.getBbHeight() / 2.0F, 0.0D);

        float yaw = this.getYRot();
        float pitch = this.getXRot();
        float roll = this.getRoll();

        Vec3 forward = this.calculateViewVector(pitch, 180.0F - yaw);
        Vec3 up = this.calculateViewVector(pitch - 90.0F, 180.0F - yaw);

        Quaternionf quaternion = new Quaternionf().rotateAxis((float) Math.toRadians(-roll), (float) forward.x, (float) forward.y, (float) forward.z);
        Vec3 side = new Vec3(quaternion.transform(forward.cross(up).toVector3f()));

        int length = this.getLength();
        Vec3 start = center.add(side.scale((double) length / 2));
        Vec3 end = center.add(forward.subtract(side.scale((double) length / 2)));

        Set<Entity> hits = new HashSet<>();

        double depth = Math.max(1, Math.round(this.getDeltaMovement().length()));

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < length; x++) {
                BlockPos current = BlockPos.containing(start.add(end.subtract(start).scale((1.0D / length) * x).add(forward.scale(z))));

                AABB bounds = AABB.ofSize(current.getCenter(), 1.0D, 1.0D, 1.0D);

                hits.addAll(this.level().getEntities(this, bounds));

                BlockState state = this.level().getBlockState(current);

                this.level().setBlockAndUpdate(current, Blocks.AIR.defaultBlockState());

                if (!state.isAir()) {
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.EXPLOSION, current.getCenter().x, current.getCenter().y, current.getCenter().z,
                            0, 1.0D, 0.0D, 0.0D, 1.0D);
                }
            }
        }
        return hits;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            for (Entity entity : this.getHits()) {
                if (!(entity instanceof LivingEntity living)) {
                    entity.discard();
                    continue;
                }

                if (!(this.getOwner() instanceof LivingEntity owner)) continue;

                if (living == owner) continue;

                float distance = (float) Math.abs((living.getY() + living.getBbHeight() / 2.0F) -
                        (this.getY() + (this.getBbHeight() / 2.0F)));

                if (distance < this.getBbHeight()) {
                    distance = 0.0F;
                }
                float strength = 1.0F - (Math.min(living.getBbHeight(), distance) / living.getBbHeight());
                living.hurt(JJKDamageSources.worldSlash(this, owner), living.getMaxHealth() * strength);
            }
        }
    }
}
