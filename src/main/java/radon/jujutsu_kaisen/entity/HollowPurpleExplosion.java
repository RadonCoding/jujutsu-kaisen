package radon.jujutsu_kaisen.entity;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;

public class HollowPurpleExplosion extends JujutsuProjectile {
    public static final int DURATION = 3 * 20;
    private static final float EXPLOSIVE_POWER = 5.0F;
    private static final float MAX_EXPLOSION = 20.0F;

    public HollowPurpleExplosion(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public HollowPurpleExplosion(LivingEntity owner, float power, Vec3 pos) {
        this(JJKEntities.HOLLOW_PURPLE_EXPLOSION.get(), owner.level());

        this.setOwner(owner);
        this.setPower(power);

        float radius = this.getRadius();
        this.setPos(pos.subtract(0.0D, radius / 2.0F, 0.0D));
    }

    private float getRadius() {
        return Math.min(MAX_EXPLOSION, EXPLOSIVE_POWER * this.getPower());
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        float radius = this.getRadius();
        return EntityDimensions.fixed(radius, radius);
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (this.level().isClientSide) return;

        if (this.getTime() >= DURATION) {
            this.discard();
            return;
        }

        if (this.getTime() - 1 == 0) {
            if (!(this.getOwner() instanceof LivingEntity owner)) return;

            this.playSound(JJKSounds.HOLLOW_PURPLE_EXPLOSION.get(), 3.0F, 1.0F);

            float radius = this.getRadius();
            int duration = (int) (radius / 5.0F * 20);

            Vec3 offset = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());
            ExplosionHandler.spawn(this.level().dimension(), offset, radius, duration, this.getPower() * 0.1F, owner,
                    JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.HOLLOW_PURPLE.get()), false);
        }
    }
}
