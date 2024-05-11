package radon.jujutsu_kaisen.data.mission;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;


import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;

public enum MissionType {
    EXORCISE_CURSES;

    public Component getTitle() {
        return Component.translatable(String.format("mission_type.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }

    public Component getDescription() {
        return Component.translatable(String.format("mission_type.%s.%s.description", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }
}
