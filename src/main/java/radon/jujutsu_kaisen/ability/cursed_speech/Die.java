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

    private static float calculateDamage(DamageSource source, LivingEntity target) {
        float damage = target.getMaxHealth();
        float armor = (float) target.getArmorValue();
        float toughness = (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        float f = 2.0F + toughness / 4.0F;
        float f1 = Mth.clamp(armor - damage / f, armor * 0.2F, 20.0F);
        damage /= 1.0F - f1 / 25.0F;

        MobEffectInstance instance = target.getEffect(MobEffects.DAMAGE_RESISTANCE);

        if (instance != null) {
            int resistance = instance.getAmplifier();
            int i = (resistance + 1) * 5;
            int j = 25 - i;

            if (j == 0) {
                return damage;
            } else {
                float x = 25.0F / (float) j;
                damage = damage * x;
            }
        }

        int k = EnchantmentHelper.getDamageProtection(target.getArmorSlots(), source);

        if (k > 0) {
            float f2 = Mth.clamp(k, 0.0F, 20.0F);
            damage /= 1.0F - f2 / 25.0F;
        }
        return damage;
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        if (owner.level().isClientSide) return;

        CursedSpeechUtil.attack(owner, entity -> {
            if (!(entity instanceof LivingEntity living)) return;

            if (owner.getMaxHealth() / living.getMaxHealth() >= 4) {
                DamageSource source = JJKDamageSources.jujutsuAttack(owner, this);
                living.hurt(source, calculateDamage(source, living));
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
