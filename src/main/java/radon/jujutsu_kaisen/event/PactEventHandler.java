package radon.jujutsu_kaisen.event;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.binding_vow.JJKBindingVows;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.pact.JJKPacts;

public class PactEventHandler {
    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            // Check for BindingVow.RECOIL
            if (source.is(JJKDamageSources.JUJUTSU)) {
                IJujutsuCapability cap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) return;
                
                IContractData data = cap.getContractData();

                if (data.hasBindingVow(JJKBindingVows.RECOIL.get())) {
                    attacker.hurt(JJKDamageSources.self(victim), event.getAmount() * 0.25F);
                    event.setAmount(event.getAmount() * 1.25F);
                }
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            while (attacker instanceof TamableAnimal tamable && tamable.isTame()) {
                attacker = tamable.getOwner();

                if (attacker == null) return;
            }

            while (victim instanceof TamableAnimal tamable && tamable.isTame()) {
                victim = tamable.getOwner();

                if (victim == null) return;
            }

            // Check for Pact.INVULNERABILITY
            IJujutsuCapability victimCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (victimCap == null) return;

            IContractData victimData = victimCap.getContractData();
            
            IJujutsuCapability attackerCap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (attackerCap == null) return;

            IContractData attackerData = attackerCap.getContractData();

            if (victimData.hasPact(attacker.getUUID(), JJKPacts.INVULNERABILITY.get()) && attackerData.hasPact(victim.getUUID(), JJKPacts.INVULNERABILITY.get())) {
                event.setCanceled(true);
            }
        }
    }
}