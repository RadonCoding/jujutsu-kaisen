package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VolcanoEntity extends JujutsuProjectile implements GeoEntity {
    public static final int DELAY = 20;
    private static final int DURATION = 3 * 20;
    private static final float DAMAGE = 5.0F;
    private static final double RANGE = 20.0D;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public VolcanoEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public VolcanoEntity(LivingEntity owner, float power, BlockPos pos, Direction dir) {
        super(JJKEntities.VOLCANO.get(), owner.level(), owner, power);

        Vec3 center = pos.relative(dir).getCenter();
        center = center.subtract(dir.getStepX() * 0.5D, dir.getStepY() * 0.5D, dir.getStepZ() * 0.5D);
        float xRot = (float) (Mth.atan2(dir.getStepY(), dir.getStepX()) * 180.0F / Mth.PI);
        switch (dir) {
            case UP, DOWN -> xRot = -xRot;
            case WEST -> xRot -= 180.0F;
        }
        this.moveTo(center.x, center.y - this.getBbHeight() / 2.0F, center.z, dir.toYRot(), xRot);
    }

    @Override
    public @NotNull Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getTime() >= DURATION) {
            this.discard();
        } else if (this.getTime() >= DELAY) {
            Vec3 look = HelperMethods.getLookAngle(this);

            for (int i = 0; i < 96; i++) {
                double theta = HelperMethods.RANDOM.nextDouble() * 2 * Math.PI;
                double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
                double r = HelperMethods.RANDOM.nextDouble() * 0.8D;
                double x = r * Math.sin(phi) * Math.cos(theta);
                double y = r * Math.sin(phi) * Math.sin(theta);
                double z = r * Math.cos(phi);
                Vec3 start = this.position().add(0.0D, this.getBbHeight() / 2.0F, 0.0D);
                Vec3 end = start.add(look.scale(RANGE)).add(x, y, z);
                Vec3 speed = start.subtract(end).scale(1.0D / 12).reverse();
                this.level().addParticle(ParticleTypes.FLAME, start.x, start.y, start.z, speed.x, speed.y, speed.z);
            }

            if (this.getOwner() instanceof LivingEntity owner) {
                AABB bounds = this.getBoundingBox().expandTowards(look.scale(RANGE)).inflate(1.0D);

                for (Entity entity : this.level().getEntities(owner, bounds)) {
                    if (!(entity instanceof LivingEntity living) || !owner.canAttack(living) || !living.hasLineOfSight(this)) continue;

                    if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.VOLCANO.get()), DAMAGE * this.getPower())) {
                        entity.setSecondsOnFire(5);
                    }
                }
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
