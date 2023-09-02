package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MaximumBlueProjectile extends BlueProjectile {
    private static final double OFFSET = 10.0D;
    private static final int DELAY = 20;

    public MaximumBlueProjectile(EntityType<? extends MaximumBlueProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public MaximumBlueProjectile(LivingEntity pShooter) {
        super(JJKEntities.MAXIMUM_BLUE.get(), pShooter.level, pShooter);

        Vec3 look = HelperMethods.getLookAngle(pShooter);
        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ()).add(look);
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), pShooter.getYRot(), pShooter.getXRot());
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
        if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() % 5 == 0) {
                owner.swing(InteractionHand.MAIN_HAND);
            }
            Vec3 center = owner.getEyePosition();
            Vec3 pos = center.add(HelperMethods.getLookAngle(owner).scale(OFFSET));
            this.setPos(pos.x(), pos.y() - (this.getBbHeight() / 2.0F), pos.z());
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getTime() >= DELAY) {
            this.spin();
        }
    }
}
