package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.*;
import radon.jujutsu_kaisen.entity.effect.ForestSpikeEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ForestSpikes extends Ability {
    private static final double RANGE = 30.0D;
    private static final float SPREAD = 5.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && owner.hasLineOfSight(target);
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
    public Status isTriggerable(LivingEntity owner) {
        BlockHitResult hit = this.getBlockHit(owner);

        if (hit == null) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        BlockHitResult hit = this.getBlockHit(owner);

        if (hit != null) {
            owner.level().playSound(null, hit.getBlockPos(), JJKSounds.FOREST_SPIKES.get(), SoundSource.MASTER, 1.0F, 1.0F);

            Direction dir = hit.getDirection();
            BlockPos pos = hit.getBlockPos();

            for (int i = 0; i < 16; i++) {
                ForestSpikeEntity spike = new ForestSpikeEntity(owner, this.getOutput(owner));

                Vec3 center = pos.relative(dir).getCenter()
                        .subtract(dir.getStepX() * 0.5D, dir.getStepY() * 0.5D, dir.getStepZ() * 0.5D);
                float yRot = dir.toYRot() + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 60.0F;
                float xRot = (float) (Mth.atan2(dir.getStepY(), dir.getStepX()) * 180.0F / Mth.PI) + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 60.0F;

                switch (dir) {
                    case UP, DOWN -> xRot = -xRot;
                    case WEST -> xRot -= 180.0F;
                }

                Vec3 offset = center.add((HelperMethods.RANDOM.nextDouble() - 0.5D) * SPREAD, 0.0D, (HelperMethods.RANDOM.nextDouble() - 0.5D) * SPREAD);
                spike.moveTo(offset.x, offset.y - spike.getBbHeight() / 2.0F, offset.z, yRot, xRot);

                if (owner.level().getBlockState(BlockPos.containing(offset.subtract(RotationUtil.getTargetAdjustedLookAngle(spike)))).isAir()) {
                    spike.setPos(center);
                }
                owner.level().addFreshEntity(spike);
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Override
    public Classification getClassification() {
        return Classification.PLANTS;
    }
}
