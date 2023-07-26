package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.entity.JJKEntities;

public class MaximumBlueProjectile extends BlueProjectile {
    private static final double SPIN_RADIUS = 10.0D;

    public MaximumBlueProjectile(EntityType<? extends MaximumBlueProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public MaximumBlueProjectile(LivingEntity pShooter) {
        super(JJKEntities.MAXIMUM_BLUE.get(), pShooter.level, pShooter);

        this.spin();
    }

    @Override
    public float getRadius() {
        return super.getRadius() * 2.0F;
    }

    @Override
    protected int getInterval() {
        return 1;
    }

    @Override
    protected float getDamage() {
        return super.getDamage() * 2.0F;
    }

    private void spin() {
        Entity owner = this.getOwner();

        if (owner != null) {
            double radians = Math.toRadians(owner.getYRot() + 90.0F);
            Vec3 center = owner.position();
            double x = center.x() + SPIN_RADIUS * Math.cos(radians);
            double y = center.y();
            double z = center.z() + SPIN_RADIUS * Math.sin(radians);
            this.setPos(x, y, z);
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.spin();
    }
}
