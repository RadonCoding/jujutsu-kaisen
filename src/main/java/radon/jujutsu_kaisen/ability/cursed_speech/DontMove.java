package radon.jujutsu_kaisen.ability.cursed_speech;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.cursed_speech.base.CursedSpeech;
import radon.jujutsu_kaisen.ability.cursed_speech.util.CursedSpeechUtil;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.util.HelperMethods;

public class DontMove extends CursedSpeech {
    private static final int DURATION = 20;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || !owner.hasLineOfSight(target)) return false;
        return CursedSpeechUtil.getTargets(owner).contains(target) && HelperMethods.RANDOM.nextInt(5) == 0;
    }

    @Override
    public Ability.ActivationType getActivationType(LivingEntity owner) {
        return Ability.ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        if (owner.level().isClientSide) return;

        CursedSpeechUtil.attack(owner, entity -> {
            if (!(entity instanceof LivingEntity living)) return;

            living.addEffect(new MobEffectInstance(JJKEffects.STUN.get(), Math.round(DURATION * this.getOutput(owner)), 1, false, false, false));

            if (living instanceof Player player) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.dont_move", JujutsuKaisen.MOD_ID), owner.getName()));
            }
        });
    }

    @Override
    public int getThroatDamage() {
        return 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.CURSED_SPEECH;
    }
}
