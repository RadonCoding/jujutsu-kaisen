package radon.jujutsu_kaisen;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.effect.BlackFlashEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.*;

public class BlackFlashHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        private static final int CLEAR_INTERVAL = 5 * 20;

        private static final Map<UUID, Integer> TIMERS = new HashMap<>();
        private static final Map<UUID, Integer> COMBOS = new HashMap<>();

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            Iterator<Map.Entry<UUID, Integer>> iter = TIMERS.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<UUID, Integer> entry = iter.next();

                if (!COMBOS.containsKey(entry.getKey())) {
                    iter.remove();
                    continue;
                }

                int remaining = entry.getValue();

                if (remaining > 0) {
                    TIMERS.put(entry.getKey(), --remaining);
                } else {
                    COMBOS.remove(entry.getKey());
                    iter.remove();
                }
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            LivingEntity owner = event.getEntity();
            COMBOS.remove(owner.getUUID());
        }

        // Has to fire after CursedEnergyFlow::onLivingHurt
        // Has to fire after JJKEventHandler::onLivingHurt
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingHurt(LivingHurtEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (event.getAmount() / victim.getMaxHealth() >= 0.5F) {
                COMBOS.remove(victim.getUUID());
                TIMERS.remove(victim.getUUID());
            }

            if (!DamageUtil.isMelee(source)) return;

            if (attacker instanceof ISorcerer sorcerer && !sorcerer.hasArms()) return;

            IJujutsuCapability cap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData sorcererData = cap.getSorcererData();
            IAbilityData abilityData = cap.getAbilityData();

            if (SorcererUtil.getGrade(sorcererData.getExperience()).ordinal() < SorcererGrade.GRADE_1.ordinal() ||
                    (!(source instanceof JJKDamageSources.JujutsuDamageSource)
                            && !abilityData.hasToggled(JJKAbilities.CURSED_ENERGY_FLOW.get())
                            && !abilityData.hasToggled(JJKAbilities.BLUE_FISTS.get()))) return;

            int combo = COMBOS.getOrDefault(attacker.getUUID(), 0);
            COMBOS.put(attacker.getUUID(), ++combo);
            TIMERS.put(attacker.getUUID(), CLEAR_INTERVAL);

            if (HelperMethods.RANDOM.nextInt(Math.max(1, ConfigHolder.SERVER.blackFlashChance.get() / (sorcererData.isInZone() ? 2 : 1) - combo)) != 0) return;

            COMBOS.remove(attacker.getUUID());

            long lastBlackFlashTime = sorcererData.getLastBlackFlashTime();
            int seconds = (int) (attacker.level().getGameTime() - lastBlackFlashTime) / 20;

            if (lastBlackFlashTime != 0 && seconds <= 1) return;

            sorcererData.onBlackFlash();

            if (attacker instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(sorcererData.serializeNBT()), player);
            }

            event.setAmount((float) Math.pow(event.getAmount(), 2.5D));

            attacker.level().addFreshEntity(new BlackFlashEntity(attacker, victim));

            victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.MASTER, 2.0F, 0.8F + HelperMethods.RANDOM.nextFloat() * 0.2F);
            victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                    SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.MASTER, 1.0F, 0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F);
        }
    }
}
