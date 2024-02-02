package radon.jujutsu_kaisen.client.visual.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.HelperMethods;

public class CursedEnergyVisual implements IVisual {
    @Override
    public boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData data) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return false;

        return ConfigHolder.CLIENT.visibleCursedEnergy.get() && data.toggled.contains(JJKAbilities.CURSED_ENERGY_FLOW.get()) &&
                (data.channeled == JJKAbilities.CURSED_ENERGY_SHIELD.get() || (JJKAbilities.hasTrait(mc.player, Trait.SIX_EYES) && !mc.player.getItemBySlot(EquipmentSlot.HEAD).is(JJKItems.BLINDFOLD.get())));
    }

    @Override
    public void tick(LivingEntity entity, ClientVisualHandler.ClientData data) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) return;

        float scale = data.channeled == JJKAbilities.CURSED_ENERGY_SHIELD.get() ? 1.5F : 1.0F;

        for (int i = 0; i < 12 * scale; i++) {
            double x = entity.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 1.5F * scale) - entity.getLookAngle().scale(0.35D).x;
            double y = entity.getY() + HelperMethods.RANDOM.nextDouble() * entity.getBbHeight();
            double z = entity.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 1.5F * scale) - entity.getLookAngle().scale(0.35D).z;
            double speed = (entity.getBbHeight() * 0.3F) * HelperMethods.RANDOM.nextDouble();
            mc.level.addParticle(new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.getCursedEnergyColor(entity), entity.getBbWidth() * 0.5F * scale,
                    0.2F, 6), x, y, z, 0.0D, speed * scale, 0.0D);
        }
    }
}
