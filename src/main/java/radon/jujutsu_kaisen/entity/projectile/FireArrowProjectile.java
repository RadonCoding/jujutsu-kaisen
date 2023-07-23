package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;

public class FireArrowProjectile extends JujutsuProjectile {
    private static final float DAMAGE = 25.0F;
    private static final float SPEED = 5.0F;
    private static final float EXPLOSIVE_POWER = 2.5F;
    private static final int DELAY = 20;

    public FireArrowProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FireArrowProjectile(LivingEntity pShooter) {
        super(JJKEntities.FIRE_ARROW.get(), pShooter.level, pShooter);

        Vec3 look = pShooter.getLookAngle();
        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ()).add(look);
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), pShooter.getYRot(), pShooter.getXRot());
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);

        Entity entity = pResult.getEntity();

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if ((entity instanceof LivingEntity living && owner.canAttack(living)) && entity != owner) {
                    entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner), DAMAGE * cap.getGrade().getPower());
                }
            });
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        Vec3 dir = this.getDeltaMovement();

        for (int i = 0; i < 50; i++) {
            Vec3 yaw = dir.yRot(this.random.nextFloat() * 360.0F);
            Vec3 pitch = yaw.xRot(this.random.nextFloat() * 180.0F - 90.0F);

            double dx = pitch.x() + (this.random.nextDouble() - 0.5D) * 0.2D;
            double dy = pitch.y() + (this.random.nextDouble() - 0.5D) * 0.2D;
            double dz = pitch.z() + (this.random.nextDouble() - 0.5D) * 0.2D;

            if (!this.level.isClientSide) {
                ((ServerLevel) this.level).sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 0, dx, dy, dz, 1.0D);
            }
        }

        Entity owner = this.getOwner();

        if (owner != null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                Vec3 location = result.getLocation();
                this.level.explode(owner, JJKDamageSources.indirectJujutsuAttack(owner, null), null,
                        location.x(), location.y(), location.z(), EXPLOSIVE_POWER * cap.getGrade().getPower(), false, Level.ExplosionInteraction.NONE);
            });
        }
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();

        this.level.addParticle(ParticleTypes.FLAME, this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ(), 0.0D, 0.0D, 0.0D);

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() < DELAY) {
                if (!owner.isAlive()) {
                    this.discard();
                } else {
                    owner.swing(InteractionHand.MAIN_HAND);

                    Vec3 look = owner.getLookAngle();
                    Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
                    this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
                }
            } else if (this.getTime() >= DELAY) {
                if (this.getTime() == DELAY) {
                    this.setDeltaMovement(this.getLookAngle().scale(SPEED));
                    this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 1.0F, 1.0F);
                } else if (this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                    this.discard();
                }
            }
        }
    }
}
