package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
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
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;
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

    public WaterballEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public WaterballEntity(LivingEntity owner, float power) {
        super(JJKEntities.WATERBALL.get(), owner.level(), owner, power);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look));
    }

    private static boolean isInside(LivingEntity owner, BlockPos pos) {
        BlockPos center = owner.blockPosition();
        BlockPos relative = pos.subtract(center);
        return relative.getY() <= HEIGHT && relative.distSqr(Vec3i.ZERO) < WIDTH * WIDTH;
    }

    private void createWave(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        BlockPos center = owner.blockPosition();

        for (Entity entity : owner.level().getEntities(owner, AABB.ofSize(owner.position(), WIDTH, HEIGHT, WIDTH))) {
            if (!isInside(owner, entity.blockPosition())) continue;

            if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.DISASTER_TIDES.get()), DAMAGE * this.getPower())) {
                entity.setDeltaMovement(entity.position().subtract(center.getCenter()));
            }
        }

        for (int i = 0; i < WIDTH; i++) {
            int horizontal = i;

            int duration = (i + 1) / 2;

            data.delayTickEvent(() -> {
                for (int j = -HEIGHT; j < HEIGHT; j++) {
                    for (int x = -horizontal; x <= horizontal; x++) {
                        for (int z = -horizontal; z <= horizontal; z++) {
                            double distance = Math.sqrt(x * x + -j * -j + z * z);

                            if (distance <= horizontal && distance >= horizontal - 1) {
                                BlockPos pos = center.offset(x, j, z);

                                if (pos == center) continue;

                                if (!this.level().isInWorldBounds(pos)) continue;

                                BlockState state = owner.level().getBlockState(pos);

                                if (!state.isAir() || state.canOcclude()) continue;

                                owner.level().setBlockAndUpdate(pos, JJKBlocks.FAKE_WATER_DURATION.get().defaultBlockState());

                                if (owner.level().getBlockEntity(pos) instanceof DurationBlockEntity be) {
                                    be.create(duration, state);
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
                if (!this.level().isClientSide && this.getTime() % INTERVAL == 0) {
                    this.createWave(owner);
                }
                Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
                EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ())
                        .add(look));
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
