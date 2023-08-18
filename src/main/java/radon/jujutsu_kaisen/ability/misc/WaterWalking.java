package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.util.HelperMethods;

public class WaterWalking extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return !owner.getFeetBlockState().getFluidState().isEmpty() || !owner.level.getFluidState(owner.blockPosition().below()).isEmpty() || owner.isInFluidType();
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
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
