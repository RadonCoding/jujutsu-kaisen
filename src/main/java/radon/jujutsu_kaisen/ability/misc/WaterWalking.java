package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.VaporParticle;
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
            ParticleOptions particle = new VaporParticle.VaporParticleOptions(ParticleColors.CURSED_ENERGY_COLOR, 1.0F, 0.5F, false, 3);

            level.sendParticles(particle, owner.getX() + (HelperMethods.RANDOM.nextGaussian() * 0.1D) + 0.15D,
                    owner.getY(),
                    owner.getZ() + HelperMethods.RANDOM.nextGaussian() * 0.1D,
                    0, 0.0D, 0.23D, 0.0D, -0.1D);
            level.sendParticles(particle, owner.getX() + (HelperMethods.RANDOM.nextGaussian() * 0.1D) - 0.15D,
                    owner.getY(),
                    owner.getZ() + HelperMethods.RANDOM.nextGaussian() * 0.1D,
                    0, 0.0D, 0.23D, 0.0D, -0.1D);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.NONE;
    }
}
