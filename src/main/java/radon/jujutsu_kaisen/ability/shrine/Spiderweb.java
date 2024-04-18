package radon.jujutsu_kaisen.ability.shrine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Spiderweb extends Ability {
    private static final int RANGE = 3;
    private static final float EXPLOSIVE_POWER = 2.0F;
    private static final float MIN_EXPLOSIVE_POWER = 8.0F;
    private static final float MAX_EXPLOSIVE_POWER = 24.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target)) return false;
        return HelperMethods.RANDOM.nextInt(40) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable BlockHitResult getBlockHit(LivingEntity owner) {
        Vec3 start = owner.getEyePosition();
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 end = start.add(look.scale(RANGE));
        HitResult result = RotationUtil.getHitResult(owner, start, end);

        if (result.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) result;
        } else if (result.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) result).getEntity();
            Vec3 offset = entity.position().subtract(0.0D, 5.0D, 0.0D);
            return owner.level().clip(new ClipContext(entity.position(), offset, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        owner.swing(InteractionHand.MAIN_HAND, true);

        BlockHitResult hit = this.getBlockHit(owner);

        if (hit != null) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            float radius = Math.max(MIN_EXPLOSIVE_POWER, Math.min(MAX_EXPLOSIVE_POWER, EXPLOSIVE_POWER * this.getOutput(owner)));
            float real = (radius % 2 == 0) ? radius + 1 : radius;

            Vec3 center = hit.getBlockPos().getCenter().add(RotationUtil.getTargetAdjustedLookAngle(owner).scale(real * 0.5F));

            AABB bounds = AABB.ofSize(center, real, real, real);

            BlockPos.betweenClosedStream(bounds).forEach(pos -> {
                Vec3 current = pos.getCenter();

                if (HelperMethods.RANDOM.nextInt(Math.round(radius)) == 0) {
                    data.delayTickEvent(() -> {
                        owner.level().addFreshEntity(new DismantleProjectile(owner, this.getOutput(owner), (HelperMethods.RANDOM.nextFloat() - 0.5F) * 360.0F,
                                current, HelperMethods.RANDOM.nextInt(DismantleProjectile.MIN_LENGTH, DismantleProjectile.MAX_LENGTH + 1),
                                true, true));
                    }, (int) Math.round(Math.sqrt(owner.distanceToSqr(current))));
                }
            });
        }
    }


    @Override
    public Status isTriggerable(LivingEntity owner) {
        BlockHitResult hit = this.getBlockHit(owner);

        if (hit == null) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.SLASHING;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}