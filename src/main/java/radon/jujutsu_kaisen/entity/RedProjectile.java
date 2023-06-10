package radon.jujutsu_kaisen.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.capability.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.SpinningParticle;

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
        super(JujutsuEntities.RED.get(), pShooter.level, pShooter);
    }

    private void explode() {
        Entity owner = this.getOwner();

        if (owner != null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                float radius = EXPLOSIVE_POWER * (cap.getGrade().ordinal() + 1);

                Vec3 explosionPos = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ()).add(this.getLookAngle().scale(7.5D));
                this.level.explode(owner, explosionPos.x(), explosionPos.y(), explosionPos.z(), radius, Level.ExplosionInteraction.NONE);

                float f2 = radius * 2.0F;
                int k1 = Mth.floor(explosionPos.x() - (double) f2 - 1.0D);
                int l1 = Mth.floor(explosionPos.x() + (double) f2 + 1.0D);
                int i2 = Mth.floor(explosionPos.y() - (double) f2 - 1.0D);
                int i1 = Mth.floor(explosionPos.y() + (double) f2 + 1.0D);
                int j2 = Mth.floor(explosionPos.z() - (double) f2 - 1.0D);
                int j1 = Mth.floor(explosionPos.z() + (double) f2 + 1.0D);

                List<Entity> entities = this.level.getEntities(owner, new AABB(k1, i2, j2, l1, i1, j1));

                Vec3 look = owner.getLookAngle();

                for (Entity entity : entities) {
                    if (entity != this) {
                        entity.hurt(DamageSource.indirectMagic(this, owner), DAMAGE * (cap.getGrade().ordinal() + 1));
                        entity.setDeltaMovement(look.x() * LAUNCH_POWER, look.y() * LAUNCH_POWER, look.z() * LAUNCH_POWER);
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
            this.reapplyPosition();
        }

        if (!this.level.isClientSide) {
            if (this.getTime() >= DELAY) {
                this.explode();
            }
        }
    }
}
