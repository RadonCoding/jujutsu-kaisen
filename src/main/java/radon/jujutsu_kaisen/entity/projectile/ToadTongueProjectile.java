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
import radon.jujutsu_kaisen.entity.ten_shadows.ToadEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.UUID;

public class ToadTongueProjectile extends JujutsuProjectile {
    public static final float SPEED = 1.0F;

    private int range;
    private UUID target;

    public ToadTongueProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ToadTongueProjectile(LivingEntity pShooter, int range, UUID target) {
        super(JJKEntities.TOAD_TONGUE.get(), pShooter.level, pShooter);

        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F) - 0.1D, pShooter.getZ());
        this.setPos(spawn);

        this.setDeltaMovement(HelperMethods.getLookAngle(pShooter).scale(SPEED * (((ToadEntity) pShooter).hasWings() ? 5.0D : 1.0D)));

        this.target = target;
        this.range = range;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("range", this.range);
        pCompound.putUUID("target", this.target);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.range = pCompound.getInt("range");
        this.target = pCompound.getUUID("target");
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        Entity target = pResult.getEntity();

        if (!target.getUUID().equals(this.target)) return;

        Entity owner = this.getOwner();

        if (owner != null) {
            if (target.isPushable()) {
                target.moveTo(owner.position());
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        this.discard();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        Entity owner = this.getOwner();

        if (owner instanceof ToadEntity toad) {
            toad.canShoot = true;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide) {
            Entity owner = this.getOwner();

            if (owner != null) {
                if (this.distanceTo(owner) >= this.range) {
                    this.discard();
                } else if (this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                    this.discard();
                }
            }
        }
    }
}
