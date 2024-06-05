package radon.jujutsu_kaisen.client.visual.visual;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.util.HelperMethods;

public class IdleTransfigurationVisual implements IVisual {
    private static final float RADIUS = 1.5F;
    private static final float PARTICLE_SIZE = RADIUS * 0.2F;

    @Override
    public boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData client) {
        return client.toggled.contains(JJKAbilities.IDLE_TRANSFIGURATION.get());
    }

    @Override
    public void tick(LivingEntity entity, ClientVisualHandler.ClientData client) {
        Vec3 right = BlueFistsVisual.getArmPos(entity, HumanoidArm.RIGHT).add(0.0D, PARTICLE_SIZE / 2.0F, 0.0D);
        spawn(entity.level(), right, ParticleColors.getCursedEnergyColor(entity));

        Vec3 left = BlueFistsVisual.getArmPos(entity, HumanoidArm.LEFT).add(0.0D, PARTICLE_SIZE / 2.0F, 0.0D);
        spawn(entity.level(), left, ParticleColors.getCursedEnergyColor(entity));
    }

    private static void spawn(Level level, Vec3 pos, Vector3f color) {
        int count = (int) (RADIUS * Math.PI * 2);

        for (int i = 0; i < count; i++) {
            double theta = HelperMethods.RANDOM.nextDouble() * Math.PI * 2.0D;
            double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;

            double xOffset = RADIUS * Math.sin(phi) * Math.cos(theta);
            double yOffset = RADIUS * Math.sin(phi) * Math.sin(theta);
            double zOffset = RADIUS * Math.cos(phi);

            double x = pos.x + xOffset * (RADIUS * 0.1F);
            double y = pos.y + yOffset * (RADIUS * 0.1F);
            double z = pos.z + zOffset * (RADIUS * 0.1F);

            level.addParticle(new TravelParticle.Options(pos.toVector3f(), color, RADIUS * 0.15F, 0.2F, true, 20),
                    x, y, z, 0.0D, 1.0D, 0.0D);
        }
    }
}
