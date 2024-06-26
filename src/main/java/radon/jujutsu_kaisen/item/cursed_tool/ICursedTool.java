package radon.jujutsu_kaisen.item.cursed_tool;


import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ICursedTool {
    // LivingAttackEvent
    default boolean doPreHurtEffects(ItemStack stack, DamageSource source, LivingEntity attacker, LivingEntity victim, float amount) {
        return false;
    }

    // LivingHurtEvent
    default float doHurtEffects(ItemStack stack, DamageSource source, LivingEntity attacker, LivingEntity victim, float amount) {
        return amount;
    }

    // LivingDamageEvent
    default void doPostHurtEffects(ItemStack stack, DamageSource source, LivingEntity attacker, LivingEntity victim) {
    }
}
