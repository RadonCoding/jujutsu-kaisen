package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.ten_shadows.ToadEntity;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class EelGrappleProjectile extends JujutsuProjectile implements GeoEntity {
    public static final float SPEED = 2.0F;
    private static final int DURATION = 5 * 20;
    public static final double RANGE = 16.0D;
    private static final double PULL_STRENGTH = 5.0D;

    private LivingEntity grabbed;
    private Vec3 pos;

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
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        if (this.level().isClientSide) return;
        if (!(pResult.getEntity() instanceof LivingEntity target)) return;

        this.grabbed = target;
        this.pos = target.position();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);

        this.discard();
    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = this.getOwner();

        if (this.level().isClientSide) return;

        if (this.getTime() >= DURATION) {
            if (owner != null && this.grabbed != null) {
                this.grabbed.setDeltaMovement(owner.position().subtract(this.grabbed.position())
                        .normalize()
                        .scale(PULL_STRENGTH)
                        .multiply(1.0D, 0.5D, 1.0D));
                this.grabbed.hurtMarked = true;
            }
            this.discard();
            return;
        }

        if (owner == null) return;

        if (this.grabbed != null) {
            if (this.grabbed.isDeadOrDying() || this.grabbed.isRemoved()) this.discard();

            this.grabbed.teleportTo(this.pos.x, this.pos.y, this.pos.z);

            this.setPos(this.grabbed.getX(), this.grabbed.getY() + (this.grabbed.getBbHeight() / 2.0F), this.grabbed.getZ());
            this.setDeltaMovement(Vec3.ZERO);
        } else {
            if (this.distanceTo(owner) >= RANGE) {
                this.discard();
            }
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
