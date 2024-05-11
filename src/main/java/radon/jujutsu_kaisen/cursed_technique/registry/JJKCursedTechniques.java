package radon.jujutsu_kaisen.cursed_technique.registry;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class JJKCursedTechniques {
    public static ResourceKey<Registry<CursedTechnique>> CURSED_TECHNIQUE_KEY = ResourceKey.createRegistryKey(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_technique"));
    public static Registry<CursedTechnique> CURSED_TECHNIQUE_REGISTRY = new RegistryBuilder<>(CURSED_TECHNIQUE_KEY).create();
    public static DeferredRegister<CursedTechnique> CURSED_TECHNIQUES = DeferredRegister.create(CURSED_TECHNIQUE_REGISTRY, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<CursedTechnique, CursedTechnique> SHOCKWAVE = CURSED_TECHNIQUES.register("shockwave", () ->
            new CursedTechnique.Builder().abilities(JJKAbilities.SHOCKWAVE).build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> SKY_STRIKE = CURSED_TECHNIQUES.register("sky_strike", () ->
            new CursedTechnique.Builder().abilities(JJKAbilities.SKY_STRIKE).build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> SCISSORS = CURSED_TECHNIQUES.register("scissors", () ->
            new CursedTechnique.Builder().abilities(JJKAbilities.SCISSORS).build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> CURSE_MANIPULATION = CURSED_TECHNIQUES.register("curse_manipulation", () ->
            new CursedTechnique.Builder()
                    .abilities(
                            JJKAbilities.CURSE_ABSORPTION,
                            JJKAbilities.RELEASE_CURSE,
                            JJKAbilities.RELEASE_CURSES,
                            JJKAbilities.SUMMON_ALL,
                            JJKAbilities.ENHANCE_CURSE,
                            JJKAbilities.MAXIMUM_UZUMAKI,
                            JJKAbilities.MINI_UZUMAKI,
                            JJKAbilities.WORM_CURSE_GRAB,
                            JJKAbilities.FISH_SWARM
                    )
                    .build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> LIMITLESS = CURSED_TECHNIQUES.register("limitless", () ->
            new CursedTechnique.Builder()
                    .abilities(
                            JJKAbilities.INFINITY,
                            JJKAbilities.RED,
                            JJKAbilities.BLUE_STILL,
                            JJKAbilities.BLUE_MOTION,
                            JJKAbilities.BLUE_FISTS,
                            JJKAbilities.HOLLOW_PURPLE,
                            JJKAbilities.TELEPORT_SELF,
                            JJKAbilities.TELEPORT_OTHERS,
                            JJKAbilities.AZURE_GLIDE
                    )
                    .domain(JJKAbilities.UNLIMITED_VOID)
                    .build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> SHRINE = CURSED_TECHNIQUES.register("shrine", () ->
            new CursedTechnique.Builder()
                    .abilities(
                            JJKAbilities.DISMANTLE,
                            JJKAbilities.BIG_DISMANTLE,
                            JJKAbilities.DISMANTLE_NET,
                            JJKAbilities.DISMANTLE_SKATING,
                            JJKAbilities.CLEAVE,
                            JJKAbilities.SPIDERWEB,
                            JJKAbilities.FURNACE_OPEN
                    )
                    .domain(JJKAbilities.MALEVOLENT_SHRINE)
                    .build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> CURSED_SPEECH = CURSED_TECHNIQUES.register("cursed_speech", () ->
            new CursedTechnique.Builder()
                    .abilities(
                            JJKAbilities.DONT_MOVE,
                            JJKAbilities.GET_CRUSHED,
                            JJKAbilities.BLAST_AWAY,
                            JJKAbilities.EXPLODE,
                            JJKAbilities.DIE
                    )
                    .build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> MIMICRY = CURSED_TECHNIQUES.register("mimicry", () ->
            new CursedTechnique.Builder()
                    .abilities(
                            JJKAbilities.RIKA,
                            JJKAbilities.MIMICRY,
                            JJKAbilities.REFILL,
                            JJKAbilities.COMMAND_PURE_LOVE
                    )
                    .domain(JJKAbilities.AUTHENTIC_MUTUAL_LOVE)
                    .build());

    public static DeferredHolder<CursedTechnique, CursedTechnique> DISASTER_FLAMES = CURSED_TECHNIQUES.register("disaster_flames", () ->
            new CursedTechnique.Builder()
                    .abilities(
                            JJKAbilities.EMBER_INSECTS,
                            JJKAbilities.EMBER_INSECT_FLIGHT,
                            JJKAbilities.VOLCANO,
                            JJKAbilities.MAXIMUM_METEOR,
                            JJKAbilities.DISASTER_FLAMES,
                            JJKAbilities.FLAMETHROWER,
                            JJKAbilities.FIREBALL,
                            JJKAbilities.FIRE_BEAM
                    )
                    .build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> DISASTER_TIDES = CURSED_TECHNIQUES.register("disaster_tides", () ->
            new CursedTechnique.Builder()
                    .abilities(
                            JJKAbilities.DISASTER_TIDES,
                            JJKAbilities.WATER_SHIELD,
                            JJKAbilities.DEATH_SWARM,
                            JJKAbilities.FISH_SHIKIGAMI,
                            JJKAbilities.WATER_TORRENT,
                            JJKAbilities.EEL_GRAPPLE
                    )
                    .build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> DISASTER_PLANTS = CURSED_TECHNIQUES.register("disaster_plants", () ->
            new CursedTechnique.Builder()
                    .abilities(
                            JJKAbilities.FOREST_PLATFORM,
                            JJKAbilities.FOREST_SPIKES,
                            JJKAbilities.WOOD_SHIELD,
                            JJKAbilities.CURSED_BUD,
                            JJKAbilities.FOREST_WAVE,
                            JJKAbilities.FOREST_ROOTS,
                            JJKAbilities.FOREST_DASH,
                            JJKAbilities.DISASTER_PLANT
                    )
                    .build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> IDLE_TRANSFIGURATION = CURSED_TECHNIQUES.register("idle_transfiguration", () ->
            new CursedTechnique.Builder()
                    .abilities(
                            JJKAbilities.IDLE_TRANSFIGURATION,
                            JJKAbilities.SOUL_DECIMATION,
                            JJKAbilities.SOUL_REINFORCEMENT,
                            JJKAbilities.SOUL_RESTORATION,
                            JJKAbilities.BODY_REPEL,
                            JJKAbilities.FEROCIOUS_BODY_REPEL,
                            JJKAbilities.ARM_BLADE,
                            JJKAbilities.GUN,
                            JJKAbilities.HORSE_LEGS,
                            JJKAbilities.WINGS,
                            JJKAbilities.TRANSFIGURED_SOUL_SMALL,
                            JJKAbilities.TRANSFIGURED_SOUL_NORMAL,
                            JJKAbilities.TRANSFIGURED_SOUL_LARGE,
                            JJKAbilities.POLYMORPHIC_SOUL_ISOMER,
                            JJKAbilities.INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING
                    )
                    .domain(JJKAbilities.SELF_EMBODIMENT_OF_PERFECTION)
                    .build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> TEN_SHADOWS = CURSED_TECHNIQUES.register("ten_shadows", () ->
            new CursedTechnique.Builder()
                    .abilities(
                            JJKAbilities.ABILITY_MODE,
                            JJKAbilities.RELEASE_SHIKIGAMI,
                            JJKAbilities.SHADOW_STORAGE,
                            JJKAbilities.SHADOW_TRAVEL,
                            JJKAbilities.NUE_LIGHTNING,
                            JJKAbilities.PIERCING_WATER,
                            JJKAbilities.WHEEL,
                            JJKAbilities.GREAT_SERPENT_GRAB,
                            JJKAbilities.DIVINE_DOG_WHITE,
                            JJKAbilities.DIVINE_DOG_BLACK,
                            JJKAbilities.DIVINE_DOG_TOTALITY,
                            JJKAbilities.TOAD,
                            JJKAbilities.TOAD_FUSION,
                            JJKAbilities.GREAT_SERPENT,
                            JJKAbilities.NUE,
                            JJKAbilities.NUE_TOTALITY,
                            JJKAbilities.MAX_ELEPHANT,
                            JJKAbilities.RABBIT_ESCAPE,
                            JJKAbilities.TRANQUIL_DEER,
                            JJKAbilities.PIERCING_BULL,
                            JJKAbilities.AGITO,
                            JJKAbilities.MAHORAGA
                    )
                    .domain(JJKAbilities.CHIMERA_SHADOW_GARDEN)
                    .build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> BOOGIE_WOOGIE = CURSED_TECHNIQUES.register("boogie_woogie", () ->
            new CursedTechnique.Builder()
                    .abilities(
                            JJKAbilities.SWAP_SELF,
                            JJKAbilities.SWAP_OTHERS,
                            JJKAbilities.FEINT,
                            JJKAbilities.CE_THROW,
                            JJKAbilities.ITEM_SWAP,
                            JJKAbilities.SHUFFLE
                    )
                    .build());
    public static DeferredHolder<CursedTechnique, CursedTechnique> PROJECTION_SORCERY = CURSED_TECHNIQUES.register("projection_sorcery", () ->
            new CursedTechnique.Builder()
                    .abilities(
                            JJKAbilities.PROJECTION_SORCERY,
                            JJKAbilities.TWENTY_FOUR_FRAME_RULE,
                            JJKAbilities.AIR_FRAME
                    )
                    .domain(JJKAbilities.TIME_CELL_MOON_PALACE)
                    .build());

    public static ResourceLocation getKey(CursedTechnique technique) {
        return CURSED_TECHNIQUE_REGISTRY.getKey(technique);
    }

    public static CursedTechnique getValue(ResourceLocation key) {
        return CURSED_TECHNIQUE_REGISTRY.get(key);
    }

    public static @Nullable CursedTechnique getTechnique(Ability ability) {
        for (DeferredHolder<CursedTechnique, ? extends CursedTechnique> entry : JJKCursedTechniques.CURSED_TECHNIQUES.getEntries()) {
            CursedTechnique technique = entry.get();

            if (technique.getAbilities().contains(ability) || technique.getDomain() == ability) {
                return technique;
            }
        }
        return null;
    }
}
