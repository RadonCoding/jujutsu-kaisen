package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;

public enum CursedTechnique {
    CURSE_MANIPULATION(null, JJKAbilities.ABSORB_CURSE.get(), JJKAbilities.RELEASE_CURSE.get(), JJKAbilities.RELEASE_CURSES.get(),
            JJKAbilities.MAXIMUM_UZUMAKI.get(),
            JJKAbilities.MINI_UZUMAKI.get()),
    LIMITLESS(JJKAbilities.UNLIMITED_VOID.get(), JJKAbilities.INFINITY.get(), JJKAbilities.RED.get(), JJKAbilities.MAXIMUM_RED.get(), JJKAbilities.BLUE.get(),
            JJKAbilities.MAXIMUM_BLUE.get(), JJKAbilities.BLUE_FISTS.get(), JJKAbilities.HOLLOW_PURPLE.get(), JJKAbilities.MAXIMUM_HOLLOW_PURPLE.get(), JJKAbilities.TELEPORT.get()),
    DISMANTLE_AND_CLEAVE(JJKAbilities.MALEVOLENT_SHRINE.get(), JJKAbilities.DISMANTLE.get(), JJKAbilities.CLEAVE.get(), JJKAbilities.SPIDERWEB.get(), JJKAbilities.DISMANTLE_BARRAGE.get(), JJKAbilities.FIRE_ARROW.get()),
    CURSED_SPEECH(null, JJKAbilities.DONT_MOVE.get(), JJKAbilities.GET_CRUSHED.get(), JJKAbilities.BLAST_AWAY.get(), JJKAbilities.EXPLODE.get(), JJKAbilities.DIE.get()),
    RIKA(null, JJKAbilities.RIKA.get(), JJKAbilities.COPY.get(), JJKAbilities.COMMAND_PURE_LOVE.get()),
    DISASTER_FLAMES(JJKAbilities.COFFIN_OF_THE_IRON_MOUNTAIN.get(), JJKAbilities.EMBER_INSECTS.get(), JJKAbilities.VOLCANO.get(),
            JJKAbilities.MAXIMUM_METEOR.get(), JJKAbilities.DISASTER_FLAMES.get(), JJKAbilities.FLAMETHROWER.get(), JJKAbilities.FIREBALL.get()),
    DISASTER_TIDES(JJKAbilities.HORIZON_OF_THE_CAPTIVATING_SKANDHA.get(), JJKAbilities.DISASTER_TIDES.get(), JJKAbilities.WATER_SHIELD.get(), JJKAbilities.FISH_SHIKIGAMI.get(), JJKAbilities.WATER_TORRENT.get()),
    DISASTER_PLANTS(null, JJKAbilities.FOREST_PLATFORM.get(), JJKAbilities.FOREST_SPIKES.get(), JJKAbilities.WOOD_SHIELD.get()),
    TEN_SHADOWS(JJKAbilities.CHIMERA_SHADOW_GARDEN.get(),
            JJKAbilities.SWITCH_MODE.get(),
            JJKAbilities.RELEASE_SHIKIGAMI.get(),
            JJKAbilities.SHADOW_STORAGE.get(),

            JJKAbilities.NUE_LIGHTNING.get(), JJKAbilities.PIERCING_WATER.get(), JJKAbilities.WHEEL.get(),

            JJKAbilities.DIVINE_DOGS.get(), JJKAbilities.DIVINE_DOG_TOTALITY.get(), JJKAbilities.TOAD.get(), JJKAbilities.TOAD_TOTALITY.get(),
            JJKAbilities.GREAT_SERPENT.get(), JJKAbilities.NUE.get(), JJKAbilities.NUE_TOTALITY.get(),
            JJKAbilities.MAX_ELEPHANT.get(), JJKAbilities.RABBIT_ESCAPE.get(), JJKAbilities.TRANQUIL_DEER.get(),
            JJKAbilities.PIERCING_BULL.get(), JJKAbilities.AGITO.get(), JJKAbilities.MAHORAGA.get()),
    BOOGIE_WOOGIE(null, JJKAbilities.BOOGIE_WOOGIE.get(), JJKAbilities.FEINT.get());

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
