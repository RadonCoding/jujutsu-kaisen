package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;

import java.util.List;

public class RedProjectile extends JujutsuProjectile {
    private static final double LAUNCH_POWER = 3.0D;
    private static final float EXPLOSIVE_POWER = 5.0F;
    private static final int DELAY = 20;

    public RedProjectile(EntityType<? extends Projectile> pEntityType, Level level) {
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

                Vec3 offset = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());
                this.level.explode(owner, JJKDamageSources.indirectJujutsuAttack(this, owner), null, offset, radius, false, Level.ExplosionInteraction.NONE);

                float f = radius * 2.0F;
                int k1 = Mth.floor(offset.x() - (double) f - 1.0D);
                int l1 = Mth.floor(offset.x() + (double) f + 1.0D);
                int i2 = Mth.floor(offset.y() - (double) f - 1.0D);
                int i1 = Mth.floor(offset.y() + (double) f + 1.0D);
                int j2 = Mth.floor(offset.z() - (double) f - 1.0D);
                int j1 = Mth.floor(offset.z() + (double) f + 1.0D);
                List<Entity> entities = this.level.getEntities(owner, new AABB(k1, i2, j2, l1, i1, j1));

                for (Entity entity : entities) {
                    if (!entity.ignoreExplosion()) {
                        double d = Math.sqrt(entity.distanceToSqr(offset)) / (double) f;

                        if (d <= 1.0D) {
                            double d0 = entity.getX() - offset.x();
                            double d1 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - offset.y();
                            double d2 = entity.getZ() - offset.z();
                            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                            if (d3 != 0.0D) {
                                d0 /= d3;
                                d1 /= d3;
                                d2 /= d3;
                                double d4 = Explosion.getSeenPercent(offset, entity);
                                double d5 = (1.0D - d) * d4;

                                double d6;

                                if (entity instanceof LivingEntity living) {
                                    d6 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(living, d5);
                                } else {
                                    d6 = d5;
                                }
                                Vec3 movement = new Vec3(d0 * d6, d1 * d6, d2 * d6).scale(LAUNCH_POWER);
                                entity.setDeltaMovement(entity.getDeltaMovement().add(movement));
                            }
                        }
                    }
                }
                this.discard();
            });
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() < DELAY && !owner.isAlive()) {
                this.discard();
            } else {
                owner.swing(InteractionHand.MAIN_HAND);

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
