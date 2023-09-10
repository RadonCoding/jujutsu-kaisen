package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ScissorEntity extends JujutsuProjectile implements GeoEntity {
    private static final float DAMAGE = 20.0F;
    private static final int CUT_DURATION = 5;
    private static final int DELAY = 20;
    private static final double SPEED = 2.5D;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation CUT = RawAnimation.begin().thenPlay("misc.cut");

    private LivingEntity target;

    public ScissorEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ScissorEntity(LivingEntity pShooter, LivingEntity target) {
        super(JJKEntities.SCISSOR.get(), pShooter.level, pShooter);

        this.target = target;

        double offsetX = this.random.nextDouble() * 4 - 2;
        double offsetZ = this.random.nextDouble() * 4 - 2;
        this.setPos(target.position().add(offsetX, target.getBbHeight() * 1.5F, offsetZ));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    private PlayState cutPredicate(AnimationState<ScissorEntity> animationState) {
        if (this.getTime() - CUT_DURATION > 0) {
            return animationState.setAndContinue(CUT);
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Cut", this::cutPredicate));
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        if (this.getOwner() instanceof LivingEntity owner) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    pResult.getEntity().hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.SCISSORS.get()),
                            DAMAGE * cap.getGrade().getPower(owner)));
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level.isClientSide) return;

        if ((this.target == null || this.target.isRemoved() || !this.target.isAlive())) {
            this.discard();
            return;
        }

        Vec3 direction = this.target.position().subtract(this.position()).normalize();

        double pitch = Math.asin(direction.y());
        double yaw = Math.atan2(direction.x(), direction.z());
        this.setRot((float) Math.toDegrees(yaw), (float) Math.toDegrees(pitch));

        if (this.getTime() == DELAY) {
            this.setDeltaMovement(this.target.position().subtract(this.position()).normalize().scale(SPEED));
        } else if (this.getTime() > DELAY) {
            if (this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                this.discard();
            }
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
