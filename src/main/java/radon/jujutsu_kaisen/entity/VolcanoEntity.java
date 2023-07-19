package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.projectile.LavaProjectile;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VolcanoEntity extends JujutsuProjectile implements GeoEntity {
    private static final int DELAY = 20;
    private static final int DURATION = 3 * 20;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public VolcanoEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public VolcanoEntity(LivingEntity pShooter, BlockPos pos, Direction dir) {
        super(JJKEntities.VOLCANO.get(), pShooter.level, pShooter);

        Vec3 center = pos.relative(dir).getCenter();
        center = center.subtract(dir.getStepX() * 0.5D, dir.getStepY() * 0.5D, dir.getStepZ() * 0.5D);
        float xRot = (float) (Mth.atan2(dir.getStepY(), dir.getStepX()) * 180.0F / Mth.PI);
        switch (dir) {
            case UP, DOWN -> xRot = -xRot;
            case WEST -> xRot -= 180.0F;
        }
        this.moveTo(center.x(), center.y() - this.getBbHeight() / 2.0F, center.z(), dir.toYRot(), xRot);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getTime() >= DURATION) {
            this.discard();
        } else if (this.getTime() >= DELAY) {
            if (this.getOwner() instanceof LivingEntity owner) {
                Vec3 pos = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ());
                LavaProjectile lava = new LavaProjectile(owner, pos.x(), pos.y(), pos.z());
                lava.shootFromRotation(owner, this.getXRot(), this.getYRot(), 0.0F, LavaProjectile.SPEED, 0.0F);
                this.level.addFreshEntity(lava);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
