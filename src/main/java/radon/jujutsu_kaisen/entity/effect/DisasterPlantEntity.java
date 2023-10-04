package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.projectile.CursedBudProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class DisasterPlantEntity extends JujutsuProjectile implements GeoEntity {
    private static final int DURATION = 3 * 20;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private UUID targetUUID;
    @Nullable
    private LivingEntity cachedTarget;

    public DisasterPlantEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DisasterPlantEntity(LivingEntity pShooter, LivingEntity target) {
        super(JJKEntities.DISASTER_PLANT.get(), pShooter.level(), pShooter);

        this.setTarget(target);

        Vec3 pos = pShooter.position()
                .subtract(HelperMethods.getLookAngle(pShooter)
                        .multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x(), pos.y(), pos.z(), pShooter.getXRot(), pShooter.getYRot());
    }

    public void setTarget(@Nullable LivingEntity target) {
        if (target != null) {
            this.targetUUID = target.getUUID();
            this.cachedTarget = target;
        }
    }

    @Nullable
    public LivingEntity getTarget() {
        if (this.cachedTarget != null && !this.cachedTarget.isRemoved()) {
            return this.cachedTarget;
        } else if (this.targetUUID != null && this.level() instanceof ServerLevel) {
            this.cachedTarget = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.targetUUID);
            return this.cachedTarget;
        } else {
            return null;
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.targetUUID != null) {
            pCompound.putUUID("target", this.targetUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("target")) {
            this.targetUUID = pCompound.getUUID("target");
        }
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.getTarget();

        if (target == null) return;

        this.setXRot(HelperMethods.getXRotD(this, target.getEyePosition()));
        this.xRotO = this.getXRot();

        this.setYRot(HelperMethods.getYRotD(this, target.getEyePosition()));
        this.yRotO = this.getYRot();

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        if (this.getTime() >= DURATION) {
            this.discard();
        } else if (this.getTime() % 5 == 0) {
            this.level().addFreshEntity(new CursedBudProjectile(owner, this));
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
