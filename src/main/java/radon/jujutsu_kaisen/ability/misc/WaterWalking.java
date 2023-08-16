package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.util.HelperMethods;

public class WaterWalking extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        Vec3 movement = owner.getDeltaMovement();
        return getAdjustedY(owner.level, owner.getBoundingBox(), movement) != movement.y();
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    private static boolean isFluid(BlockGetter getter, BlockPos pos) {
        return !getter.getFluidState(pos).isEmpty();
    }

    private static double getAdjustedY(BlockGetter getter, AABB bb, Vec3 movement) {
        AABB feet = new AABB(
                bb.minX,
                bb.minY,
                bb.minZ,
                bb.maxX,
                bb.minY,
                bb.maxZ
        );
        AABB ankles = new AABB(
                bb.minX,
                bb.minY + 0.5D,
                bb.minZ,
                bb.maxX,
                bb.minY + 0.5D,
                bb.maxZ
        );

        double movementY = movement.y();

        if (isFluid(getter, BlockPos.containing(ankles.maxX, ankles.maxY, ankles.maxZ))) {
            movementY = 0.5D;
        }
        else if (isFluid(getter, BlockPos.containing(ankles.minX, ankles.minY, ankles.minZ))) {
            movementY = 0.25D;
        }
        else if (movementY < 0.0D && isFluid(getter, BlockPos.containing(feet.minX, feet.minY, feet.minZ))) {
            movementY = 0.1D;
        }
        else if (movementY < 0.0D && isFluid(getter, BlockPos.containing(feet.minX, feet.minY - 0.1D, feet.minZ))) {
            movementY = 0.0D;
        }
        return movementY;
    }

    private void checkWaterWalking(LivingEntity owner) {
        if (owner.isShiftKeyDown()) return;

        Vec3 movement = owner.getDeltaMovement();
        double movementY = getAdjustedY(owner.level, owner.getBoundingBox(), movement);

        if (movementY != movement.y()) {
            owner.setDeltaMovement(movement.x(), movementY, movement.z());
            owner.setOnGround(true);
            owner.resetFallDistance();
        }
    }

    @Override
    public void run(LivingEntity owner) {
        this.checkWaterWalking(owner);

        if (owner.level instanceof ServerLevel level) {
            level.sendParticles(JJKParticles.CURSED_ENERGY.get(), owner.getX() + (HelperMethods.RANDOM.nextGaussian() * 0.1D) + 0.15D,
                    owner.getY(),
                    owner.getZ() + HelperMethods.RANDOM.nextGaussian() * 0.1D,
                    0, 0.0D, 0.23D, 0.0D, -0.1D);
            level.sendParticles(JJKParticles.CURSED_ENERGY.get(), owner.getX() + (HelperMethods.RANDOM.nextGaussian() * 0.1D) - 0.15D,
                    owner.getY(),
                    owner.getZ() + HelperMethods.RANDOM.nextGaussian() * 0.1D,
                    0, 0.0D, 0.23D, 0.0D, -0.1D);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.001F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public boolean isDisplayed() {
        return false;
    }
}
