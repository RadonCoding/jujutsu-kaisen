package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;

import java.util.List;

public class RedProjectile extends JujutsuProjectile {
    private static final float LAUNCH_POWER = 10.0F;
    private static final float EXPLOSIVE_POWER = 5.0F;
    private static final int DELAY = 20;
    private static final float DAMAGE = 5.0F;

    public RedProjectile(EntityType<? extends RedProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public RedProjectile(LivingEntity pShooter) {
        super(JJKEntities.RED.get(), pShooter.level, pShooter);

        Vec3 look = pShooter.getLookAngle();
        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ()).add(look);
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), pShooter.getYRot(), pShooter.getXRot());
    }

    private void explode() {
        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                float radius = EXPLOSIVE_POWER * cap.getGrade().getPower();

                Vec3 offset = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ()).add(this.getLookAngle().scale(7.5D));
                this.level.explode(owner, offset.x(), offset.y(), offset.z(), radius, Level.ExplosionInteraction.NONE);

                float f = radius * 2.0F;
                List<Entity> entities = this.level.getEntities(this, new AABB(Mth.floor(offset.x() - (double) f - 1.0D),
                        Mth.floor(offset.y() - (double) f - 1.0D),
                        Mth.floor(offset.z() - (double) f - 1.0D),
                        Mth.floor(offset.x() + (double) f + 1.0D),
                        Mth.floor(offset.y() + (double) f + 1.0D),
                        Mth.floor(offset.z() + (double) f + 1.0D)));

                Vec3 look = owner.getLookAngle();

                for (Entity entity : entities) {
                    if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner) continue;

                    float distance = entity.distanceTo(owner);
                    float scalar = (radius - distance) / radius;

                    entity.hurt(DamageSource.explosion(this, owner), (DAMAGE * cap.getGrade().getPower()) * scalar);
                    entity.setDeltaMovement(look.x() * LAUNCH_POWER, look.y() * LAUNCH_POWER, (look.z() * LAUNCH_POWER) * scalar);
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
            if (this.getTime() < DELAY && !owner.isAlive()) {
                this.discard();
            } else {
                Vec3 look = owner.getLookAngle();
                Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
                this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
            }
        }

        if (!this.level.isClientSide) {
            if (this.getTime() >= DELAY) {
                this.explode();
            }
        }
    }
}
