package radon.jujutsu_kaisen.client.visual.visual;


import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.util.HelperMethods;

public class AzureGlideVisual implements IVisual {
    @Override
    public boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData client) {
        return client.channeled == JJKAbilities.AZURE_GLIDE.get();
    }

    @Override
    public void tick(LivingEntity entity, ClientVisualHandler.ClientData client) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) return;

        Vec3 center = entity.position().add(0.0D, entity.getBbHeight() / 2, 0.0D);

        for (int i = 0; i < 12; i++) {
            double theta = HelperMethods.RANDOM.nextDouble() * Math.PI * 2;
            double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;

            Vec3 direction = new Vec3(Math.sin(phi) * Math.cos(theta), Math.sin(phi) * Math.sin(theta), Math.cos(phi));
            Vec3 start = center.add(direction.multiply(entity.getBbWidth() / 2.0F, entity.getBbHeight() / 2, entity.getBbWidth() / 2.0F));
            Vec3 end = start.add(direction.scale(0.1D));

            entity.level().addParticle(new TravelParticle.Options(end, ParticleColors.LIGHT_BLUE, entity.getBbWidth() * 0.5F, 0.25F, true, true, 20),
                    start.x, start.y, start.z, 0.0D, 0.0D, 0.0D);
        }
    }
}
