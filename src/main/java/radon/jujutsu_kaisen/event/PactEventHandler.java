package radon.jujutsu_kaisen.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.BindingVow;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;
import radon.jujutsu_kaisen.damage.JJKDamageSources;

public class PactEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class PactEventHandlerForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            // Check for BindingVow.RECOIL
            if (source.is(JJKDamageSources.JUJUTSU)) {
                if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData cap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    if (cap.hasBindingVow(BindingVow.RECOIL)) {
                        attacker.hurt(JJKDamageSources.self(victim), event.getAmount() * 0.25F);
                        event.setAmount(event.getAmount() * 1.25F);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            // Check for Pact.INVULNERABILITY
            if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent() && attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                ISorcererData victimCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                if (victimCap.hasPact(attacker.getUUID(), Pact.INVULNERABILITY) && attackerCap.hasPact(victim.getUUID(), Pact.INVULNERABILITY)) {
                    event.setCanceled(true);
                }
            }
        }
    }
}