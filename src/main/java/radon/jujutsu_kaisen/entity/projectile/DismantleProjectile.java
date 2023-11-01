package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;

public class DismantleProjectile extends JujutsuProjectile {
    public static final float DAMAGE = 15.0F;
    private static final int DURATION = 5;
    private static final int LINE_LENGTH = 3;
    private static final float SPEED = 5.0F;

    private boolean vertical;
    private int length;

    public DismantleProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DismantleProjectile(LivingEntity owner, float power, boolean vertical) {
        super(JJKEntities.DISMANTLE.get(), owner.level(), owner, power);

        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                .add(owner.getLookAngle());
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());

        this.setDeltaMovement(this.getLookAngle().scale(SPEED));

        this.vertical = vertical;
    }

    public DismantleProjectile(LivingEntity owner, float power, boolean vertical, Vec3 pos, int length) {
        super(JJKEntities.DISMANTLE.get(), owner.level(), owner, power);

        this.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());

        this.setDeltaMovement(this.getLookAngle().scale(SPEED));

        this.vertical = vertical;
        this.length = length;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putBoolean("vertical", this.vertical);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.vertical = pCompound.getBoolean("vertical");
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
            if ((entity instanceof LivingEntity living && owner.canAttack(living)) && entity != owner) {
                entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.DISMANTLE.get()), DAMAGE * this.getPower());
            }
        }
    }

    public List<HitResult> getHitResults() {
        BlockPos center = this.blockPosition();
        Vec3 movement = this.getDeltaMovement();
        Direction direction = Direction.getNearest(movement.x(), movement.y(), movement.z()).getOpposite();

        Direction perpendicular;

        if (this.vertical) {
            perpendicular = direction.getAxis() == Direction.Axis.Y ? Direction.fromYRot(this.getYRot()).getOpposite() : Direction.UP;
        } else {
            perpendicular = direction.getAxis() == Direction.Axis.Y ? Direction.fromYRot(this.getYRot()).getCounterClockWise() : direction.getCounterClockWise();
        }

        List<HitResult> hits = new ArrayList<>();

        int size = this.length > 0.0D ? this.length : Mth.floor(LINE_LENGTH * this.getPower());
        BlockPos start = center.relative(perpendicular.getOpposite(), size / 2);
        BlockPos end = center.relative(direction, Math.round(SPEED)).relative(perpendicular, size / 2);

        BlockPos.betweenClosed(start, end).forEach(pos -> {
            Vec3 current = pos.getCenter();

            AABB bounds = AABB.ofSize(current, 1.0D, 1.0D, 1.0D);

            for (Entity entity : HelperMethods.getEntityCollisions(this.level(), bounds)) {
                hits.add(new EntityHitResult(entity));
            }

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

        if (this.getTime() >= DURATION) {
            this.discard();
        }
    }
}
