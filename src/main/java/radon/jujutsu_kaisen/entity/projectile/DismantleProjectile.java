package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DismantleProjectile extends JujutsuProjectile {
    private static final float DAMAGE = 15.0F;
    private static final int DURATION = 5;
    private static final int LINE_LENGTH = 3;
    private static final float SPEED = 5.0F;

    public DismantleProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DismantleProjectile(LivingEntity pShooter) {
        super(JJKEntities.DISMANTLE.get(), pShooter.level(), pShooter);

        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ())
                .add(HelperMethods.getLookAngle(pShooter));
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), pShooter.getYRot(), pShooter.getXRot());

        this.setDeltaMovement(this.getLookAngle().scale(SPEED));
    }

    @Override
    protected void onInsideBlock(@NotNull BlockState pState) {
        if (pState.getBlock().defaultDestroyTime() <= -1.0F) {
            this.discard();
        }
    }

    public int getSize() {
        AtomicInteger result = new AtomicInteger();

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(Mth.floor(LINE_LENGTH * cap.getAbilityPower(owner))));
        }
        return result.get();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);

        if (this.level().isClientSide || !this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return;

        BlockPos center = pResult.getBlockPos();
        Direction direction = pResult.getDirection();

        Direction perpendicular;

        if (direction.getAxis() == Direction.Axis.Y) {
            perpendicular = Direction.fromYRot(this.getYRot()).getCounterClockWise();
        } else {
            perpendicular = direction.getCounterClockWise();
        }

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                int size = Mth.floor(LINE_LENGTH * cap.getAbilityPower(owner));
                BlockPos start = center.relative(perpendicular.getOpposite(), size / 2);
                BlockPos end = center.relative(perpendicular, size / 2);

                for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
                    BlockState state = this.level().getBlockState(pos);

                    if (state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                        this.level().destroyBlock(pos, false);
                    }
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.EXPLOSION, pos.getCenter().x(), pos.getCenter().y(), pos.getCenter().z(),
                            0, 1.0D, 0.0D, 0.0D, 1.0D);
                }
            });
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        Entity entity = pResult.getEntity();

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if ((entity instanceof LivingEntity living && owner.canAttack(living)) && entity != owner) {
                    entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.DISMANTLE.get()), DAMAGE * cap.getAbilityPower(owner));
                }
            });
        }
    }

    public List<EntityHitResult> getHitResults() {
        Vec3 movement = this.getDeltaMovement();

        BlockPos center = this.blockPosition();
        Direction direction = this.getDirection();

        Direction perpendicular;

        if (direction.getAxis() == Direction.Axis.Y) {
            perpendicular = Direction.fromYRot(this.getYRot()).getCounterClockWise();
        } else {
            perpendicular = direction.getCounterClockWise();
        }

        List<EntityHitResult> entities = new ArrayList<>();

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                int size = Mth.floor(LINE_LENGTH * cap.getAbilityPower(owner));
                BlockPos start = center.relative(perpendicular.getOpposite(), size / 2);
                BlockPos end = center.relative(perpendicular, size / 2);

                BlockPos.betweenClosed(start, end).forEach(pos -> {
                    Vec3 vec31 = pos.getCenter();
                    Vec3 vec32 = vec31.add(movement);
                    entities.add(ProjectileUtil.getEntityHitResult(this.level(), this, vec31, vec32,
                            this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity));
                });
            });
        }
        return entities;
    }

    @Override
    public void tick() {
        super.tick();

        for (EntityHitResult hit : this.getHitResults()) {
            if (hit != null && !ForgeEventFactory.onProjectileImpact(this, hit)) {
                this.onHitEntity(hit);
            }
        }

        if (this.getTime() >= DURATION) {
            this.discard();
        } else if (this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            this.discard();
        }
    }
}
