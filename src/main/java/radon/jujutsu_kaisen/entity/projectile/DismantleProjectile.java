package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;

public class DismantleProjectile extends JujutsuProjectile {
    public static final int FRAMES = 2;
    private static final float DAMAGE = 25.0F;
    private static final int DURATION = 5;
    private static final int LINE_LENGTH = 5;
    private static final float SPEED = 5.0F;

    public int animation;
    public boolean on = true;

    public DismantleProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DismantleProjectile(LivingEntity pShooter) {
        super(JJKEntities.DISMANTLE.get(), pShooter.level, pShooter);

        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ())
                .add(pShooter.getLookAngle());
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), pShooter.getYRot(), pShooter.getXRot());

        this.setDeltaMovement(this.getLookAngle().scale(SPEED));
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

        Entity entity = pResult.getEntity();

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if ((entity instanceof LivingEntity living && owner.canAttack(living)) && entity != owner) {
                    entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.DISMANTLE.get()), DAMAGE * cap.getGrade().getPower());
                }
            });
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);

        if (this.level.isClientSide || !this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return;

        BlockPos center = pResult.getBlockPos();
        Direction direction = pResult.getDirection();

        Direction perpendicular;

        if (direction.getAxis() == Direction.Axis.Y) {
            perpendicular = Direction.fromYRot(this.getYRot()).getCounterClockWise();
        } else {
            perpendicular = direction.getCounterClockWise();
        }

        BlockPos start = center.relative(perpendicular.getOpposite(), LINE_LENGTH / 2);
        BlockPos end = center.relative(perpendicular, LINE_LENGTH / 2);

        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
            BlockState state = this.level.getBlockState(pos);

            if (state.getFluidState().isEmpty() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                this.level.destroyBlock(pos, false);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.on && this.animation == 0) {
            this.discard();
        } else if (this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
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

        if (this.getTime() - DURATION / 2 > DURATION) {
            this.on = false;
        }
    }
}
