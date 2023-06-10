package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;

public class HollowPurpleProjectile extends JujutsuProjectile {
    private static final int DELAY = 2 * 20;
    private static final double SPEED = 1.0D;
    private static final float DAMAGE = 100.0F;
    private static final int DURATION = 10 * 20;

    public HollowPurpleProjectile(EntityType<? extends HollowPurpleProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HollowPurpleProjectile(LivingEntity pShooter) {
        super(JujutsuEntities.HOLLOW_PURPLE.get(), pShooter.level, pShooter);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    private void hurtEntities() {
        AABB bounds = this.getBoundingBox().inflate(1.5D);

        Entity owner = this.getOwner();

        if (owner != null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (Entity entity : this.level.getEntities(null, bounds)) {
                    if (entity != owner) {
                        entity.hurt(DamageSource.indirectMagic(this, owner), DAMAGE * (cap.getGrade().ordinal() + 1));
                    }
                }
            });
        }
    }

    private void breakBlocks() {
        AABB bounds = this.getBoundingBox().inflate(1.5D);

        for (double x = bounds.minX; x <= bounds.maxX; x++) {
            for (double y = bounds.minY; y <= bounds.maxY; y++) {
                for (double z = bounds.minZ; z <= bounds.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.getBlock().defaultDestroyTime() > -1.0F) {
                        this.level.destroyBlock(pos, true);
                    }
                }
            }
        }
    }

    @Override
    protected void onInsideBlock(@NotNull BlockState pState) {
        if (this.getTime() >= DELAY && pState.getBlock().defaultDestroyTime() <= -1.0F) {
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getTime() >= DURATION) {
            this.discard();
        } else {
            Entity owner = this.getOwner();

            if (owner != null) {
                if (this.getTime() >= DELAY) {
                    this.setDeltaMovement(this.getLookAngle().scale(SPEED));

                    this.hurtEntities();
                    this.breakBlocks();
                } else {
                    double x = owner.getX();
                    double y = owner.getEyeY() - (this.getBbHeight() / 2.0F);
                    double z = owner.getZ();

                    Vec3 look = owner.getLookAngle();
                    Vec3 spawn = new Vec3(x, y, z).add(look);
                    this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
                    this.reapplyPosition();
                }
            }
        }
    }
}