package radon.jujutsu_kaisen;

import com.google.common.collect.Lists;
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

import java.util.*;

public class ChantHandler {
    private static final Map<UUID, Integer> timers = new HashMap<>();
    private static final Map<UUID, List<String>> messages = new HashMap<>();
    private static final int CLEAR_INTERVAL = 10 * 20;

    public static float getChant(LivingEntity owner, Ability ability) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        Set<String> chants = cap.getChants(ability);

        if (chants.isEmpty()) return 1.0F;

        List<String> latest = messages.get(owner.getUUID());

        if (latest == null || latest.isEmpty()) return 1.0F;

        ListIterator<String> iter = latest.listIterator(latest.size());

        int count = 0;
        int length = 0;

        for (String chant : Lists.reverse(new ArrayList<>(chants))) {
            if (!iter.hasPrevious()) break;

            String msg = iter.previous();

            if (msg.equals(chant)) {
                count++;
                length += chant.length();
            }
        }
        return 1.0F + (count * 0.1F) + (length * 0.01F);
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
        public static void onAbilityTrigger(AbilityTriggerEvent event) {
            LivingEntity owner = event.getEntity();
            messages.remove(owner.getUUID());
        }

        @SubscribeEvent
        public static void onServerChat(ServerChatEvent event) {
            ServerPlayer owner = event.getPlayer();

            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            String msg = event.getRawText();

            if (cap.hasChant(msg)) {
                if (!messages.containsKey(owner.getUUID())) {
                    messages.put(owner.getUUID(), new ArrayList<>());
                }
                messages.get(owner.getUUID()).add(msg);
                timers.put(owner.getUUID(), CLEAR_INTERVAL);
            }
        }
    }
}
