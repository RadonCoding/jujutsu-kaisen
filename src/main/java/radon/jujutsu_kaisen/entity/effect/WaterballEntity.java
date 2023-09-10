package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DurationBlockEntity;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WaterballEntity extends JujutsuProjectile implements GeoEntity {
    private static final RawAnimation SPIN = RawAnimation.begin().thenLoop("misc.spin");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final int DURATION = 3 * 20;
    private static final int INTERVAL = 20;
    private static final int WIDTH = 16;
    private static final int HEIGHT = 8;
    private static final float DAMAGE = 10.0F;

    public WaterballEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public WaterballEntity(LivingEntity pShooter) {
        super(JJKEntities.WATERBALL.get(), pShooter.level, pShooter);

        Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - (this.getBbHeight() / 2.0F), pShooter.getZ())
                .add(HelperMethods.getLookAngle(pShooter));
        this.moveTo(spawn.x(), spawn.y(), spawn.z(), pShooter.getYRot(), pShooter.getXRot());
    }

    private static boolean isInside(LivingEntity owner, BlockPos pos) {
        BlockPos center = owner.blockPosition();
        BlockPos relative = pos.subtract(center);
        return relative.getY() <= HEIGHT && relative.distSqr(Vec3i.ZERO) < WIDTH * WIDTH;
    }

    private static AABB getBounds(LivingEntity owner) {
        BlockPos center = owner.blockPosition();
        return new AABB(center.getX() - WIDTH, center.getY() - HEIGHT, center.getZ() - WIDTH,
                center.getX() + WIDTH, center.getY() + HEIGHT, center.getZ() + WIDTH);
    }

    private void createWave(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        BlockPos center = owner.blockPosition();

        for (Entity entity : owner.level.getEntities(owner, getBounds(owner))) {
            if ((!(entity instanceof LivingEntity living) || !owner.canAttack(living)) && !isInside(owner, entity.blockPosition()))
                continue;

            if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.DISASTER_TIDES.get()), DAMAGE * cap.getGrade().getPower())) {
                entity.setDeltaMovement(entity.position().subtract(center.getCenter()));
            }
        }

        for (int i = 0; i < WIDTH; i++) {
            int horizontal = i;

            int delay = i + 1;

            cap.delayTickEvent(() -> {
                for (int j = -HEIGHT; j < HEIGHT; j++) {
                    for (int x = -horizontal; x <= horizontal; x++) {
                        for (int z = -horizontal; z <= horizontal; z++) {
                            double distance = Math.sqrt(x * x + -j * -j + z * z);

                            if (distance <= horizontal && distance >= horizontal - 1) {
                                BlockPos pos = center.offset(x, j, z);

                                BlockState state = owner.level.getBlockState(pos);

                                if (!state.isAir() || state.canOcclude()) continue;

                                owner.level.setBlockAndUpdate(pos, JJKBlocks.FAKE_WATER.get().defaultBlockState());

                                if (owner.level.getBlockEntity(pos) instanceof DurationBlockEntity be) {
                                    be.create(delay, state);
                                }
                            }
                        }
                    }
                }
            }, i);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        if (this.getTime() <= DURATION) {
            if (!owner.isAlive()) {
                this.discard();
            } else {
                if (this.getTime() % 5 == 0) {
                    owner.swing(InteractionHand.MAIN_HAND);
                }
                if (this.getTime() % INTERVAL == 0) {
                    this.createWave(owner);
                }
                Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                        .add(HelperMethods.getLookAngle(owner));
                this.setPos(spawn.x(), spawn.y(), spawn.z());
            }
        } else {
            this.discard();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Spin", animationState -> animationState.setAndContinue(SPIN)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
