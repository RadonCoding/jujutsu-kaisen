package radon.jujutsu_kaisen.chant;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.*;

import java.util.*;

public class ServerChantHandler {
    private static final Map<UUID, Integer> timers = new HashMap<>();
    private static final Map<UUID, List<String>> messages = new HashMap<>();
    private static final int CLEAR_INTERVAL = 10 * 20;

    public static List<String> getMessages(LivingEntity owner) {
        return messages.getOrDefault(owner.getUUID(), List.of());
    }

    public static void onChant(LivingEntity owner, String word) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        Ability ability = cap.getAbility(word);

        if (ability != null) {
            if (!messages.containsKey(owner.getUUID())) {
                messages.put(owner.getUUID(), new ArrayList<>());
            }

            List<String> chants = new ArrayList<>(cap.getFirstChants(ability));

            List<String> latest = messages.get(owner.getUUID());

            int index = 0;

            Iterator<String> iter = chants.iterator();

            for (String chant : latest) {
                if (!iter.hasNext() || !chant.equals(iter.next())) break;

                index++;
            }

            if (index >= chants.size() || !chants.get(index).equals(word)) return;

            messages.get(owner.getUUID()).add(word);

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new AddChantS2CPacket(word), player);
            }
            timers.put(owner.getUUID(), CLEAR_INTERVAL);

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.chant", JujutsuKaisen.MOD_ID),
                        ability.getName().copy(), ChantHandler.getOutput(owner, ability) * 100), false), player);
            }

            if (cap.hasTrait(Trait.PERFECT_BODY)) {
                PacketHandler.broadcast(new SyncMouthS2CPacket(owner.getUUID()));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ChantHandlerForgeEvents {
        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            Iterator<Map.Entry<UUID, Integer>> iter = timers.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<UUID, Integer> entry = iter.next();

                if (!messages.containsKey(entry.getKey())) {
                    iter.remove();
                    continue;
                }

                Entity owner = null;

                for (ServerLevel level : event.getServer().getAllLevels()) {
                    if ((owner = level.getEntity(entry.getKey())) != null) break;
                }

                if (owner == null) continue;

                ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                Ability ability = cap.getAbility(new LinkedHashSet<>(messages.get(entry.getKey())));

                if (cap.isChanneling(ability)) continue;

                int remaining = entry.getValue();

                if (remaining > 0) {
                    timers.put(entry.getKey(), --remaining);
                } else {
                    messages.remove(entry.getKey());
                    iter.remove();

                    if (owner instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new ClearChantsC2SPacket(), player);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Post event) {
            if (event.getEntity().level().isClientSide) return;

            if (ChantHandler.isChanted(event.getEntity(), event.getAbility())) {
                LivingEntity owner = event.getEntity();
                messages.remove(owner.getUUID());

                if (event.getEntity() instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new ClearChantsC2SPacket(), player);
                }
            }
        }

        @SubscribeEvent
        public static void onServerChat(ServerChatEvent event) {
            onChant(event.getPlayer(), event.getRawText().toLowerCase());
        }
    }
}
