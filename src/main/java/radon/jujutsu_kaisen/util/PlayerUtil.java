package radon.jujutsu_kaisen.util;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class PlayerUtil {
    public static void giveAdvancement(ServerPlayer player, String name) {
        MinecraftServer server = player.getServer();
        assert server != null;
        AdvancementHolder advancement = server.getAdvancements().get(new ResourceLocation(JujutsuKaisen.MOD_ID,
                String.format("%s/%s", JujutsuKaisen.MOD_ID, name)));

        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);

            if (!progress.isDone()) {
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criterion);
                }
            }
        }
    }

    public static void removeAdvancement(ServerPlayer player, String name) {
        MinecraftServer server = player.getServer();
        assert server != null;
        AdvancementHolder advancement = server.getAdvancements().get(new ResourceLocation(JujutsuKaisen.MOD_ID,
                String.format("%s/%s", JujutsuKaisen.MOD_ID, name)));

        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);

            if (progress.isDone()) {
                for (String criterion : progress.getCompletedCriteria()) {
                    player.getAdvancements().revoke(advancement, criterion);
                }
            }
        }
    }
}
