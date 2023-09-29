package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.HollowPurpleExplosion;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class RedProjectile extends JujutsuProjectile {
    private static final double LAUNCH_POWER = 10.0D;
    private static final float EXPLOSIVE_POWER = 1.5F;
    public static final int DELAY = 20;
    private static final int DURATION = 3 * 20;
    private static final float SPEED = 5.0F;
    private static final float DAMAGE = 25.0F;

    public RedProjectile(EntityType<? extends Projectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public RedProjectile(LivingEntity pShooter) {
        super(JJKEntities.RED.get(), pShooter.level(), pShooter);

        Vec3 look = HelperMethods.getLookAngle(pShooter);
        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ()).add(look);
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), pShooter.getYRot(), pShooter.getXRot());
    }

    private void hurtEntities() {
        AABB bounds = this.getBoundingBox().inflate(1.0D);

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (Entity entity : HelperMethods.getEntityCollisions(this.level(), bounds)) {
                    if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner) continue;

                    float factor = 1.0F - (((float) this.getTime() - DELAY) / DURATION);

                    if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.RED.get()), DAMAGE * factor * cap.getGrade().getRealPower(owner))) {
                        entity.setDeltaMovement(this.getLookAngle().multiply(1.0D, 0.25D, 1.0D).scale(LAUNCH_POWER));
                        entity.hurtMarked = true;
                    }
                }
            });
        }
    }

    @Override
    protected void onHit(@NotNull HitResult pResult) {
        super.onHit(pResult);

        if (this.level().isClientSide) return;

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                float radius = EXPLOSIVE_POWER * cap.getGrade().getRealPower(owner);

                Vec3 offset = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());
                this.level().explode(owner, JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.RED.get()), null, offset, radius, false,
                        this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
            });
        }
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() < DELAY) {
                if (!owner.isAlive()) {
                    this.discard();
                } else {
                    if (this.getTime() % 5 == 0) {
                        owner.swing(InteractionHand.MAIN_HAND);
                    }
                    Vec3 look = HelperMethods.getLookAngle(owner);
                    Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look);
                    this.moveTo(spawn.x(), spawn.y(), spawn.z(), owner.getYRot(), owner.getXRot());
                }
            } else if (this.getTime() >= DURATION) {
                this.discard();
            } else if (this.getTime() >= DELAY) {
                this.hurtEntities();

                ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                for (BlueProjectile blue : HelperMethods.getEntityCollisionsOfClass(BlueProjectile.class, this.level(), this.getBoundingBox().expandTowards(this.getDeltaMovement()))) {
                    if (cap.isCooldownDone(JJKAbilities.HOLLOW_PURPLE.get())) {
                        cap.addCooldown(owner, JJKAbilities.HOLLOW_PURPLE.get());

                        HollowPurpleExplosion explosion = new HollowPurpleExplosion(owner, blue.position().add(0.0D, blue.getBbHeight() / 2.0F, 0.0D));
                        this.level().addFreshEntity(explosion);

                        blue.discard();
                        this.discard();
                    }
                }

                if (this.getTime() == DELAY) {
                    this.setDeltaMovement(this.getLookAngle().scale(SPEED));
                } else if (this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                    this.discard();
                }
            }
        }
    }
}
