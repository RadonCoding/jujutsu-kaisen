package radon.jujutsu_kaisen.event;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncAbilityDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.HashSet;
import java.util.Set;

public class AdaptationEventHandler {
    private static final int DISRUPTION_DURATION = 20;

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHitByDomain(LivingHitByDomainEvent event) {
            LivingEntity victim = event.getEntity();

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData abilityData = cap.getAbilityData();
            ITenShadowsData tenShadowsData = cap.getTenShadowsData();

            if (!abilityData.hasToggled(JJKAbilities.WHEEL.get())) return;

            tenShadowsData.tryAdapt(event.getAbility());
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            IJujutsuCapability victimCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (victimCap == null) return;

            IAbilityData victimAbilityData = victimCap.getAbilityData();
            ITenShadowsData victimTenShadowsData = victimCap.getTenShadowsData();

            if (victimAbilityData.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get()) || !victimAbilityData.hasToggled(JJKAbilities.WHEEL.get())) return;

            if (source.getEntity() instanceof LivingEntity attacker) {
                IJujutsuCapability attackerCap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (attackerCap != null) {
                    IAbilityData attackerData = attackerCap.getAbilityData();

                    if (attackerData.hasToggled(JJKAbilities.INFINITY.get())) {
                        victimTenShadowsData.tryAdapt(JJKAbilities.INFINITY.get());
                    }
                    if (attackerData.hasToggled(JJKAbilities.SOUL_REINFORCEMENT.get())) {
                        victimTenShadowsData.tryAdapt(JJKAbilities.SOUL_REINFORCEMENT.get());
                    }
                }
            }

            // Initiate / continue the adaptation process
            victimTenShadowsData.tryAdapt(source);

            if (!(victim instanceof MahoragaEntity)) return;

            float process = (1.0F - victimTenShadowsData.getAdaptationProgress(source));

            switch (victimTenShadowsData.getAdaptationType(source)) {
                case DAMAGE -> {
                    if (victimTenShadowsData.isAdaptedTo(source)) {
                        victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.MASTER, 1.0F, 1.0F);
                    }
                    event.setAmount(event.getAmount() * process);
                }
                case COUNTER -> {
                    if (HelperMethods.RANDOM.nextInt(Math.max(2, Math.round(20 * process))) == 0) {
                        Entity attacker = source.getEntity();

                        if (attacker != null) {
                            victim.lookAt(EntityAnchorArgument.Anchor.EYES, attacker.position());
                            victim.swing(InteractionHand.MAIN_HAND);

                            victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.MASTER, 1.0F, 1.0F);

                            event.setCanceled(true);
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            IJujutsuCapability victimCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (victimCap == null) return;

            IAbilityData victimData = victimCap.getAbilityData();

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (!(attacker instanceof MahoragaEntity)) return;

            IJujutsuCapability attackerCap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (attackerCap == null) return;

            ITenShadowsData attackerTenShadowsData = attackerCap.getTenShadowsData();

            Set<Ability> toggled = new HashSet<>(victimData.getToggled());

            for (Ability ability : toggled) {
                if (ability instanceof DomainExpansion.IOpenDomain) continue;
                if (!attackerTenShadowsData.isAdaptedTo(ability)) continue;
                victimData.disrupt(ability, DISRUPTION_DURATION * attackerTenShadowsData.getAdaptation(ability));
            }

            Ability channeled = victimData.getChanneled();

            if (channeled != null) {
                if (attackerTenShadowsData.isAdaptedTo(channeled)) {
                    victimData.disrupt(channeled, DISRUPTION_DURATION * attackerTenShadowsData.getAdaptation(channeled));
                }
            }

            if (victim instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncAbilityDataS2CPacket(victimData.serializeNBT()), player);
            }
        }
    }
}
