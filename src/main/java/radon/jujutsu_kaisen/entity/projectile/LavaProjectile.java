package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;

public class LavaProjectile extends JujutsuProjectile {
    private static final float DAMAGE = 10.0F;
    private static final int DURATION = 20;
    public static final float SPEED = 3.0F;

    public LavaProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public LavaProjectile(LivingEntity pShooter, double x, double y, double z) {
        super(JJKEntities.LAVA.get(), pShooter.level, pShooter);

        this.moveTo(x, y, z);
    }

    private void hurtEntities() {
        AABB bounds = this.getBoundingBox().inflate(1.5D);

        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (Entity entity : this.level.getEntities(this, bounds)) {
                    if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner) continue;

                    entity.setSecondsOnFire(15);
                    entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner), DAMAGE * cap.getGrade().getPower());
                }
            });
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getTime() >= DURATION) {
            this.discard();
        } else {
            this.hurtEntities();
        }
    }
}
