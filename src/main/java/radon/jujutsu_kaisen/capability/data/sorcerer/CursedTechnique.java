package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;

public enum CursedTechnique {
    CURSE_MANIPULATION(null, JJKAbilities.ABSORB_CURSE.get(), JJKAbilities.RELEASE_CURSE.get(), JJKAbilities.RELEASE_CURSES.get(),
            JJKAbilities.MAXIMUM_UZUMAKI.get(),
            JJKAbilities.MINI_UZUMAKI.get()),
    LIMITLESS(JJKAbilities.UNLIMITED_VOID.get(), JJKAbilities.INFINITY.get(), JJKAbilities.RED.get(), JJKAbilities.BLUE_STILL.get(),
            JJKAbilities.BLUE_MOTION.get(), JJKAbilities.BLUE_FISTS.get(), JJKAbilities.HOLLOW_PURPLE.get(), JJKAbilities.TELEPORT.get(), JJKAbilities.FLY.get()),
    DISMANTLE_AND_CLEAVE(JJKAbilities.MALEVOLENT_SHRINE.get(), JJKAbilities.DISMANTLE.get(), JJKAbilities.DISMANTLE_NET.get(), JJKAbilities.CLEAVE.get(), JJKAbilities.SPIDERWEB.get(),
            /*JJKAbilities.WORLD_SLASH.get(),*/ JJKAbilities.FIRE_ARROW.get()),
    CURSED_SPEECH(null, JJKAbilities.DONT_MOVE.get(), JJKAbilities.GET_CRUSHED.get(), JJKAbilities.BLAST_AWAY.get(), JJKAbilities.EXPLODE.get(), JJKAbilities.DIE.get()),
    MIMICRY(null, JJKAbilities.RIKA.get(), JJKAbilities.MIMICRY.get(), JJKAbilities.COMMAND_PURE_LOVE.get()),
    DISASTER_FLAMES(JJKAbilities.COFFIN_OF_THE_IRON_MOUNTAIN.get(), JJKAbilities.EMBER_INSECTS.get(), JJKAbilities.VOLCANO.get(),
            JJKAbilities.MAXIMUM_METEOR.get(), JJKAbilities.DISASTER_FLAMES.get(), JJKAbilities.FLAMETHROWER.get(), JJKAbilities.FIREBALL.get()),
    DISASTER_TIDES(JJKAbilities.HORIZON_OF_THE_CAPTIVATING_SKANDHA.get(), JJKAbilities.DISASTER_TIDES.get(), JJKAbilities.WATER_SHIELD.get(), JJKAbilities.DEATH_SWARM.get(), JJKAbilities.FISH_SHIKIGAMI.get(), JJKAbilities.WATER_TORRENT.get()),
    DISASTER_PLANTS(JJKAbilities.SHINING_SEA_OF_FLOWERS.get(), JJKAbilities.FOREST_PLATFORM.get(), JJKAbilities.FOREST_SPIKES.get(), JJKAbilities.WOOD_SHIELD.get(), JJKAbilities.CURSED_BUD.get(),
            JJKAbilities.FOREST_WAVE.get(), JJKAbilities.FOREST_ROOTS.get(), JJKAbilities.DISASTER_PLANT.get()),
    IDLE_TRANSFIGURATION(null, JJKAbilities.IDLE_TRANSFIGURATION.get(), JJKAbilities.SOUL_REINFORCEMENT.get(), JJKAbilities.INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING.get()),
    TEN_SHADOWS(JJKAbilities.CHIMERA_SHADOW_GARDEN.get(),
            JJKAbilities.ABILITY_MODE.get(),
            JJKAbilities.RELEASE_SHIKIGAMI.get(),
            JJKAbilities.SHADOW_STORAGE.get(),

            JJKAbilities.NUE_LIGHTNING.get(), JJKAbilities.PIERCING_WATER.get(), JJKAbilities.WHEEL.get(),

            JJKAbilities.DIVINE_DOGS.get(), JJKAbilities.DIVINE_DOG_TOTALITY.get(), JJKAbilities.TOAD.get(), JJKAbilities.TOAD_TOTALITY.get(),
            JJKAbilities.GREAT_SERPENT.get(), JJKAbilities.NUE.get(), JJKAbilities.NUE_TOTALITY.get(),
            JJKAbilities.MAX_ELEPHANT.get(), JJKAbilities.RABBIT_ESCAPE.get(), JJKAbilities.TRANQUIL_DEER.get(),
            JJKAbilities.PIERCING_BULL.get(), JJKAbilities.AGITO.get(), JJKAbilities.MAHORAGA.get()),
    BOOGIE_WOOGIE(null, JJKAbilities.BOOGIE_WOOGIE.get(), JJKAbilities.FEINT.get()),
    PROJECTION_SORCERY(JJKAbilities.TIME_CELL_MOON_PALACE.get(), JJKAbilities.PROJECTION_SORCERY.get(), JJKAbilities.TWENTY_FOUR_FRAME_RULE.get());

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
