package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class HollowPurpleExplosion extends JujutsuProjectile {
    public static final int DURATION = 3 * 20;
    private static final float DAMAGE = 10.0F;
    private static final float RADIUS = 5.0F;

    public HollowPurpleExplosion(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public HollowPurpleExplosion(Entity pShooter, Vec3 pos) {
        super(JJKEntities.HOLLOW_PURPLE_EXPLOSION.get(), pShooter.level, pShooter);

        this.setOwner(pShooter);

        this.setPos(pos);
    }

    private void hurtEntities() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        float radius = RADIUS * cap.getGrade().getPower();

        AABB bounds = this.getBoundingBox().inflate(radius);

        for (Entity entity : HelperMethods.getEntityCollisions(this.level, bounds)) {
            if ((entity instanceof LivingEntity living && !owner.canAttack(living))) continue;
            entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.HOLLOW_PURPLE.get()),
                    (DAMAGE * cap.getGrade().getPower(owner)) * (entity == owner ? 0.25F : 1.0F));
        }
    }

    private void spawnParticles() {
        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        float radius = RADIUS * cap.getGrade().getPower();

        Vec3 center = new Vec3(this.getX() + (this.random.nextDouble() - 0.5D) * radius,
                this.getY() + (this.random.nextDouble() - 0.5D) * radius,
                this.getZ() + (this.random.nextDouble() - 0.5D) * radius);
        this.level.addParticle(ParticleTypes.EXPLOSION, center.x(), center.y(), center.z(), 1.0D, 0.0D, 0.0D);
        this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, center.x(), center.y(), center.z(), 1.0D, 0.0D, 0.0D);
    }

    @Override
    public void tick() {
        this.spawnParticles();

        if (this.getTime() >= DURATION) {
            this.discard();
        } else if (this.getTime() == 0) {
            if (!(this.getOwner() instanceof LivingEntity owner)) return;
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            ExplosionHandler.spawn(this.level.dimension(), BlockPos.containing(this.position().add(0.0D, this.getBbHeight() / 2.0F, 0.0D)),
                    RADIUS * cap.getGrade().getPower(), DURATION, owner, JJKAbilities.HOLLOW_PURPLE.get());
        } else {
            this.hurtEntities();
        }
        super.tick();
    }
}
