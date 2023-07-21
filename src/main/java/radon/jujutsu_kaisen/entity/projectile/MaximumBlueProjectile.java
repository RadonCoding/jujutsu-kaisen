package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.JJKEntities;

public class MaximumBlueProjectile extends BlueProjectile {
    private static final double SPIN_RADIUS = 5.0D;

    private double angle;

    public MaximumBlueProjectile(EntityType<? extends MaximumBlueProjectile> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public MaximumBlueProjectile(LivingEntity pShooter) {
        super(JJKEntities.MAXIMUM_BLUE.get(), pShooter.level, pShooter);

        this.spin();
    }

    @Override
    protected double getBallRadius() {
        return super.getBallRadius() * 1.5D;
    }

    @Override
    protected int getInterval() {
        return 1;
    }

    @Override
    protected float getDamage() {
        return super.getDamage() * 1.5F;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putDouble("angle", this.angle);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.angle = pCompound.getDouble("angle");
    }

    private void spin() {
        Entity owner = this.getOwner();

        if (owner != null) {
            this.angle += (360.0F / this.getDuration()) * 2;
            double radians = Math.toRadians(this.angle);
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
