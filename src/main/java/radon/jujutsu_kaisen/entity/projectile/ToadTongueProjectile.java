package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.ten_shadows.ToadEntity;

import java.util.UUID;

public class ToadTongueProjectile extends JujutsuProjectile {
    public static final float SPEED = 2.0F;
    private static final double PULL_STRENGTH = 0.5D;

    private int range;
    private UUID target;
    private boolean grabbed;
    private Vec3 pos;

    public ToadTongueProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public ToadTongueProjectile(LivingEntity owner, int range, UUID target) {
        this(JJKEntities.TOAD_TONGUE.get(), owner.level());

        this.setOwner(owner);

        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F) - 0.1D, owner.getZ());
        this.setPos(spawn);

        this.setDeltaMovement(owner.getLookAngle().scale(SPEED * (((ToadEntity) owner).hasWings() ? 5.0D : 1.0D)));

        this.target = target;
        this.range = range;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("range", this.range);
        pCompound.putUUID("target", this.target);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.range = pCompound.getInt("range");
        this.target = pCompound.getUUID("target");
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        Entity target = pResult.getEntity();

        if (!target.getUUID().equals(this.target)) return;

        this.grabbed = true;
        this.pos = target.position();
    }
    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        this.discard();
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        super.remove(pReason);

        Entity owner = this.getOwner();

        if (owner instanceof ToadEntity toad) {
            toad.setCanShoot(true);
        }
    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = this.getOwner();

        if (this.level().isClientSide || owner == null) return;

        if (this.grabbed) {
            if (((ServerLevel) this.level()).getEntity(this.target) instanceof LivingEntity living) {
                if (!(owner instanceof ToadEntity toad) || toad.getTarget() != living || living.isDeadOrDying() || living.isRemoved() || !owner.isAlive() || owner.isRemoved())
                    this.discard();

                living.setDeltaMovement(this.pos.subtract(living.position()).normalize().scale(PULL_STRENGTH));
                living.hurtMarked = true;

                this.setPos(living.getX(), living.getY() + (living.getBbHeight() / 2.0F), living.getZ());
            } else {
                this.discard();
            }
            this.setDeltaMovement(Vec3.ZERO);
        } else {
            if (this.distanceTo(owner) >= this.range) {
                this.discard();
            }
        }
    }
}
