package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ShadowTravel extends Ability {
    private static final double RANGE = 100.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        return this.getTarget(owner) instanceof EntityHitResult hit && hit.getEntity() == target && HelperMethods.RANDOM.nextInt(20) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable HitResult getTarget(LivingEntity owner) {
        HitResult hit = RotationUtil.getLookAtHit(owner, RANGE);
        if (hit.getType() == HitResult.Type.MISS) return null;
        if (hit.getType() == HitResult.Type.BLOCK && ((BlockHitResult) hit).getDirection() == Direction.DOWN) return null;

        long time = owner.level().getLevelData().getDayTime();
        boolean night = time >= 13000 && time < 24000;

        if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof TenShadowsSummon ||
                ((night || owner.level().getBrightness(LightLayer.SKY, BlockPos.containing(hit.getLocation())) < 15) &&
                        owner.level().getBrightness(LightLayer.BLOCK, BlockPos.containing(hit.getLocation())) == 0)) {
            return hit;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (!(owner.level() instanceof ServerLevel level)) return;

        HitResult target = this.getTarget(owner);

        if (target != null) {
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.FISHING_BOBBER_SPLASH, SoundSource.MASTER, 1.0F, 1.0F);

            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < owner.getBbHeight() * owner.getBbHeight(); j++) {
                    level.sendParticles(ParticleTypes.SMOKE, owner.getX() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), owner.getY(),
                            owner.getZ() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), 0,
                            HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D, HelperMethods.RANDOM.nextGaussian() * 0.075D, 1.0D);
                    level.sendParticles(ParticleTypes.LARGE_SMOKE, owner.getX() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), owner.getY(),
                            owner.getZ() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), 0,
                            HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D, HelperMethods.RANDOM.nextGaussian() * 0.075D, 1.0D);
                }
            }

            Vec3 pos = target.getLocation();
            owner.teleportTo(pos.x, pos.y, pos.z);

            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.FISHING_BOBBER_SPLASH, SoundSource.MASTER, 1.0F, 1.0F);

            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < owner.getBbHeight() * owner.getBbHeight(); j++) {
                    level.sendParticles(ParticleTypes.SMOKE, owner.getX() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), owner.getY(),
                            owner.getZ() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), 0,
                            HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D, HelperMethods.RANDOM.nextGaussian() * 0.075D, 1.0D);
                    level.sendParticles(ParticleTypes.LARGE_SMOKE, owner.getX() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), owner.getY(),
                            owner.getZ() + (owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F), 0,
                            HelperMethods.RANDOM.nextGaussian() * 0.075D, HelperMethods.RANDOM.nextGaussian() * 0.25D, HelperMethods.RANDOM.nextGaussian() * 0.075D, 1.0D);
                }
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            HitResult target = this.getTarget(owner);

            if (target == null) {
                return Status.FAILURE;
            }
        }
        return super.isTriggerable(owner);
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
