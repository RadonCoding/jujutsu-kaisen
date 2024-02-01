package radon.jujutsu_kaisen.client.visual.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.idle_transfiguration.IdleTransfiguration;
import radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.util.HelperMethods;

public class TransfiguredSoulVisual implements IVisual {
    @Override
    public boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData data) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return false;

        if (!JJKCursedTechniques.getTechniques(mc.player).contains(JJKCursedTechniques.IDLE_TRANSFIGURATION.get())) return false;

        return entity.hasEffect(JJKEffects.TRANSFIGURED_SOUL.get());
    }

    @Override
    public void tick(LivingEntity entity, ClientVisualHandler.ClientData data) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return;

        MobEffectInstance instance = entity.getEffect(JJKEffects.TRANSFIGURED_SOUL.get());

        if (instance == null) return;

        int amplifier = instance.getAmplifier();

        float attackerStrength = IdleTransfiguration.calculateStrength(mc.player);
        float victimStrength = IdleTransfiguration.calculateStrength(entity);

        int required = Math.round((victimStrength / attackerStrength) * 2);

        if (amplifier >= required) {
            int count = Math.round(entity.getBbWidth() + entity.getBbHeight());

            for (int i = 0; i < count; i++) {
                double x = entity.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2);
                double y = entity.getY() + HelperMethods.RANDOM.nextDouble() * entity.getBbHeight();
                double z = entity.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (entity.getBbWidth() * 2);
                mc.level.addParticle(ParticleTypes.SOUL, x, y, z, 0.0D, HelperMethods.RANDOM.nextDouble() * 0.1D, 0.0D);
            }
        }
    }
}
