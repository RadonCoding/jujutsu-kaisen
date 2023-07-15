package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.util.HelperMethods;

public class DomainAmplification extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.distanceTo(target) < 3.0D && JJKAbilities.hasToggled(target, JJKAbilities.INFINITY.get());
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level instanceof ServerLevel level) {
            for (int i = 0; i < 4; i++) {
                level.sendParticles(JJKParticles.CURSED_ENERGY.get(),
                        owner.getX() + (HelperMethods.RANDOM.nextGaussian() * 0.1D),
                        owner.getY() + HelperMethods.RANDOM.nextDouble(owner.getBbHeight() * 0.75F),
                        owner.getZ() + (HelperMethods.RANDOM.nextGaussian() * 0.1D),
                        0, 0.0D, HelperMethods.RANDOM.nextDouble(), 0.0D, 2.5D);
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.0F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }
}
