package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;

public enum CursedEnergyNature {
    BASIC,
    ROUGH,
    LIGHTNING,
    DIVERGENT;

    public Component getName() {
        return Component.translatable(String.format("cursed_energy_nature.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }
}
