package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;

public class ToadTongueProjectile extends JujutsuProjectile {
    public static final float SPEED = 1.0F;

    private int range;

    public ToadTongueProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ToadTongueProjectile(LivingEntity pShooter, int range) {
        super(JJKEntities.TOAD_TONGUE.get(), pShooter.level, pShooter);

        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F) - 0.1D, pShooter.getZ());
        this.setPos(spawn);

        this.range = range;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("range", this.range);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        pCompound.putInt("range", this.range);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        Entity target = pResult.getEntity();

        Entity owner = this.getOwner();

        if (owner != null) {
            target.setDeltaMovement(owner.position().subtract(target.position()));
            target.hurtMarked = true;
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide) {
            Entity owner = this.getOwner();

            if (owner != null) {
                if (this.distanceTo(owner) >= this.range) {
                    this.discard();
                }
            }
        }
    }
}
