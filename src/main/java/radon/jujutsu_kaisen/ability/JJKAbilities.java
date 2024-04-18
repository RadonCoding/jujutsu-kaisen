package radon.jujutsu_kaisen.ability;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.*;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.mimicry.*;
import radon.jujutsu_kaisen.ability.projection_sorcery.*;
import radon.jujutsu_kaisen.ability.shockwave.Shockwave;
import radon.jujutsu_kaisen.ability.ai.dino_curse.BlueFire;
import radon.jujutsu_kaisen.ability.ai.max_elephant.Water;
import radon.jujutsu_kaisen.ability.ai.nue_totality.NueTotalityLightning;
import radon.jujutsu_kaisen.ability.ai.rika.ShootPureLove;
import radon.jujutsu_kaisen.ability.scissor.Scissors;
import radon.jujutsu_kaisen.ability.sky_strike.SkyStrike;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.boogie_woogie.*;
import radon.jujutsu_kaisen.ability.idle_transfiguration.*;
import radon.jujutsu_kaisen.ability.misc.ZeroPointTwoSecondDomainExpansion;
import radon.jujutsu_kaisen.ability.curse_manipulation.*;
import radon.jujutsu_kaisen.ability.cursed_speech.*;
import radon.jujutsu_kaisen.ability.disaster_flames.*;
import radon.jujutsu_kaisen.ability.disaster_plants.*;
import radon.jujutsu_kaisen.ability.disaster_tides.*;
import radon.jujutsu_kaisen.ability.shrine.*;
import radon.jujutsu_kaisen.ability.limitless.*;
import radon.jujutsu_kaisen.ability.misc.*;
import radon.jujutsu_kaisen.ability.misc.lightning.Discharge;
import radon.jujutsu_kaisen.ability.misc.lightning.Lightning;
import radon.jujutsu_kaisen.ability.ten_shadows.*;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.NueLightning;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.PiercingWater;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.Wheel;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.*;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.curse.JogoatEntity;

import java.util.*;

