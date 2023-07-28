package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.entity.JJKEntities;

public class MaximumBlueProjectile extends BlueProjectile {
    private static final double OFFSET = 10.0D;

    public MaximumBlueProjectile(EntityType<? extends MaximumBlueProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public MaximumBlueProjectile(LivingEntity pShooter) {
        super(JJKEntities.MAXIMUM_BLUE.get(), pShooter.level, pShooter);

        this.spin();
    }

    @Override
    public float getRadius() {
        return super.getRadius() * 1.5F;
    }

    @Override
    protected float getDamage() {
        return super.getDamage() * 2.0F;
    }

    private void spin() {
        Entity owner = this.getOwner();

        if (owner != null) {
            Vec3 center = owner.getEyePosition();
            Vec3 pos = center.add(owner.getLookAngle().scale(OFFSET));
            this.setPos(pos.x(), pos.y() - (this.getBbHeight() / 2.0F), pos.z());
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() % 5 == 0) {
                owner.swing(InteractionHand.MAIN_HAND);
            }
        }
        this.spin();
    }
}
