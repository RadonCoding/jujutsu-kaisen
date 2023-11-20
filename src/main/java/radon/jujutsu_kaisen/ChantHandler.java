package radon.jujutsu_kaisen;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;

import java.util.*;

public class ChantHandler {
    private static final Map<UUID, Integer> timers = new HashMap<>();
    private static final Map<UUID, List<String>> messages = new HashMap<>();
    private static final int CLEAR_INTERVAL = 10 * 20;

    public static boolean isChanted(LivingEntity owner, Ability ability) {
        return getChant(owner, ability) > 1.0F;
    }

    public static float getChant(LivingEntity owner, Ability ability) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        Set<String> chants = cap.getChants(ability);

        if (chants.isEmpty()) return 1.0F;

        List<String> latest = messages.get(owner.getUUID());

        if (latest == null || latest.isEmpty()) return 1.0F;

        int count = 0;
        int length = 0;

        Iterator<String> iter = chants.iterator();

        for (String chant : latest) {
            if (!iter.hasNext() || !chant.equals(iter.next())) break;

            count++;
            length += chant.length();
        }
        float countFactor = (float) count / ConfigHolder.SERVER.maximumChantCount.get();
        float lengthFactor = (float) length / (ConfigHolder.SERVER.maximumChantCount.get() * ConfigHolder.SERVER.maximumChantLength.get());
        return 1.0F + 0.75F * (0.6F * countFactor + 0.4F * lengthFactor);
    }

    public static void onChant(LivingEntity owner, String word) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        Ability ability = cap.getAbility(word);

        if (ability != null) {
            if (!messages.containsKey(owner.getUUID())) {
                messages.put(owner.getUUID(), new ArrayList<>());
            }

            List<String> chants = new ArrayList<>(cap.getChants(ability));

            List<String> latest = messages.get(owner.getUUID());

            int index = 0;

            Iterator<String> iter = chants.iterator();

            for (String chant : latest) {
                if (!iter.hasNext() || !chant.equals(iter.next())) break;

                index++;
            }

            if (index >= chants.size() || !chants.get(index).equals(word)) return;

            messages.get(owner.getUUID()).add(word);
            timers.put(owner.getUUID(), CLEAR_INTERVAL);

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.chant", JujutsuKaisen.MOD_ID),
                        Math.round(getChant(owner, ability) * 100)), false), player);
            }

            int delta = messages.get(owner.getUUID()).size() - 5;

            if (delta > 0) {
                messages.put(owner.getUUID(), messages.get(owner.getUUID()).subList(delta, 5));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            Iterator<Map.Entry<UUID, Integer>> iter = timers.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<UUID, Integer> entry = iter.next();

                int remaining = entry.getValue();

                if (remaining > 0) {
                    timers.put(entry.getKey(), --remaining);
                } else {
                    messages.remove(entry.getKey());
                    iter.remove();
                }
            }
        }

        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Post event) {
            if (event.getEntity().level().isClientSide) return;

            if (isChanted(event.getEntity(), event.getAbility())) {
                LivingEntity owner = event.getEntity();
                messages.remove(owner.getUUID());
            }
        }

        @SubscribeEvent
        public static void onServerChat(ServerChatEvent event) {
            onChant(event.getPlayer(), event.getRawText().toLowerCase());
        }
    }
}
