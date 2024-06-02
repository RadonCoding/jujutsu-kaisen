package radon.jujutsu_kaisen.chant;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.*;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.chant.IChantData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.network.packet.s2c.*;
import radon.jujutsu_kaisen.util.DimensionUtil;

import java.util.*;
import java.util.List;

public class ServerChantHandler {
    private static final Map<UUID, Integer> TIMERS = new HashMap<>();
    private static final Map<UUID, List<String>> MESSAGES = new HashMap<>();
    private static final int CLEAR_INTERVAL = 10 * 20;

    public static List<String> getMessages(LivingEntity owner) {
        return MESSAGES.getOrDefault(owner.getUUID(), List.of());
    }

    public static void onChant(LivingEntity owner, String word) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IChantData chantData = cap.getChantData();
        
        Ability ability = chantData.getAbility(word);

        if (ability != null) {
            if (!MESSAGES.containsKey(owner.getUUID())) {
                MESSAGES.put(owner.getUUID(), new ArrayList<>());
            }

            List<String> chants = new ArrayList<>(chantData.getFirstChants(ability));

            List<String> latest = MESSAGES.get(owner.getUUID());

            int index = 0;

            Iterator<String> iter = chants.iterator();

            for (String chant : latest) {
                if (!iter.hasNext() || !chant.equals(iter.next())) break;

                index++;
            }

            if (index >= chants.size() || !chants.get(index).equals(word)) return;

            MESSAGES.get(owner.getUUID()).add(word);

            if (owner instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new AddChantS2CPacket(word));
            }
            TIMERS.put(owner.getUUID(), CLEAR_INTERVAL);

            if (owner instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.chant", JujutsuKaisen.MOD_ID),
                        ability.getName().copy(), ChantHandler.getOutput(owner, ability) * 100), false));
            }

            if (sorcererData.hasTrait(Trait.PERFECT_BODY)) {
                PacketDistributor.sendToAllPlayers(new SyncMouthS2CPacket(owner.getUUID()));
            }
        }
    }

    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onServerTickPre(ServerTickEvent.Pre event) {
            Iterator<Map.Entry<UUID, Integer>> iter = TIMERS.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<UUID, Integer> entry = iter.next();

                if (!MESSAGES.containsKey(entry.getKey())) {
                    iter.remove();
                    continue;
                }

                Entity owner = null;

                for (ServerLevel level : event.getServer().getAllLevels()) {
                    if ((owner = level.getEntity(entry.getKey())) != null) break;
                }

                if (owner == null) continue;

                IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) return;

                IAbilityData abilityData = cap.getAbilityData();
                IChantData chantData = cap.getChantData();

                Ability ability = chantData.getAbility(new LinkedHashSet<>(MESSAGES.get(entry.getKey())));

                if (abilityData.isChanneling(ability)) continue;

                int remaining = entry.getValue();

                if (remaining > 0) {
                    TIMERS.put(entry.getKey(), --remaining);
                } else {
                    MESSAGES.remove(entry.getKey());
                    iter.remove();

                    if (owner instanceof ServerPlayer player) {
                        PacketDistributor.sendToPlayer(player, new ClearChantsS2CPacket());
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Post event) {
            if (event.getEntity().level().isClientSide) return;

            if (ChantHandler.isChanted(event.getEntity(), event.getAbility())) {
                LivingEntity owner = event.getEntity();
                MESSAGES.remove(owner.getUUID());

                if (event.getEntity() instanceof ServerPlayer player) {
                    PacketDistributor.sendToPlayer(player, new ClearChantsS2CPacket());
                }
            }
        }

        @SubscribeEvent
        public static void onServerChat(ServerChatEvent event) {
            onChant(event.getPlayer(), event.getRawText().toLowerCase());
        }
    }
}
