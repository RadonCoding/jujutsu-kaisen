package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class HollowPurpleExplosion extends JujutsuProjectile {
    public static final int DURATION = 3 * 20;
    private static final float DAMAGE = 1.0F;
    private static final float RADIUS = 5.0F;
    private static final float MAX_EXPLOSION = 25.0F;

    public HollowPurpleExplosion(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.noCulling = true;
    }

    public HollowPurpleExplosion(LivingEntity owner, float power, Vec3 pos) {
        this(JJKEntities.HOLLOW_PURPLE_EXPLOSION.get(), owner.level());

        this.setOwner(owner);
        this.setPower(power);

        float radius = RADIUS * this.getPower();
        this.setPos(pos.subtract(0.0D, radius / 2.0F, 0.0D));
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        float radius = RADIUS * this.getPower();
        return EntityDimensions.fixed(radius, radius);
    }

    @Override
    public void tick() {
        this.refreshDimensions();

        if (this.level().isClientSide) return;

        if (this.getTime() >= DURATION) {
            this.discard();
        } else if (this.getTime() == 0) {
            if (!(this.getOwner() instanceof LivingEntity owner)) return;

            float radius = Math.min(MAX_EXPLOSION, RADIUS * this.getPower());
            int duration = (int) (radius / 5.0F * 20);
            ExplosionHandler.spawn(this.level().dimension(), this.position().add(0.0D, this.getBbHeight() / 2.0F, 0.0D), radius,
                    duration, DAMAGE * this.getPower(),  owner, JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.HOLLOW_PURPLE.get()), false);
        }
        super.tick();
    }
}
