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
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DismantleProjectile extends JujutsuProjectile {
    private static final EntityDataAccessor<Float> DATE_ROLL = SynchedEntityData.defineId(DismantleProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_LENGTH = SynchedEntityData.defineId(DismantleProjectile.class, EntityDataSerializers.INT);

    public static final int MIN_LENGTH = 4;
    public static final int MAX_LENGTH = 12;

    private boolean instant;
    private boolean destroy = true;
    private int destroyed;

    public DismantleProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public DismantleProjectile(LivingEntity owner, float power, float roll) {
        this(JJKEntities.DISMANTLE.get(), owner, power, roll);
    }

    public DismantleProjectile(EntityType<? extends Projectile> pType, LivingEntity owner, float power, float roll) {
        super(pType, owner.level(), owner, power);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look));

        this.setRoll(roll);
    }

    public DismantleProjectile(LivingEntity owner, float power, float roll, Vec3 pos, int length) {
        this(owner, power, roll, pos, length, true);
    }

    public DismantleProjectile(LivingEntity owner, float power, float roll, Vec3 pos, int length, boolean destroy) {
        super(JJKEntities.DISMANTLE.get(), owner.level(), owner, power);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, pos.subtract(0.0D, this.getBbHeight() / 2.0F, 0.0D));

        this.setRoll(roll);
        this.setLength(length);

        this.destroy = destroy;
    }

    public DismantleProjectile(LivingEntity owner, float power, float roll, Vec3 pos, int length, boolean destroy, boolean instant) {
        this(owner, power, roll, pos, length, destroy);

        this.moveTo(pos.x, pos.y, pos.z, (this.random.nextFloat() - 0.5F) * 360.0F, 0.0F);

        this.instant = instant;
    }

    protected boolean isInfinite() {
        return false;
    }

    protected float getDamage() {
        return 10.0F;
    }

    public int getMinLength() {
        return MIN_LENGTH;
    }

    public int getMaxLength() {
        return MAX_LENGTH;
    }

    public int getScalar() {
        return 4;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATE_ROLL, 0.0F);
        this.entityData.define(DATA_LENGTH, 0);
    }

    public int getLength() {
        int length = this.entityData.get(DATA_LENGTH);
        return length > 0 ? length : Math.max(this.getMinLength(), Math.min(this.getMaxLength(), Mth.floor(this.getScalar() * this.getPower())));
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
        pCompound.putBoolean("instant", this.instant);
        pCompound.putBoolean("destroy", this.destroy);
        pCompound.putInt("destroyed", this.destroyed);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATE_ROLL, pCompound.getFloat("roll"));
        this.entityData.set(DATA_LENGTH, pCompound.getInt("length"));
        this.instant = pCompound.getBoolean("instant");
        this.destroy = pCompound.getBoolean("destroy");
        this.destroyed = pCompound.getInt("destroyed");
    }

    @Override
    protected void onInsideBlock(@NotNull BlockState pState) {
        if (pState.getBlock().defaultDestroyTime() <= -1.0F) {
            this.discard();
        }
    }

    public Set<Entity> getHits() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return Set.of();

        Vec3 center = this.position().add(0.0D, this.getBbHeight() / 2.0F, 0.0D);

        float yaw = this.getYRot();
        float pitch = this.getXRot();
        float roll = this.getRoll();

        Vec3 forward = this.calculateViewVector(pitch, yaw);
        Vec3 up = this.calculateViewVector(pitch - 90.0F, yaw);

        Quaternionf quaternion = new Quaternionf().rotateAxis((float) Math.toRadians(-roll), (float) forward.x, (float) forward.y, (float) forward.z);
        Vec3 side = new Vec3(quaternion.transform(forward.cross(up).toVector3f()));

        int length = this.getLength();
        Vec3 start = center.add(side.scale((double) length / 2));
        Vec3 end = center.add(forward.subtract(side.scale((double) length / 2)));

        Set<Entity> hits = new HashSet<>();

        double depth = Math.max(1, Math.round(this.getDeltaMovement().length()));

        List<Entity> entities = EntityUtil.getTouchableEntities(Entity.class, this.level(), owner, AABB.ofSize(center, 16.0D, 16.0D, 16.0D));

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < length; x++) {
                BlockPos current = BlockPos.containing(start.add(end.subtract(start).scale((1.0D / length) * x).add(forward.scale(z))));

                AABB bounds = AABB.ofSize(current.getCenter(), 1.0D, 1.0D, 1.0D);

                for (Entity entity : entities) {
                    if (entity.getBoundingBox().intersects(bounds)) {
                        hits.add(entity);
                    }
                }

                if (!this.destroy) continue;

                if (!HelperMethods.isDestroyable((ServerLevel) this.level(), this, owner, current)) continue;

                boolean destroyed = this.level().setBlockAndUpdate(current, Blocks.AIR.defaultBlockState());

                if (destroyed) {
                    this.destroyed++;
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
                if (!(this.getOwner() instanceof LivingEntity owner)) continue;

                if (entity == owner) continue;

                IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) continue;

                ISorcererData data = cap.getSorcererData();

                DomainExpansionEntity domain = data.getSummonByClass(DomainExpansionEntity.class);
                entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain == null ? this : domain, owner, JJKAbilities.DISMANTLE.get()),
                        this.getDamage() * this.getPower());
            }
        }

        if (this.instant || (!this.isInfinite() && this.destroyed >= this.getLength() * 2)) {
            this.discard();
        }
    }
}
