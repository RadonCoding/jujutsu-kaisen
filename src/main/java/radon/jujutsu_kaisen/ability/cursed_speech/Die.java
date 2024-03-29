package radon.jujutsu_kaisen.ability.cursed_speech;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.cursed_speech.base.CursedSpeech;
import radon.jujutsu_kaisen.ability.cursed_speech.util.CursedSpeechUtil;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Die extends CursedSpeech {
    private static final float DAMAGE = 25.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;
        return CursedSpeechUtil.getTargets(owner).contains(target) && (owner.getHealth() / owner.getMaxHealth() <= 0.25F || HelperMethods.RANDOM.nextInt(10) == 0 && owner.hasLineOfSight(target));
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        if (owner.level().isClientSide) return;

        CursedSpeechUtil.attack(owner, entity -> {
            if (!(entity instanceof LivingEntity living)) return;

            if (owner.getMaxHealth() / living.getMaxHealth() >= 4) {
                DamageSource source = JJKDamageSources.jujutsuAttack(owner, this);
                living.hurt(source, EntityUtil.calculateDamage(source, living));
            } else {
                living.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getOutput(owner));
            }

            if (living instanceof Player player) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.die", JujutsuKaisen.MOD_ID), owner.getName()));
            }
        });
    }

    @Override
    public int getThroatDamage() {
        return 5 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 500.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.CURSED_SPEECH;
    }
}
