package radon.jujutsu_kaisen.entity.projectile;

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

public class BulletProjectile extends JujutsuProjectile {
    private static final float DAMAGE = 10.0F;
    public static final float SPEED = 10.0F;

    public BulletProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BulletProjectile(LivingEntity pShooter) {
        super(JJKEntities.BULLET.get(), pShooter.level, pShooter);

        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ());
        this.setPos(spawn.x(), spawn.y(), spawn.z());
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        Entity entity = pResult.getEntity();

        if (this.getOwner() instanceof LivingEntity owner) {
            if ((entity instanceof LivingEntity living && owner.canAttack(living)) && entity != owner) {
                entity.hurt(this.damageSources().mobProjectile(this, owner), DAMAGE);
            }
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);

        this.discard();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            this.discard();
        }
    }
}
