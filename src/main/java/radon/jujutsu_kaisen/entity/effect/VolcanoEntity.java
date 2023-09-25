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
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VolcanoEntity extends JujutsuProjectile implements GeoEntity {
    public static final int DELAY = 20;
    private static final int DURATION = 3 * 20;
    private static final float DAMAGE = 5.0F;

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
            Vec3 look = this.getLookAngle();

            for (int i = 0; i < 48; i++) {
                Vec3 speed = look.add((this.random.nextDouble() - 0.5D) * 0.2D, (this.random.nextDouble() - 0.5D) * 0.2D, (this.random.nextDouble() - 0.5D) * 0.2D);
                this.level.addParticle(ParticleTypes.FLAME, this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ(), speed.x(), speed.y(), speed.z());
            }

            if (this.getOwner() instanceof LivingEntity owner) {
                Vec3 length = look.scale(5.0D);
                AABB bounds = this.getBoundingBox().inflate(0.0D, length.y(), 0.0D);

                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    for (Entity entity : this.level.getEntities(owner, bounds)) {
                        if (!(entity instanceof LivingEntity living) || !owner.canAttack(living)) continue;

                        if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.VOLCANO.get()), DAMAGE * cap.getGrade().getRealPower(owner))) {
                            entity.setSecondsOnFire(5);
                        }
                    }
                });
            }
        }

        if (this.getTime() % 5 == 0) {
            Vec3 speed = this.getLookAngle().scale(0.25D);
            this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + (this.getBbHeight() / 2.0F), this.getZ(), speed.x(), speed.y(), speed.z());
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
