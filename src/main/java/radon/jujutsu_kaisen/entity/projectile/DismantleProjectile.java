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
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.ArrayList;
import java.util.List;

public class DismantleProjectile extends JujutsuProjectile {
    private static final EntityDataAccessor<Float> DATE_ROLL = SynchedEntityData.defineId(DismantleProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_LENGTH = SynchedEntityData.defineId(DismantleProjectile.class, EntityDataSerializers.INT);

    public static final float DAMAGE = 10.0F;
    private static final int DURATION = 10;
    private static final int LINE_LENGTH = 2;
    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 12;

    private boolean instant;
    private boolean destroy = true;
    private int destroyed;

    public DismantleProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public DismantleProjectile(LivingEntity owner, float power, float roll) {
        super(JJKEntities.DISMANTLE.get(), owner.level(), owner, power);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look));

        this.setRoll(roll);
    }

    public DismantleProjectile(LivingEntity owner, float power, float roll, Vec3 pos, int length) {
        super(JJKEntities.DISMANTLE.get(), owner.level(), owner, power);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, pos);

        this.setRoll(roll);
        this.setLength(length);
    }

    public DismantleProjectile(LivingEntity owner, float power, float roll, Vec3 pos, int length, boolean instant, boolean destroy) {
        this(owner, power, roll, pos, length);

        this.moveTo(pos.x, pos.y, pos.z, (this.random.nextFloat() - 0.5F) * 360.0F, 0.0F);

        this.instant = instant;
        this.destroy = destroy;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATE_ROLL, 0.0F);
        this.entityData.define(DATA_LENGTH, 0);
    }

    public int getLength() {
        int length = this.entityData.get(DATA_LENGTH);
        return length > 0 ? length : Math.max(MIN_LENGTH, Math.min(MAX_LENGTH, Mth.floor(LINE_LENGTH * this.getPower())));
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

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (this.level().isClientSide) return;

        Entity entity = pResult.getEntity();

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        DomainExpansionEntity domain = cap.getSummonByClass(DomainExpansionEntity.class);
        entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain == null ? this : domain, owner, JJKAbilities.DISMANTLE.get()), DAMAGE * this.getPower());
    }

    private Vec3 rotate(Vec3 vector, Vec3 axis, double degrees) {
        double radians = degrees * Math.PI / 180.0D;

        double cosine = Math.cos(radians);
        double sine = Math.sin(radians);

        double xx = (1.0D - cosine) * axis.x * axis.x;
        double xy = (1.0D - cosine) * axis.x * axis.y;
        double xz = (1.0D - cosine) * axis.x * axis.z;
        double yy = (1.0D - cosine) * axis.y * axis.y;
        double yz = (1.0D - cosine) * axis.y * axis.z;
        double zz = (1.0D - cosine) * axis.z * axis.z;

        return new Vec3(
                (cosine + xx) * vector.x + (xy - axis.z * sine) * vector.y + (xz + axis.y * sine) * vector.z,
                (xy + axis.z * sine) * vector.x + (cosine + yy) * vector.y + (yz - axis.x * sine) * vector.z,
                (xz - axis.y * sine) * vector.x + (yz + axis.x * sine) * vector.y + (cosine + zz) * vector.z
        );
    }

    public List<HitResult> getHitResults() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return List.of();

        Vec3 center = this.position().add(0.0D, this.getBbHeight() / 2.0F, 0.0D);

        float yaw = this.getYRot();
        float pitch = this.getXRot();
        float roll = this.getRoll();

        Vec3 forward = this.calculateViewVector(pitch, 180.0F - yaw);
        Vec3 up = this.calculateViewVector(pitch - 90.0F, 180.0f - yaw);

        Vec3 side = this.rotate(forward.cross(up), forward, -roll);

        int length = this.getLength();
        Vec3 start = center.add(side.scale((double) length / 2));
        Vec3 end = center.add(forward.subtract(side.scale((double) length / 2)));

        List<HitResult> hits = new ArrayList<>();

        double depth = Math.max(1, Math.round(this.getDeltaMovement().length()));

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < length; x++) {
                BlockPos current = BlockPos.containing(start.add(end.subtract(start).scale((1.0D / length) * x).add(forward.scale(z))));

                AABB bounds = AABB.ofSize(current.getCenter(), 1.0D, 1.0D, 1.0D);

                for (Entity entity : this.level().getEntities(this, bounds)) {
                    hits.add(new EntityHitResult(entity));
                }

                if (!this.destroy) continue;

                BlockState state = this.level().getBlockState(current);

                if (HelperMethods.isDestroyable(this.level(), owner, current)) {
                    boolean destroyed;

                    if (state.getFluidState().isEmpty()) {
                        destroyed = this.level().destroyBlock(current, false);
                    } else {
                        destroyed = this.level().setBlockAndUpdate(current, Blocks.AIR.defaultBlockState());
                    }

                    if (destroyed) {
                        ((ServerLevel) this.level()).sendParticles(ParticleTypes.EXPLOSION, current.getCenter().x, current.getCenter().y, current.getCenter().z,
                                0, 1.0D, 0.0D, 0.0D, 1.0D);
                        this.destroyed++;
                    }
                }
            }
        }
        return hits;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            for (HitResult result : this.getHitResults()) {
                if (result.getType() != HitResult.Type.MISS) {
                    this.onHit(result);
                }
            }
        }

        if (this.instant || this.destroyed >= this.getLength() * 2 || this.getTime() >= DURATION) {
            this.discard();
        }
    }
}
