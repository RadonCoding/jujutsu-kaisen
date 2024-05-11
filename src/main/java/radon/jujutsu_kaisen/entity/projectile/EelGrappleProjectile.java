package radon.jujutsu_kaisen.entity.projectile;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EelGrappleProjectile extends JujutsuProjectile implements GeoEntity {
    public static final float SPEED = 2.0F;
    private static final int DURATION = 5 * 20;
    public static final double RANGE = 16.0D;

    private LivingEntity pulled;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public EelGrappleProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public EelGrappleProjectile(LivingEntity owner) {
        this(JJKEntities.EEL_GRAPPLE.get(), owner.level());

        this.setOwner(owner);

        Vec3 spawn = new Vec3(owner.getX(), owner.getY() + (owner.getBbHeight() / 2.0F) - (this.getBbHeight() / 2.0F), owner.getZ());
        this.setPos(spawn.x, spawn.y, spawn.z);

        this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);

        this.discard();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        if (this.level().isClientSide) return;
        if (!(pResult.getEntity() instanceof LivingEntity target)) return;

        this.pulled = target;

        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = this.getOwner();

        if (this.getTime() >= DURATION) {
            this.discard();
            return;
        }

        if (owner == null) return;

        if (this.pulled != null) {
            if (this.pulled.isRemoved() || this.pulled.isDeadOrDying()) {
                this.discard();
                return;
            }

            this.setPos(this.pulled.getX(), this.pulled.getY() + (this.pulled.getBbHeight() / 2.0F), this.pulled.getZ());

            this.pulled.setDeltaMovement(owner.position().subtract(this.pulled.position()).normalize());
            this.pulled.hurtMarked = true;

            if (this.pulled.distanceTo(owner) <= 1.0D) {
                this.discard();
            }
        } else if (this.distanceTo(owner) >= RANGE) {
            this.discard();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