public class JJKAbilities {
    public static ResourceKey<Registry<Ability>> ABILITY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(JujutsuKaisen.MOD_ID, "ability"));
    public static Registry<Ability> ABILITY_REGISTRY = new RegistryBuilder<>(ABILITY_KEY).create();
    public static DeferredRegister<Ability> ABILITIES = DeferredRegister.create(ABILITY_REGISTRY, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<Ability, Shockwave> SHOCKWAVE = ABILITIES.register("shockwave", Shockwave::new);

    public static DeferredHolder<Ability, SkyStrike> SKY_STRIKE = ABILITIES.register("sky_strike", SkyStrike::new);

    public static DeferredHolder<Ability, Scissors> SCISSORS = ABILITIES.register("scissors", Scissors::new);

    public static DeferredHolder<Ability, Dash> DASH = ABILITIES.register("dash", Dash::new);
    public static DeferredHolder<Ability, AirJump> AIR_JUMP = ABILITIES.register("air_jump", AirJump::new);
    public static DeferredHolder<Ability, Punch> PUNCH = ABILITIES.register("punch", Punch::new);
    public static DeferredHolder<Ability, Slam> SLAM = ABILITIES.register("slam", Slam::new);
    public static DeferredHolder<Ability, Barrage> BARRAGE = ABILITIES.register("barrage", Barrage::new);
    public static DeferredHolder<Ability, DomainAmplification> DOMAIN_AMPLIFICATION = ABILITIES.register("domain_amplification", DomainAmplification::new);
    public static DeferredHolder<Ability, SimpleDomain> SIMPLE_DOMAIN = ABILITIES.register("simple_domain", SimpleDomain::new);
    public static DeferredHolder<Ability, SimpleDomainEnlargement> SIMPLE_DOMAIN_ENLARGE = ABILITIES.register("simple_domain_enlargement", SimpleDomainEnlargement::new);
    public static DeferredHolder<Ability, QuickDraw> QUICK_DRAW = ABILITIES.register("quick_draw", QuickDraw::new);
    public static DeferredHolder<Ability, FallingBlossomEmotion> FALLING_BLOSSOM_EMOTION = ABILITIES.register("falling_blossom_emotion", FallingBlossomEmotion::new);
    public static DeferredHolder<Ability, CursedEnergyFlow> CURSED_ENERGY_FLOW = ABILITIES.register("cursed_energy_flow", CursedEnergyFlow::new);
    public static DeferredHolder<Ability, CursedEnergyShield> CURSED_ENERGY_SHIELD = ABILITIES.register("cursed_energy_shield", CursedEnergyShield::new);
    public static DeferredHolder<Ability, Lightning> LIGHTNING = ABILITIES.register("lightning", Lightning::new);
    public static DeferredHolder<Ability, Discharge> DISCHARGE = ABILITIES.register("discharge", Discharge::new);
    public static DeferredHolder<Ability, ZeroPointTwoSecondDomainExpansion> ZERO_POINT_TWO_SECOND_DOMAIN_EXPANSION = ABILITIES.register("zero_point_two_second_domain_expansion", ZeroPointTwoSecondDomainExpansion::new);
    public static DeferredHolder<Ability, Switch> SWITCH = ABILITIES.register("switch", Switch::new);
    public static DeferredHolder<Ability, CursedEnergyBomb> CURSED_ENERGY_BOMB = ABILITIES.register("cursed_energy_bomb", CursedEnergyBomb::new);
    public static DeferredHolder<Ability, CursedEnergyBlast> CURSED_ENERGY_BLAST = ABILITIES.register("cursed_energy_blast", CursedEnergyBlast::new);

    public static DeferredHolder<Ability, RCT1> RCT1 = ABILITIES.register("rct1", RCT1::new);
    public static DeferredHolder<Ability, RCT2> RCT2 = ABILITIES.register("rct2", RCT2::new);
    public static DeferredHolder<Ability, RCT3> RCT3 = ABILITIES.register("rct3", RCT3::new);
    public static DeferredHolder<Ability, OutputRCT> OUTPUT_RCT = ABILITIES.register("output_rct", OutputRCT::new);
    public static DeferredHolder<Ability, Heal> HEAL = ABILITIES.register("heal", Heal::new);
    public static DeferredHolder<Ability, VeilActivate> VEIL_ACTIVATE = ABILITIES.register("veil_activate", VeilActivate::new);
    public static DeferredHolder<Ability, VeilDeactivate> VEIL_DEACTIVATE = ABILITIES.register("veil_deactivate", VeilDeactivate::new);

    public static DeferredHolder<Ability, ShootPureLove> SHOOT_PURE_LOVE = ABILITIES.register("shoot_pure_love", ShootPureLove::new);

    public static DeferredHolder<Ability, NueTotalityLightning> NUE_TOTALITY_LIGHTNING = ABILITIES.register("nue_totality_lightning", NueTotalityLightning::new);

    public static DeferredHolder<Ability, Water> WATER = ABILITIES.register("water", Water::new);

    public static DeferredHolder<Ability, BlueFire> BLUE_FIRE = ABILITIES.register("blue_fire", BlueFire::new);

    public static DeferredHolder<Ability, Infinity> INFINITY = ABILITIES.register("infinity", Infinity::new);
    public static DeferredHolder<Ability, Red> RED = ABILITIES.register("red", Red::new);
    public static DeferredHolder<Ability, BlueStill> BLUE_STILL = ABILITIES.register("blue_still", BlueStill::new);
    public static DeferredHolder<Ability, BlueMotion> BLUE_MOTION = ABILITIES.register("blue_motion", BlueMotion::new);
    public static DeferredHolder<Ability, BlueFists> BLUE_FISTS = ABILITIES.register("blue_fists", BlueFists::new);
    public static DeferredHolder<Ability, HollowPurple> HOLLOW_PURPLE = ABILITIES.register("hollow_purple", HollowPurple::new);
    public static DeferredHolder<Ability, TeleportSelf> TELEPORT_SELF = ABILITIES.register("teleport_self", TeleportSelf::new);
    public static DeferredHolder<Ability, TeleportOthers> TELEPORT_OTHERS = ABILITIES.register("teleport_others", TeleportOthers::new);
    public static DeferredHolder<Ability, AzureGlide> AZURE_GLIDE = ABILITIES.register("azure_glide", AzureGlide::new);
    public static DeferredHolder<Ability, UnlimitedVoid> UNLIMITED_VOID = ABILITIES.register("unlimited_void", UnlimitedVoid::new);

    public static DeferredHolder<Ability, Dismantle> DISMANTLE = ABILITIES.register("dismantle", Dismantle::new);
    public static DeferredHolder<Ability, BigDismantle> BIG_DISMANTLE = ABILITIES.register("big_dismantle", BigDismantle::new);
    public static DeferredHolder<Ability, WorldSlash> WORLD_SLASH = ABILITIES.register("world_slash", WorldSlash::new);
    public static DeferredHolder<Ability, DismantleNet> DISMANTLE_NET = ABILITIES.register("dismantle_net", DismantleNet::new);
    public static DeferredHolder<Ability, DismantleSkating> DISMANTLE_SKATING = ABILITIES.register("dismantle_skating", DismantleSkating::new);
    public static DeferredHolder<Ability, Cleave> CLEAVE = ABILITIES.register("cleave", Cleave::new);
    public static DeferredHolder<Ability, Spiderweb> SPIDERWEB = ABILITIES.register("spiderweb", Spiderweb::new);
    public static DeferredHolder<Ability, FireArrow> FIRE_ARROW = ABILITIES.register("fire_arrow", FireArrow::new);
    public static DeferredHolder<Ability, MalevolentShrine> MALEVOLENT_SHRINE = ABILITIES.register("malevolent_shrine", MalevolentShrine::new);

    public static DeferredHolder<Ability, Rika> RIKA = ABILITIES.register("rika", Rika::new);
    public static DeferredHolder<Ability, Mimicry> MIMICRY = ABILITIES.register("mimicry", Mimicry::new);
    public static DeferredHolder<Ability, Refill> REFILL = ABILITIES.register("refill", Refill::new);
    public static DeferredHolder<Ability, CommandPureLove> COMMAND_PURE_LOVE = ABILITIES.register("command_pure_love", CommandPureLove::new);
    public static DeferredHolder<Ability, AuthenticMutualLove> AUTHENTIC_MUTUAL_LOVE = ABILITIES.register("authentic_mutual_love", AuthenticMutualLove::new);

    public static DeferredHolder<Ability, EmberInsects> EMBER_INSECTS = ABILITIES.register("ember_insects", EmberInsects::new);
    public static DeferredHolder<Ability, EmberInsectFlight> EMBER_INSECT_FLIGHT = ABILITIES.register("ember_insect_flight", EmberInsectFlight::new);
    public static DeferredHolder<Ability, Volcano> VOLCANO = ABILITIES.register("volcano", Volcano::new);
    public static DeferredHolder<Ability, MaximumMeteor> MAXIMUM_METEOR = ABILITIES.register("maximum_meteor", MaximumMeteor::new);
    public static DeferredHolder<Ability, DisasterFlames> DISASTER_FLAMES = ABILITIES.register("disaster_flames", DisasterFlames::new);
    public static DeferredHolder<Ability, Flamethrower> FLAMETHROWER = ABILITIES.register("flamethrower", Flamethrower::new);
    public static DeferredHolder<Ability, Fireball> FIREBALL = ABILITIES.register("fireball", Fireball::new);
    public static DeferredHolder<Ability, FireBeam> FIRE_BEAM = ABILITIES.register("fire_beam", FireBeam::new);
    public static DeferredHolder<Ability, CoffinOfTheIronMountain> COFFIN_OF_THE_IRON_MOUNTAIN = ABILITIES.register("coffin_of_the_iron_mountain", CoffinOfTheIronMountain::new);

    public static DeferredHolder<Ability, HorizonOfTheCaptivatingSkandha> HORIZON_OF_THE_CAPTIVATING_SKANDHA = ABILITIES.register("horizon_of_the_captivating_skandha", HorizonOfTheCaptivatingSkandha::new);
    public static DeferredHolder<Ability, DisasterTides> DISASTER_TIDES = ABILITIES.register("disaster_tides", DisasterTides::new);
    public static DeferredHolder<Ability, WaterShield> WATER_SHIELD = ABILITIES.register("water_shield", WaterShield::new);
    public static DeferredHolder<Ability, DeathSwarm> DEATH_SWARM = ABILITIES.register("death_swarm", DeathSwarm::new);
    public static DeferredHolder<Ability, FishShikigami> FISH_SHIKIGAMI = ABILITIES.register("fish_shikigami", FishShikigami::new);
    public static DeferredHolder<Ability, WaterTorrent> WATER_TORRENT = ABILITIES.register("water_torrent", WaterTorrent::new);
    public static DeferredHolder<Ability, EelGrapple> EEL_GRAPPLE = ABILITIES.register("eel_grapple", EelGrapple::new);

    public static DeferredHolder<Ability, ForestPlatform> FOREST_PLATFORM = ABILITIES.register("forest_platform", ForestPlatform::new);
    public static DeferredHolder<Ability, ForestSpikes> FOREST_SPIKES = ABILITIES.register("forest_spikes", ForestSpikes::new);
    public static DeferredHolder<Ability, WoodShield> WOOD_SHIELD = ABILITIES.register("wood_shield", WoodShield::new);
    public static DeferredHolder<Ability, CursedBud> CURSED_BUD = ABILITIES.register("cursed_bud", CursedBud::new);
    public static DeferredHolder<Ability, ForestWave> FOREST_WAVE = ABILITIES.register("forest_wave", ForestWave::new);
    public static DeferredHolder<Ability, ForestRoots> FOREST_ROOTS = ABILITIES.register("forest_roots", ForestRoots::new);
    public static DeferredHolder<Ability, ForestDash> FOREST_DASH = ABILITIES.register("forest_dash", ForestDash::new);
    public static DeferredHolder<Ability, DisasterPlant> DISASTER_PLANT = ABILITIES.register("disaster_plant", DisasterPlant::new);
    public static DeferredHolder<Ability, ShiningSeaOfFlowers> SHINING_SEA_OF_FLOWERS = ABILITIES.register("shining_sea_of_flowers", ShiningSeaOfFlowers::new);

    public static DeferredHolder<Ability, IdleTransfiguration> IDLE_TRANSFIGURATION = ABILITIES.register("idle_transfiguration", IdleTransfiguration::new);
    public static DeferredHolder<Ability, SoulDecimation> SOUL_DECIMATION = ABILITIES.register("soul_decimation", SoulDecimation::new);
    public static DeferredHolder<Ability, SoulReinforcement> SOUL_REINFORCEMENT = ABILITIES.register("soul_reinforcement", SoulReinforcement::new);
    public static DeferredHolder<Ability, SoulRestoration> SOUL_RESTORATION = ABILITIES.register("soul_restoration", SoulRestoration::new);
    public static DeferredHolder<Ability, BodyRepel> BODY_REPEL = ABILITIES.register("body_repel", BodyRepel::new);
    public static DeferredHolder<Ability, FerociousBodyRepel> FEROCIOUS_BODY_REPEL = ABILITIES.register("ferocious_body_repel", FerociousBodyRepel::new);
    public static DeferredHolder<Ability, ArmBlade> ARM_BLADE = ABILITIES.register("arm_blade", ArmBlade::new);
    public static DeferredHolder<Ability, Gun> GUN = ABILITIES.register("gun", Gun::new);
    public static DeferredHolder<Ability, HorseLegs> HORSE_LEGS = ABILITIES.register("horse_legs", HorseLegs::new);
    public static DeferredHolder<Ability, Wings> WINGS = ABILITIES.register("wings", Wings::new);
    public static DeferredHolder<Ability, TransfiguredSoulSmall> TRANSFIGURED_SOUL_SMALL = ABILITIES.register("transfigured_soul_small", TransfiguredSoulSmall::new);
    public static DeferredHolder<Ability, TransfiguredSoulNormal> TRANSFIGURED_SOUL_NORMAL = ABILITIES.register("transfigured_soul_normal", TransfiguredSoulNormal::new);
    public static DeferredHolder<Ability, TransfiguredSoulLarge> TRANSFIGURED_SOUL_LARGE = ABILITIES.register("transfigured_soul_large", TransfiguredSoulLarge::new);
    public static DeferredHolder<Ability, PolymorphicSoulIsomer> POLYMORPHIC_SOUL_ISOMER = ABILITIES.register("polymorphic_soul_isomer", PolymorphicSoulIsomer::new);
    public static DeferredHolder<Ability, InstantSpiritBodyOfDistortedKilling> INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING = ABILITIES.register("instant_spirit_body_of_distorted_killing", InstantSpiritBodyOfDistortedKilling::new);
    public static DeferredHolder<Ability, SelfEmbodimentOfPerfection> SELF_EMBODIMENT_OF_PERFECTION = ABILITIES.register("self_embodiment_of_perfection", SelfEmbodimentOfPerfection::new);

    public static DeferredHolder<Ability, AbilityMode> ABILITY_MODE = ABILITIES.register("ability_mode", AbilityMode::new);
    public static DeferredHolder<Ability, ReleaseShikigami> RELEASE_SHIKIGAMI = ABILITIES.register("release_shikigami", ReleaseShikigami::new);
    public static DeferredHolder<Ability, ShadowStorage> SHADOW_STORAGE = ABILITIES.register("shadow_storage", ShadowStorage::new);
    public static DeferredHolder<Ability, ShadowTravel> SHADOW_TRAVEL = ABILITIES.register("shadow_travel", ShadowTravel::new);
    public static DeferredHolder<Ability, NueLightning> NUE_LIGHTNING = ABILITIES.register("nue_lightning", NueLightning::new);
    public static DeferredHolder<Ability, PiercingWater> PIERCING_WATER = ABILITIES.register("piercing_water", PiercingWater::new);
    public static DeferredHolder<Ability, Wheel> WHEEL = ABILITIES.register("wheel", Wheel::new);
    public static DeferredHolder<Ability, GreatSerpentGrab> GREAT_SERPENT_GRAB = ABILITIES.register("great_serpent_grab", GreatSerpentGrab::new);
    public static DeferredHolder<Ability, Mahoraga> MAHORAGA = ABILITIES.register("mahoraga", Mahoraga::new);
    public static DeferredHolder<Ability, DivineDogWhite> DIVINE_DOG_WHITE = ABILITIES.register("divine_dog_white", DivineDogWhite::new);
    public static DeferredHolder<Ability, DivineDogBlack> DIVINE_DOG_BLACK = ABILITIES.register("divine_dog_black", DivineDogBlack::new);

    public static DeferredHolder<Ability, DivineDogTotality> DIVINE_DOG_TOTALITY = ABILITIES.register("divine_dog_totality", DivineDogTotality::new);
    public static DeferredHolder<Ability, Toad> TOAD = ABILITIES.register("toad", Toad::new);
    public static DeferredHolder<Ability, ToadFusion> TOAD_FUSION = ABILITIES.register("toad_fusion", ToadFusion::new);
    public static DeferredHolder<Ability, RabbitEscape> RABBIT_ESCAPE = ABILITIES.register("rabbit_escape", RabbitEscape::new);
    public static DeferredHolder<Ability, Nue> NUE = ABILITIES.register("nue", Nue::new);
    public static DeferredHolder<Ability, NueTotality> NUE_TOTALITY = ABILITIES.register("nue_totality", NueTotality::new);
    public static DeferredHolder<Ability, GreatSerpent> GREAT_SERPENT = ABILITIES.register("great_serpent", GreatSerpent::new);
    public static DeferredHolder<Ability, MaxElephant> MAX_ELEPHANT = ABILITIES.register("max_elephant", MaxElephant::new);
    public static DeferredHolder<Ability, TranquilDeer> TRANQUIL_DEER = ABILITIES.register("tranquil_deer", TranquilDeer::new);
    public static DeferredHolder<Ability, PiercingBull> PIERCING_BULL = ABILITIES.register("piercing_bull", PiercingBull::new);
    public static DeferredHolder<Ability, Agito> AGITO = ABILITIES.register("agito", Agito::new);

    public static DeferredHolder<Ability, ChimeraShadowGarden> CHIMERA_SHADOW_GARDEN = ABILITIES.register("chimera_shadow_garden", ChimeraShadowGarden::new);

    public static DeferredHolder<Ability, CurseAbsorption> CURSE_ABSORPTION = ABILITIES.register("curse_absorption", CurseAbsorption::new);
    public static DeferredHolder<Ability, ReleaseCurse> RELEASE_CURSE = ABILITIES.register("release_curse", ReleaseCurse::new);
    public static DeferredHolder<Ability, ReleaseCurses> RELEASE_CURSES = ABILITIES.register("release_curses", ReleaseCurses::new);
    public static DeferredHolder<Ability, SummonAll> SUMMON_ALL = ABILITIES.register("summon_all", SummonAll::new);
    public static DeferredHolder<Ability, EnhanceCurse> ENHANCE_CURSE = ABILITIES.register("enhance_curse", EnhanceCurse::new);
    public static DeferredHolder<Ability, MaximumUzumaki> MAXIMUM_UZUMAKI = ABILITIES.register("maximum_uzumaki", MaximumUzumaki::new);
    public static DeferredHolder<Ability, MiniUzumaki> MINI_UZUMAKI = ABILITIES.register("mini_uzumaki", MiniUzumaki::new);
    public static DeferredHolder<Ability, WormCurseGrab> WORM_CURSE_GRAB = ABILITIES.register("worm_curse_grab", WormCurseGrab::new);
    public static DeferredHolder<Ability, FishSwarm> FISH_SWARM = ABILITIES.register("fish_swarm", FishSwarm::new);

    public static DeferredHolder<Ability, DontMove> DONT_MOVE = ABILITIES.register("dont_move", DontMove::new);
    public static DeferredHolder<Ability, GetCrushed> GET_CRUSHED = ABILITIES.register("get_crushed", GetCrushed::new);
    public static DeferredHolder<Ability, BlastAway> BLAST_AWAY = ABILITIES.register("blast_away", BlastAway::new);
    public static DeferredHolder<Ability, Explode> EXPLODE = ABILITIES.register("explode", Explode::new);
    public static DeferredHolder<Ability, Die> DIE = ABILITIES.register("die", Die::new);

    public static DeferredHolder<Ability, SwapSelf> SWAP_SELF = ABILITIES.register("swap_self", SwapSelf::new);
    public static DeferredHolder<Ability, SwapOthers> SWAP_OTHERS = ABILITIES.register("swap_others", SwapOthers::new);
    public static DeferredHolder<Ability, Feint> FEINT = ABILITIES.register("feint", Feint::new);
    public static DeferredHolder<Ability, CEThrow> CE_THROW = ABILITIES.register("ce_throw", CEThrow::new);
    public static DeferredHolder<Ability, ItemSwap> ITEM_SWAP = ABILITIES.register("item_swap", ItemSwap::new);
    public static DeferredHolder<Ability, Shuffle> SHUFFLE = ABILITIES.register("shuffle", Shuffle::new);

    public static DeferredHolder<Ability, ProjectionSorcery> PROJECTION_SORCERY = ABILITIES.register("projection_sorcery", ProjectionSorcery::new);
    public static DeferredHolder<Ability, TwentyFourFrameRule> TWENTY_FOUR_FRAME_RULE = ABILITIES.register("twenty_four_frame_rule", TwentyFourFrameRule::new);
    public static DeferredHolder<Ability, AirFrame> AIR_FRAME = ABILITIES.register("air_frame", AirFrame::new);
    public static DeferredHolder<Ability, TimeCellMoonPalace> TIME_CELL_MOON_PALACE = ABILITIES.register("time_cell_moon_palace", TimeCellMoonPalace::new);

    public static ResourceLocation getKey(Ability ability) {
        return ABILITY_REGISTRY.getKey(ability);
    }

    public static Ability getValue(ResourceLocation key) {
        return ABILITY_REGISTRY.get(key);
    }

    public static List<Ability> getAbilities(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return List.of();

        ISorcererData data = cap.getSorcererData();

        if (data == null) return List.of();

        Set<Ability> abilities = new LinkedHashSet<>();

        if (owner instanceof JogoatEntity) {
            for (DeferredHolder<ICursedTechnique, ? extends ICursedTechnique> entry : JJKCursedTechniques.CURSED_TECHNIQUES.getEntries()) {
                ICursedTechnique technique = entry.get();
                
                abilities.addAll(technique.getAbilities());

                Ability domain = technique.getDomain();

                if (domain != null) {
                    abilities.add(domain);
                }
            }
            return new ArrayList<>(abilities);
        }

        if (owner instanceof ISorcerer sorcerer) {
            abilities.addAll(sorcerer.getCustom());
        }

        for (DeferredHolder<Ability, ? extends Ability> entry : ABILITIES.getEntries()) {
            Ability ability = entry.get();

            if (!ability.isTechnique() && (!data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY) || ability.isPhysical())) {
                abilities.add(ability);
            }
        }

        if (!data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY)) {
            for (ICursedTechnique technique : data.getActiveTechniques()) {
                abilities.addAll(technique.getAbilities());
            }

            ICursedTechnique technique = data.getTechnique();

            if (technique != null && technique.getDomain() != null) {
                abilities.add(technique.getDomain());
            }
        }
        abilities.removeIf(ability -> !ability.isValid(owner) && !(owner instanceof ISorcerer sorcerer && sorcerer.getCustom().contains(ability)));

        return new ArrayList<>(abilities);
    }
}