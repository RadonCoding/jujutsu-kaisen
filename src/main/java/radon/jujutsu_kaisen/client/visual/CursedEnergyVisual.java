package radon.jujutsu_kaisen.client.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ClientConfig;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class CursedEnergyVisual {
    public static void tick(LivingEntity entity) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return;

        if (ConfigHolder.CLIENT.visibleCursedEnergy.get()) {
            ClientVisualHandler.ClientData data = ClientVisualHandler.get(entity);

            if (data == null) return;

            if (data.channeled != JJKAbilities.CURSED_ENERGY_SHIELD.get() && !JJKAbilities.hasTrait(mc.player, Trait.SIX_EYES)) return;

            float scale = data.channeled == JJKAbilities.CURSED_ENERGY_SHIELD.get() ? 1.5F : 1.0F;

            if (data.toggled.contains(JJKAbilities.CURSED_ENERGY_FLOW.get())) {
                for (int i = 0; i < 12 * scale; i++) {
                    double x = entity.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 1.5F * scale) - entity.getLookAngle().scale(0.35D).x;
                    double y = entity.getY() + HelperMethods.RANDOM.nextDouble() * entity.getBbHeight();
                    double z = entity.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 1.5F * scale) - entity.getLookAngle().scale(0.35D).z;
                    double speed = (entity.getBbHeight() * 0.3F) * HelperMethods.RANDOM.nextDouble();
                    mc.level.addParticle(new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.getCursedEnergyColor(entity), entity.getBbWidth() * 0.5F * scale,
                            0.2F, 6), x, y, z, 00.0D, speed * scale, 0.0D);
                }
            }
        }
    }
}
