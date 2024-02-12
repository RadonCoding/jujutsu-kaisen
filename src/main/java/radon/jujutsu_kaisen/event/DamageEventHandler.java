package radon.jujutsu_kaisen.event;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.misc.CursedEnergyFlow;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.DamageUtil;

public class DamageEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            // Your own cursed energy doesn't do as much damage
            if (source instanceof JJKDamageSources.JujutsuDamageSource) {
                if (source.getEntity() == victim) {
                    event.setAmount(event.getAmount() * 0.1F);
                }
            }

            // Perfect body generic melee increase
            if (source.getEntity() instanceof LivingEntity attacker) {
                IJujutsuCapability jujutsuCap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

if (jujutsuCap == null) return;

ISorcererData data = jujutsuCap.getSorcererData();

                if (data != null && data.hasTrait(Trait.PERFECT_BODY)) {
                    if (DamageUtil.isMelee(source)) {
                        event.setAmount(event.getAmount() * 2.0F);
                    }
                }
            }

            // Lessen damage for sorcerers
            if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
                IJujutsuCapability jujutsuCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

if (jujutsuCap == null) return;

ISorcererData data = jujutsuCap.getSorcererData();

                if (data != null) {
                    float armor = data.getExperience() * 0.002F;

                    if (data.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                        armor *= 10.0F;
                    }
                    float blocked = CombatRules.getDamageAfterAbsorb(event.getAmount(), armor, armor * 0.1F);
                    event.setAmount(blocked);
                }
            }

            // Lessen damage if using cursed energy flow
            CursedEnergyFlow.attack(event);
            CursedEnergyFlow.shield(event);
        }
    }
}
