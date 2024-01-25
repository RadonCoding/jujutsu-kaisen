package radon.jujutsu_kaisen.event;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Adaptation;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AdaptationEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class AdaptationEventHandlerForgeEvents {
        @SubscribeEvent
        public static void onLivingHitByDomain(LivingHitByDomainEvent event) {
            LivingEntity victim = event.getEntity();

            if (!JJKAbilities.hasToggled(victim, JJKAbilities.WHEEL.get())) return;

            ISorcererData cap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (!cap.isAdaptedTo(event.getAbility())) {
                cap.tryAdapt(event.getAbility());
            }
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData cap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get()) || !cap.hasToggled(JJKAbilities.WHEEL.get())) return;

            // Initiate / continue the adaptation process
            if (!cap.isAdaptedTo(source)) cap.tryAdapt(source);

            if (victim instanceof MahoragaEntity) {
                if (cap.isAdaptedTo(source)) {
                    victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.MASTER, 1.0F, 1.0F);
                }

                float process = (1.0F - cap.getAdaptationProgress(source));

                switch (cap.getAdaptationType(source)) {
                    case DAMAGE -> event.setAmount(event.getAmount() * process);
                    case COUNTER -> {
                        if (HelperMethods.RANDOM.nextInt(Math.max(1, Math.round(20 * process))) == 0) {
                            Entity attacker = source.getEntity();

                            if (attacker != null) {
                                victim.lookAt(EntityAnchorArgument.Anchor.EYES, attacker.position());

                                victim.swing(InteractionHand.MAIN_HAND);

                                if (victim.doHurtTarget(attacker)) {
                                    victim.invulnerableTime = 0;
                                }
                            }
                        }
                    }
                }
            }
        }

        @SubscribeEvent(receiveCanceled = true)
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData victimCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (!attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (!attackerCap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get()) && attackerCap.hasToggled(JJKAbilities.WHEEL.get())) {
                if (victimCap.hasToggled(JJKAbilities.INFINITY.get())) {
                    attackerCap.tryAdapt(JJKAbilities.INFINITY.get());
                }
                if (victimCap.hasToggled(JJKAbilities.SOUL_REINFORCEMENT.get())) {
                    attackerCap.tryAdapt(JJKAbilities.SOUL_REINFORCEMENT.get());
                }
            }

            if (!event.isCanceled()) {
                if (attacker instanceof MahoragaEntity) {
                    Set<Ability> toggled = new HashSet<>(victimCap.getToggled());

                    for (Ability ability : toggled) {
                        if (!attackerCap.isAdaptedTo(ability)) continue;
                        victimCap.toggle(ability);
                    }

                    if (victim instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(victimCap.serializeNBT()), player);
                    }
                }
            }
        }
    }
}
