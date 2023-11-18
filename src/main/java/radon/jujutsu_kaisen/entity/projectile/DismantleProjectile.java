package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;

public class DismantleProjectile extends JujutsuProjectile {
    private static final EntityDataAccessor<Float> DATE_ROLL = SynchedEntityData.defineId(DismantleProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_LENGTH = SynchedEntityData.defineId(DismantleProjectile.class, EntityDataSerializers.INT);

    public static final float DAMAGE = 15.0F;
    private static final int DURATION = 5;
    private static final int LINE_LENGTH = 3;

    private boolean domain;

    public DismantleProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DismantleProjectile(LivingEntity owner, float power, float roll) {
        super(JJKEntities.DISMANTLE.get(), owner.level(), owner, power);

        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                .add(owner.getLookAngle());
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());

        this.entityData.set(DATE_ROLL, roll);
    }

    public DismantleProjectile(LivingEntity owner, float power, float roll, Vec3 pos, int length) {
        super(JJKEntities.DISMANTLE.get(), owner.level(), owner, power);

        this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());

        this.entityData.set(DATE_ROLL, roll);
        this.entityData.set(DATA_LENGTH, length);
    }

    public DismantleProjectile(LivingEntity owner, float power, float roll, Vec3 pos, int length, boolean domain) {
        this(owner, power, roll, pos, length);

        this.moveTo(pos.x(), pos.y(), pos.z(), (this.random.nextFloat() - 0.5F) * 360.0F, 0.0F);

        this.domain = domain;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATE_ROLL, 0.0F);
        this.entityData.define(DATA_LENGTH, 0);
    }

    public float getRoll() {
        return this.entityData.get(DATE_ROLL);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putFloat("roll", this.getRoll());
        pCompound.putInt("length", this.getLength());
        pCompound.putBoolean("domain", this.domain);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.entityData.set(DATE_ROLL, pCompound.getFloat("roll"));
        this.entityData.set(DATA_LENGTH, pCompound.getInt("length"));
        this.domain = pCompound.getBoolean("domain");
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

        if (this.getOwner() instanceof LivingEntity owner) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if ((entity instanceof LivingEntity living && owner.canAttack(living)) && entity != owner) {
                DomainExpansionEntity domain = cap.getDomain((ServerLevel) this.level());
                entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain == null ? this : domain, owner, JJKAbilities.DISMANTLE.get()), DAMAGE * this.getPower());
            }
        }
    }

    public int getLength() {
        int length = this.entityData.get(DATA_LENGTH);
        return length > 0.0D ? length : Mth.floor(LINE_LENGTH * this.getPower());
    }

    public List<HitResult> getHitResults() {
        Vec3 center = this.position().add(0.0D, this.getBbHeight() / 2.0F, 0.0D);
        Vec3 movement = this.getDeltaMovement();
        Direction direction = Direction.getNearest(movement.x(), movement.y(), movement.z()).getOpposite();

        Direction perpendicular;

        if (Math.abs(this.getRoll() - 90.0F) < 30.0F) {
            perpendicular = direction.getAxis() == Direction.Axis.Y ? Direction.fromYRot(this.getYRot()).getOpposite() : Direction.UP;
        } else {
            perpendicular = direction.getAxis() == Direction.Axis.Y ? Direction.fromYRot(this.getYRot()).getCounterClockWise() : direction.getCounterClockWise();
        }

        List<HitResult> hits = new ArrayList<>();

        float speed = (float) this.getDeltaMovement().length();

        int length = this.getLength();
        BlockPos start = BlockPos.containing(center.relative(perpendicular.getOpposite(), (double) length / 2));
        BlockPos end = BlockPos.containing(center.relative(direction, Math.round(speed)).relative(perpendicular, (double) length / 2));

        BlockPos.betweenClosed(start, end).forEach(pos -> {
            Vec3 current = pos.getCenter();

            AABB bounds = AABB.ofSize(current, 1.0D, 1.0D, 1.0D);

            for (Entity entity : HelperMethods.getEntityCollisions(this.level(), bounds)) {
                hits.add(new EntityHitResult(entity));
            }

            if (this.domain) return;

            BlockState state = this.level().getBlockState(pos);

            if (!state.getFluidState().isEmpty() || state.isAir()) return;

            if (state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                this.level().destroyBlock(pos, false);
            }
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.EXPLOSION, pos.getCenter().x(), pos.getCenter().y(), pos.getCenter().z(),
                    0, 1.0D, 0.0D, 0.0D, 1.0D);
        });
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

        if (this.domain || this.getTime() >= DURATION) {
            this.discard();
        }
    }
}
