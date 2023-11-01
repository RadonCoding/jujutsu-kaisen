package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.util.HelperMethods;

public class InstantSpiritBodyOfDistortedKilling extends Ability implements Ability.IToggled {
    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        int count = (int) (owner.getBbWidth() * owner.getBbHeight()) * 8;

        for (int i = 0; i < count; i++) {
            double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2) - owner.getLookAngle().scale(0.35D).x();
            double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
            double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2) - owner.getLookAngle().scale(0.35D).z();
            level.sendParticles(ParticleTypes.SMOKE, x, y, z, 0, 0.0D, HelperMethods.RANDOM.nextDouble() * 0.1D, 0.0D, 1.0D);
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
}
