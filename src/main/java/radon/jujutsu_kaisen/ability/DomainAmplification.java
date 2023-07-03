package radon.jujutsu_kaisen.ability;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.util.HelperMethods;

public class DomainAmplification extends Ability implements Ability.IToggled {
    @Override
    public ActivationType getActivationType() {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level instanceof ServerLevel level) {
            for (int i = 0; i < 4; i++) {
                AABB bounds = owner.getBoundingBox();
                double minY = bounds.minY;
                double maxY = bounds.maxY;

                double randomY = minY + (maxY - minY) * HelperMethods.RANDOM.nextDouble();

                level.sendParticles(JJKParticles.CURSED_ENERGY.get(),
                        owner.getX(), randomY, owner.getZ(), 0, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }
}
