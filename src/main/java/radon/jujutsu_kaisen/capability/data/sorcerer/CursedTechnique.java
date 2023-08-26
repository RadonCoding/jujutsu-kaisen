package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;

public enum CursedTechnique {
    CURSE_MANIPULATION(null),
    LIMITLESS(JJKAbilities.UNLIMITED_VOID.get(), JJKAbilities.INFINITY.get(), JJKAbilities.RED.get(), JJKAbilities.MAXIMUM_RED.get(), JJKAbilities.BLUE.get(),
            JJKAbilities.MAXIMUM_BLUE.get(), JJKAbilities.HOLLOW_PURPLE.get(), JJKAbilities.MAXIMUM_HOLLOW_PURPLE.get(), JJKAbilities.TELEPORT.get()),
    DISMANTLE_AND_CLEAVE(JJKAbilities.MALEVOLENT_SHRINE.get(), JJKAbilities.DISMANTLE.get(), JJKAbilities.CLEAVE.get(), JJKAbilities.FIRE_ARROW.get()),
    CURSED_SPEECH(null),
    RIKA(null, JJKAbilities.RIKA.get(), JJKAbilities.COPY.get()),
    DISASTER_FLAMES(JJKAbilities.COFFIN_OF_IRON_MOUNTAIN.get(), JJKAbilities.EMBER_INSECTS.get(), JJKAbilities.VOLCANO.get(),
            JJKAbilities.MAXIMUM_METEOR.get(), JJKAbilities.DISASTER_FLAMES.get()),
    TEN_SHADOWS(JJKAbilities.CHIMERA_SHADOW_GARDEN.get(),
            JJKAbilities.SWITCH_MODE.get(),
            JJKAbilities.RELEASE.get(),

            JJKAbilities.NUE_LIGHTNING.get(),

            JJKAbilities.DIVINE_DOGS.get(), JJKAbilities.DIVINE_DOG_TOTALITY.get(), JJKAbilities.TOAD.get(),
            JJKAbilities.RABBIT_ESCAPE.get(), JJKAbilities.NUE.get(), JJKAbilities.GREAT_SERPENT.get(), JJKAbilities.MAHORAGA.get()),
    DIVERGENT_FIST(null, JJKAbilities.DIVERGENT_FIST.get());

    private final @Nullable Ability domain;
    private final Ability[] abilities;

    CursedTechnique(@Nullable Ability domain, Ability... abilities) {
        this.domain = domain;
        this.abilities = abilities;
    }

    public @Nullable Ability getDomain() {
        return this.domain;
    }

    public Ability[] getAbilities() {
        return this.abilities;
    }

    public Component getName() {
        return Component.translatable(String.format("cursed_technique.%s.%s", JujutsuKaisen.MOD_ID, this.name().toLowerCase()));
    }
}
