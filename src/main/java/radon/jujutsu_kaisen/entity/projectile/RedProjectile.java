package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;

import java.util.List;

public class RedProjectile extends JujutsuProjectile {
    private static final float LAUNCH_POWER = 25.0F;
    private static final float EXPLOSIVE_POWER = 5.0F;
    private static final int DELAY = 20;
    private static final float DAMAGE = 25.0F;

    public RedProjectile(EntityType<? extends RedProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public RedProjectile(LivingEntity pShooter) {
        super(JJKEntities.RED.get(), pShooter.level, pShooter);
    }

    private void explode() {
        Entity owner = this.getOwner();

        if (owner != null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                float radius = EXPLOSIVE_POWER * cap.getGrade().getPower();

                Vec3 explosionPos = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ()).add(this.getLookAngle().scale(7.5D));
                this.level.explode(owner, explosionPos.x(), explosionPos.y(), explosionPos.z(), radius, Level.ExplosionInteraction.NONE);

                float f = radius * 2.0F;
                List<Entity> entities = this.level.getEntities(owner, new AABB(Mth.floor(explosionPos.x() - (double) f - 1.0D),
                        Mth.floor(explosionPos.y() - (double) f - 1.0D),
                        Mth.floor(explosionPos.z() - (double) f - 1.0D),
                        Mth.floor(explosionPos.x() + (double) f + 1.0D),
                        Mth.floor(explosionPos.y() + (double) f + 1.0D),
                        Mth.floor(explosionPos.z() + (double) f + 1.0D)));

                Vec3 look = owner.getLookAngle();

                for (Entity entity : entities) {
                    if (this.canHitEntity(entity) && entity != this) {
                        float distance = entity.distanceTo(owner);
                        float scalar = (radius - distance) / radius;

                        entity.hurt(DamageSource.explosion(this, owner), (DAMAGE * cap.getGrade().getPower()) * scalar);
                        entity.setDeltaMovement(look.x() * LAUNCH_POWER, look.y() * LAUNCH_POWER, (look.z() * LAUNCH_POWER) * scalar);
                    }
                }
                this.discard();
            });
        }
    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = this.getOwner();

        if (owner != null) {
            double x = owner.getX();
            double y = owner.getEyeY() - (this.getBbHeight() / 2.0F);
            double z = owner.getZ();

            Vec3 look = owner.getLookAngle();
            Vec3 spawnPos = new Vec3(x, y, z).add(look);

            this.moveTo(spawnPos.x(), spawnPos.y(), spawnPos.z(), owner.getYRot(), owner.getXRot());
        }

        if (!this.level.isClientSide) {
            if (this.getTime() >= DELAY) {
                this.explode();
            }
        }
    }
}
