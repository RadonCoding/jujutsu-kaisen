package radon.jujutsu_kaisen.data.sorcerer;


import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.JujutsuKaisen;

public enum Trait {
    SIX_EYES,
    HEAVENLY_RESTRICTION_BODY,
    HEAVENLY_RESTRICTION_SORCERY,
    VESSEL,
    PERFECT_BODY;

    public Component getName() {
        return Component.translatable(String.format("trait.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }
}
