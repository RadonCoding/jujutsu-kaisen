package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class HollowPurpleExplosion extends JujutsuProjectile {
    public static final int DURATION = 3 * 20;
    private static final float DAMAGE = 15.0F;
    private static final float RADIUS = 5.0F;

    public HollowPurpleExplosion(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HollowPurpleExplosion(Entity pShooter, Vec3 pos) {
        super(JJKEntities.HOLLOW_PURPLE_EXPLOSION.get(), pShooter.level(), pShooter);

        this.setOwner(pShooter);

        if (this.getOwner() instanceof LivingEntity owner) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            float radius = RADIUS * cap.getGrade().getRealPower(owner);
            this.setPos(pos.subtract(0.0D, radius / 2.0F, 0.0D));
        }
    }

    private void hurtEntities() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        AABB bounds = this.getBoundingBox();

        for (Entity entity : HelperMethods.getEntityCollisions(this.level(), bounds)) {
            if ((entity instanceof LivingEntity living && !owner.canAttack(living))) continue;
            entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.HOLLOW_PURPLE.get()),
                    (DAMAGE * cap.getGrade().getRealPower(owner)) * (entity == owner ? 0.25F : 1.0F));
        }
    }

    private void spawnParticles() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        float radius = RADIUS * cap.getGrade().getRealPower(owner);

        Vec3 center = new Vec3(this.getX() + (this.random.nextDouble() - 0.5D) * radius,
                this.getY() + (this.random.nextDouble() - 0.5D) * radius,
                this.getZ() + (this.random.nextDouble() - 0.5D) * radius);
        this.level().addParticle(ParticleTypes.EXPLOSION, center.x(), center.y(), center.z(), 1.0D, 0.0D, 0.0D);
        this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, center.x(), center.y(), center.z(), 1.0D, 0.0D, 0.0D);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        if (!(this.getOwner() instanceof LivingEntity owner)) return super.getDimensions(pPose);

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        float radius = RADIUS * cap.getGrade().getRealPower(owner);

        return EntityDimensions.fixed(radius, radius);
    }

    @Override
    public void tick() {
        this.refreshDimensions();

        this.spawnParticles();

        if (this.getTime() >= DURATION) {
            this.discard();
        } else if (this.getTime() == 0) {
            if (!(this.getOwner() instanceof LivingEntity owner)) return;
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            ExplosionHandler.spawn(this.level().dimension(), BlockPos.containing(this.position().add(0.0D, this.getBbHeight() / 2.0F, 0.0D)),
                    RADIUS * cap.getGrade().getRealPower(owner), DURATION, owner, JJKAbilities.HOLLOW_PURPLE.get());
        } else {
            this.hurtEntities();
        }
        super.tick();
    }
}
