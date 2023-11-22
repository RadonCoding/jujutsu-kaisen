package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;

public enum Trait {
    SIX_EYES,
    HEAVENLY_RESTRICTION,
    REVERSE_CURSED_TECHNIQUE,
    VESSEL(JJKAbilities.SWITCH.get());

    private final Ability[] abilities;

    Trait(Ability... abilities) {
        this.abilities = abilities;
    }

    public Ability[] getAbilities() {
        return this.abilities;
    }

    public Component getName() {
        return Component.translatable(String.format("trait.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }
}
