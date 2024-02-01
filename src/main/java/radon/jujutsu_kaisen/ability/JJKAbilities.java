package radon.jujutsu_kaisen.ability;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.mimicry.*;
import radon.jujutsu_kaisen.ability.projection_sorcery.*;
import radon.jujutsu_kaisen.ability.shockwave.Shockwave;
import radon.jujutsu_kaisen.ability.ai.dino_curse.BlueFire;
import radon.jujutsu_kaisen.ability.ai.max_elephant.Water;
import radon.jujutsu_kaisen.ability.ai.nue_totality.NueTotalityLightning;
import radon.jujutsu_kaisen.ability.ai.rika.ShootPureLove;
import radon.jujutsu_kaisen.ability.ai.scissor.Scissors;
import radon.jujutsu_kaisen.ability.shockwave.ShockwaveImbuement;
import radon.jujutsu_kaisen.ability.sky_strike.SkyStrike;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.ability.boogie_woogie.*;
import radon.jujutsu_kaisen.ability.idle_transfiguration.*;
import radon.jujutsu_kaisen.ability.misc.ZeroPointTwoSecondDomainExpansion;
import radon.jujutsu_kaisen.ability.curse_manipulation.*;
import radon.jujutsu_kaisen.ability.cursed_speech.*;
import radon.jujutsu_kaisen.ability.disaster_flames.*;
import radon.jujutsu_kaisen.ability.disaster_plants.*;
import radon.jujutsu_kaisen.ability.disaster_tides.*;
import radon.jujutsu_kaisen.ability.dismantle_and_cleave.*;
import radon.jujutsu_kaisen.ability.limitless.*;
import radon.jujutsu_kaisen.ability.misc.*;
import radon.jujutsu_kaisen.ability.misc.lightning.Discharge;
import radon.jujutsu_kaisen.ability.misc.lightning.Lightning;
import radon.jujutsu_kaisen.ability.sky_strike.SkyStrikeImbuement;
import radon.jujutsu_kaisen.ability.ten_shadows.*;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.NueLightning;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.PiercingWater;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.Wheel;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.*;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity;
import radon.jujutsu_kaisen.entity.curse.JogoatEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class JJKAbilities {
    public static DeferredRegister<Ability> ABILITIES = DeferredRegister.create(
            new ResourceLocation(JujutsuKaisen.MOD_ID, "ability"), JujutsuKaisen.MOD_ID);
    public static Supplier<IForgeRegistry<Ability>> ABILITY_REGISTRY =
            ABILITIES.makeRegistry(RegistryBuilder::new);

    public static RegistryObject<Shockwave> SHOCKWAVE = ABILITIES.register("shockwave", Shockwave::new);
    public static RegistryObject<ShockwaveImbuement> SHOCKWAVE_IMBUEMENT = ABILITIES.register("shockwave_imbuement", ShockwaveImbuement::new);

    public static RegistryObject<SkyStrike> SKY_STRIKE = ABILITIES.register("sky_strike", SkyStrike::new);
    public static RegistryObject<SkyStrikeImbuement> SKY_STRIKE_IMBUEMENT = ABILITIES.register("sky_strike_imbuement", SkyStrikeImbuement::new);

    public static RegistryObject<Dash> DASH = ABILITIES.register("dash", Dash::new);
    public static RegistryObject<Punch> PUNCH = ABILITIES.register("punch", Punch::new);
    public static RegistryObject<Slam> SLAM = ABILITIES.register("slam", Slam::new);
    public static RegistryObject<Barrage> BARRAGE = ABILITIES.register("barrage", Barrage::new);
    public static RegistryObject<Heal> HEAL = ABILITIES.register("heal", Heal::new);
    public static RegistryObject<DomainAmplification> DOMAIN_AMPLIFICATION = ABILITIES.register("domain_amplification", DomainAmplification::new);
    public static RegistryObject<SimpleDomain> SIMPLE_DOMAIN = ABILITIES.register("simple_domain", SimpleDomain::new);
    public static RegistryObject<QuickDraw> QUICK_DRAW = ABILITIES.register("quick_draw", QuickDraw::new);
    public static RegistryObject<FallingBlossomEmotion> FALLING_BLOSSOM_EMOTION = ABILITIES.register("falling_blossom_emotion", FallingBlossomEmotion::new);
    public static RegistryObject<CursedEnergyFlow> CURSED_ENERGY_FLOW = ABILITIES.register("cursed_energy_flow", CursedEnergyFlow::new);
    public static RegistryObject<CursedEnergyShield> CURSED_ENERGY_SHIELD = ABILITIES.register("cursed_energy_shield", CursedEnergyShield::new);
    public static RegistryObject<Lightning> LIGHTNING = ABILITIES.register("lightning", Lightning::new);
    public static RegistryObject<Discharge> DISCHARGE = ABILITIES.register("discharge", Discharge::new);
    public static RegistryObject<ZeroPointTwoSecondDomainExpansion> ZERO_POINT_TWO_SECOND_DOMAIN_EXPANSION = ABILITIES.register("zero_point_two_second_domain_expansion", ZeroPointTwoSecondDomainExpansion::new);
    public static RegistryObject<Switch> SWITCH = ABILITIES.register("switch", Switch::new);
    public static RegistryObject<CursedEnergyBomb> CURSED_ENERGY_BOMB = ABILITIES.register("cursed_energy_bomb", CursedEnergyBomb::new);
    public static RegistryObject<CursedEnergyBlast> CURSED_ENERGY_BLAST = ABILITIES.register("cursed_energy_blast", CursedEnergyBlast::new);

    public static RegistryObject<RCT1> RCT1 = ABILITIES.register("rct1", RCT1::new);
    public static RegistryObject<RCT2> RCT2 = ABILITIES.register("rct2", RCT2::new);
    public static RegistryObject<RCT3> RCT3 = ABILITIES.register("rct3", RCT3::new);
    public static RegistryObject<OutputRCT> OUTPUT_RCT = ABILITIES.register("output_rct", OutputRCT::new);

    public static RegistryObject<ShootPureLove> SHOOT_PURE_LOVE = ABILITIES.register("shoot_pure_love", ShootPureLove::new);
    public static RegistryObject<Water> WATER = ABILITIES.register("water", Water::new);
    public static RegistryObject<Scissors> SCISSORS = ABILITIES.register("scissors", Scissors::new);
    public static RegistryObject<BlueFire> BLUE_FIRE = ABILITIES.register("blue_fire", BlueFire::new);

    public static RegistryObject<Infinity> INFINITY = ABILITIES.register("infinity", Infinity::new);
    public static RegistryObject<Red> RED = ABILITIES.register("red", Red::new);
    public static RegistryObject<BlueStill> BLUE_STILL = ABILITIES.register("blue_still", BlueStill::new);
    public static RegistryObject<BlueMotion> BLUE_MOTION = ABILITIES.register("blue_motion", BlueMotion::new);
    public static RegistryObject<BlueFists> BLUE_FISTS = ABILITIES.register("blue_fists", BlueFists::new);
    public static RegistryObject<HollowPurple> HOLLOW_PURPLE = ABILITIES.register("hollow_purple", HollowPurple::new);
    public static RegistryObject<Teleport> TELEPORT = ABILITIES.register("teleport", Teleport::new);
    public static RegistryObject<Fly> FLY = ABILITIES.register("fly", Fly::new);
    public static RegistryObject<UnlimitedVoid> UNLIMITED_VOID = ABILITIES.register("unlimited_void", UnlimitedVoid::new);
    public static RegistryObject<LimitlessImbuement> LIMITLESS_IMBUEMENT = ABILITIES.register("limitless_imbuement", LimitlessImbuement::new);

    public static RegistryObject<Dismantle> DISMANTLE = ABILITIES.register("dismantle", Dismantle::new);
    public static RegistryObject<DismantleNet> DISMANTLE_NET = ABILITIES.register("dismantle_net", DismantleNet::new);
    public static RegistryObject<DismantleSkating> DISMANTLE_SKATING = ABILITIES.register("dismantle_skating", DismantleSkating::new);
    public static RegistryObject<Cleave> CLEAVE = ABILITIES.register("cleave", Cleave::new);
    public static RegistryObject<Spiderweb> SPIDERWEB = ABILITIES.register("spiderweb", Spiderweb::new);
    public static RegistryObject<FireArrow> FIRE_ARROW = ABILITIES.register("fire_arrow", FireArrow::new);
    public static RegistryObject<MalevolentShrine> MALEVOLENT_SHRINE = ABILITIES.register("malevolent_shrine", MalevolentShrine::new);
    public static RegistryObject<DismantleAndCleaveImbuement> DISMANTLE_AND_CLEAVE_IMBUEMENT = ABILITIES.register("dismantle_and_cleave_imbuement", DismantleAndCleaveImbuement::new);

    public static RegistryObject<Rika> RIKA = ABILITIES.register("rika", Rika::new);
    public static RegistryObject<Mimicry> MIMICRY = ABILITIES.register("mimicry", Mimicry::new);
    public static RegistryObject<CommandPureLove> COMMAND_PURE_LOVE = ABILITIES.register("command_pure_love", CommandPureLove::new);
    public static RegistryObject<GenuineMutualLove> GENUINE_MUTUAL_LOVE = ABILITIES.register("genuine_mutual_love", GenuineMutualLove::new);
    public static RegistryObject<MimicryImbuement> MIMICRY_IMBUEMENT = ABILITIES.register("mimicry_imbuement", MimicryImbuement::new);

    public static RegistryObject<EmberInsects> EMBER_INSECTS = ABILITIES.register("ember_insects", EmberInsects::new);
    public static RegistryObject<EmberInsectFlight> EMBER_INSECT_FLIGHT = ABILITIES.register("ember_insect_flight", EmberInsectFlight::new);
    public static RegistryObject<Volcano> VOLCANO = ABILITIES.register("volcano", Volcano::new);
    public static RegistryObject<MaximumMeteor> MAXIMUM_METEOR = ABILITIES.register("maximum_meteor", MaximumMeteor::new);
    public static RegistryObject<DisasterFlames> DISASTER_FLAMES = ABILITIES.register("disaster_flames", DisasterFlames::new);
    public static RegistryObject<Flamethrower> FLAMETHROWER = ABILITIES.register("flamethrower", Flamethrower::new);
    public static RegistryObject<Fireball> FIREBALL = ABILITIES.register("fireball", Fireball::new);
    public static RegistryObject<FireBeam> FIRE_BEAM = ABILITIES.register("fire_beam", FireBeam::new);
    public static RegistryObject<CoffinOfTheIronMountain> COFFIN_OF_THE_IRON_MOUNTAIN = ABILITIES.register("coffin_of_the_iron_mountain", CoffinOfTheIronMountain::new);
    public static RegistryObject<DisasterFlamesImbuement> DISASTER_FLAMES_IMBUEMENT = ABILITIES.register("disaster_flames_imbuement", DisasterFlamesImbuement::new);

    public static RegistryObject<HorizonOfTheCaptivatingSkandha> HORIZON_OF_THE_CAPTIVATING_SKANDHA = ABILITIES.register("horizon_of_the_captivating_skandha", HorizonOfTheCaptivatingSkandha::new);
    public static RegistryObject<DisasterTides> DISASTER_TIDES = ABILITIES.register("disaster_tides", DisasterTides::new);
    public static RegistryObject<WaterShield> WATER_SHIELD = ABILITIES.register("water_shield", WaterShield::new);
    public static RegistryObject<DeathSwarm> DEATH_SWARM = ABILITIES.register("death_swarm", DeathSwarm::new);
    public static RegistryObject<FishShikigami> FISH_SHIKIGAMI = ABILITIES.register("fish_shikigami", FishShikigami::new);
    public static RegistryObject<WaterTorrent> WATER_TORRENT = ABILITIES.register("water_torrent", WaterTorrent::new);
    public static RegistryObject<EelGrapple> EEL_GRAPPLE = ABILITIES.register("eel_grapple", EelGrapple::new);
    public static RegistryObject<DisasterTidesImbuement> DISASTER_TIDES_IMBUEMENT = ABILITIES.register("disaster_tides_imbuement", DisasterTidesImbuement::new);

    public static RegistryObject<ForestPlatform> FOREST_PLATFORM = ABILITIES.register("forest_platform", ForestPlatform::new);
    public static RegistryObject<ForestSpikes> FOREST_SPIKES = ABILITIES.register("forest_spikes", ForestSpikes::new);
    public static RegistryObject<WoodShield> WOOD_SHIELD = ABILITIES.register("wood_shield", WoodShield::new);
    public static RegistryObject<CursedBud> CURSED_BUD = ABILITIES.register("cursed_bud", CursedBud::new);
    public static RegistryObject<ForestWave> FOREST_WAVE = ABILITIES.register("forest_wave", ForestWave::new);
    public static RegistryObject<ForestRoots> FOREST_ROOTS = ABILITIES.register("forest_roots", ForestRoots::new);
    public static RegistryObject<ForestDash> FOREST_DASH = ABILITIES.register("forest_dash", ForestDash::new);
    public static RegistryObject<DisasterPlant> DISASTER_PLANT = ABILITIES.register("disaster_plant", DisasterPlant::new);
    public static RegistryObject<ShiningSeaOfFlowers> SHINING_SEA_OF_FLOWERS = ABILITIES.register("shining_sea_of_flowers", ShiningSeaOfFlowers::new);
    public static RegistryObject<DisasterPlantsImbuement> DISASTER_PLANTS_IMBUEMENT = ABILITIES.register("disaster_plants_imbuement", DisasterPlantsImbuement::new);

    public static RegistryObject<IdleTransfiguration> IDLE_TRANSFIGURATION = ABILITIES.register("idle_transfiguration", IdleTransfiguration::new);
    public static RegistryObject<SoulDecimation> SOUL_DECIMATION = ABILITIES.register("soul_decimation", SoulDecimation::new);
    public static RegistryObject<SoulReinforcement> SOUL_REINFORCEMENT = ABILITIES.register("soul_reinforcement", SoulReinforcement::new);
    public static RegistryObject<SoulRestoration> SOUL_RESTORATION = ABILITIES.register("soul_restoration", SoulRestoration::new);
    public static RegistryObject<ArmBlade> ARM_BLADE = ABILITIES.register("arm_blade", ArmBlade::new);
    public static RegistryObject<Gun> GUN = ABILITIES.register("gun", Gun::new);
    public static RegistryObject<HorseLegs> HORSE_LEGS = ABILITIES.register("horse_legs", HorseLegs::new);
    public static RegistryObject<Wings> WINGS = ABILITIES.register("wings", Wings::new);
    public static RegistryObject<TransfiguredSoulSmall> TRANSFIGURED_SOUL_SMALL = ABILITIES.register("transfigured_soul_small", TransfiguredSoulSmall::new);
    public static RegistryObject<TransfiguredSoulNormal> TRANSFIGURED_SOUL_NORMAL = ABILITIES.register("transfigured_soul_normal", TransfiguredSoulNormal::new);
    public static RegistryObject<TransfiguredSoulLarge> TRANSFIGURED_SOUL_LARGE = ABILITIES.register("transfigured_soul_large", TransfiguredSoulLarge::new);
    public static RegistryObject<PolymorphicSoulIsomer> POLYMORPHIC_SOUL_ISOMER = ABILITIES.register("polymorphic_soul_isomer", PolymorphicSoulIsomer::new);
    public static RegistryObject<InstantSpiritBodyOfDistortedKilling> INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING = ABILITIES.register("instant_spirit_body_of_distorted_killing", InstantSpiritBodyOfDistortedKilling::new);
    public static RegistryObject<SelfEmbodimentOfPerfection> SELF_EMBODIMENT_OF_PERFECTION = ABILITIES.register("self_embodiment_of_perfection", SelfEmbodimentOfPerfection::new);
    public static RegistryObject<IdleTransfigurationImbuement> IDLE_TRANSFIGURATION_IMBUEMENT = ABILITIES.register("idle_transfiguration_imbuement", IdleTransfigurationImbuement::new);

    public static RegistryObject<SwitchMode> SWITCH_MODE = ABILITIES.register("switch_mode", SwitchMode::new);
    public static RegistryObject<ReleaseShikigami> RELEASE_SHIKIGAMI = ABILITIES.register("release_shikigami", ReleaseShikigami::new);
    public static RegistryObject<ShadowStorage> SHADOW_STORAGE = ABILITIES.register("shadow_storage", ShadowStorage::new);
    public static RegistryObject<ShadowTravel> SHADOW_TRAVEL = ABILITIES.register("shadow_travel", ShadowTravel::new);
    public static RegistryObject<NueLightning> NUE_LIGHTNING = ABILITIES.register("nue_lightning", NueLightning::new);
    public static RegistryObject<NueTotalityLightning> NUE_TOTALITY_LIGHTNING = ABILITIES.register("nue_totality_lightning", NueTotalityLightning::new);
    public static RegistryObject<PiercingWater> PIERCING_WATER = ABILITIES.register("piercing_water", PiercingWater::new);
    public static RegistryObject<Wheel> WHEEL = ABILITIES.register("wheel", Wheel::new);
    public static RegistryObject<GreatSerpentGrab> GREAT_SERPENT_GRAB = ABILITIES.register("great_serpent_grab", GreatSerpentGrab::new);
    public static RegistryObject<Mahoraga> MAHORAGA = ABILITIES.register("mahoraga", Mahoraga::new);
    public static RegistryObject<DivineDogs> DIVINE_DOGS = ABILITIES.register("divine_dogs", DivineDogs::new);
    public static RegistryObject<DivineDogTotality> DIVINE_DOG_TOTALITY = ABILITIES.register("divine_dog_totality", DivineDogTotality::new);
    public static RegistryObject<Toad> TOAD = ABILITIES.register("toad", Toad::new);
    public static RegistryObject<ToadFusion> TOAD_FUSION = ABILITIES.register("toad_fusion", ToadFusion::new);
    public static RegistryObject<RabbitEscape> RABBIT_ESCAPE = ABILITIES.register("rabbit_escape", RabbitEscape::new);
    public static RegistryObject<Nue> NUE = ABILITIES.register("nue", Nue::new);
    public static RegistryObject<NueTotality> NUE_TOTALITY = ABILITIES.register("nue_totality", NueTotality::new);
    public static RegistryObject<GreatSerpent> GREAT_SERPENT = ABILITIES.register("great_serpent", GreatSerpent::new);
    public static RegistryObject<MaxElephant> MAX_ELEPHANT = ABILITIES.register("max_elephant", MaxElephant::new);
    public static RegistryObject<TranquilDeer> TRANQUIL_DEER = ABILITIES.register("tranquil_deer", TranquilDeer::new);
    public static RegistryObject<PiercingBull> PIERCING_BULL = ABILITIES.register("piercing_bull", PiercingBull::new);
    public static RegistryObject<Agito> AGITO = ABILITIES.register("agito", Agito::new);

    public static RegistryObject<ChimeraShadowGarden> CHIMERA_SHADOW_GARDEN = ABILITIES.register("chimera_shadow_garden", ChimeraShadowGarden::new);
    public static RegistryObject<TenShadowsImbuement> TEN_SHADOWS_IMBUEMENT = ABILITIES.register("ten_shadows_imbuement", TenShadowsImbuement::new);

    public static RegistryObject<CurseAbsorption> CURSE_ABSORPTION = ABILITIES.register("curse_absorption", CurseAbsorption::new);
    public static RegistryObject<ReleaseCurse> RELEASE_CURSE = ABILITIES.register("release_curse", ReleaseCurse::new);
    public static RegistryObject<ReleaseCurses> RELEASE_CURSES = ABILITIES.register("release_curses", ReleaseCurses::new);
    public static RegistryObject<SummonAll> SUMMON_ALL = ABILITIES.register("summon_all", SummonAll::new);
    public static RegistryObject<EnhanceCurse> ENHANCE_CURSE = ABILITIES.register("enhance_curse", EnhanceCurse::new);
    public static RegistryObject<MaximumUzumaki> MAXIMUM_UZUMAKI = ABILITIES.register("maximum_uzumaki", MaximumUzumaki::new);
    public static RegistryObject<MiniUzumaki> MINI_UZUMAKI = ABILITIES.register("mini_uzumaki", MiniUzumaki::new);
    public static RegistryObject<WormCurseGrab> WORM_CURSE_GRAB = ABILITIES.register("worm_curse_grab", WormCurseGrab::new);
    public static RegistryObject<CurseManipulationImbuement> CURSE_MANIPULATION_IMBUEMENT = ABILITIES.register("curse_manipulation_imbuement", CurseManipulationImbuement::new);

    public static RegistryObject<DontMove> DONT_MOVE = ABILITIES.register("dont_move", DontMove::new);
    public static RegistryObject<GetCrushed> GET_CRUSHED = ABILITIES.register("get_crushed", GetCrushed::new);
    public static RegistryObject<BlastAway> BLAST_AWAY = ABILITIES.register("blast_away", BlastAway::new);
    public static RegistryObject<Explode> EXPLODE = ABILITIES.register("explode", Explode::new);
    public static RegistryObject<Die> DIE = ABILITIES.register("die", Die::new);
    public static RegistryObject<CursedSpeechImbuement> CURSED_SPEECH_IMBUEMENT = ABILITIES.register("cursed_speech_imbuement", CursedSpeechImbuement::new);

    public static RegistryObject<SwapSelf> SWAP_SELF = ABILITIES.register("swap_self", SwapSelf::new);
    public static RegistryObject<SwapOthers> SWAP_OTHERS = ABILITIES.register("swap_others", SwapOthers::new);
    public static RegistryObject<Feint> FEINT = ABILITIES.register("feint", Feint::new);
    public static RegistryObject<CEThrow> CE_THROW = ABILITIES.register("ce_throw", CEThrow::new);
    public static RegistryObject<ItemSwap> ITEM_SWAP = ABILITIES.register("item_swap", ItemSwap::new);
    public static RegistryObject<Shuffle> SHUFFLE = ABILITIES.register("shuffle", Shuffle::new);
    public static RegistryObject<BoogieWoogieImbuement> BOOGIE_WOOGIE_IMBUEMENT = ABILITIES.register("boogie_woogie_imbuement", BoogieWoogieImbuement::new);

    public static RegistryObject<ProjectionSorcery> PROJECTION_SORCERY = ABILITIES.register("projection_sorcery", ProjectionSorcery::new);
    public static RegistryObject<TwentyFourFrameRule> TWENTY_FOUR_FRAME_RULE = ABILITIES.register("twenty_four_frame_rule", TwentyFourFrameRule::new);
    public static RegistryObject<AirFrame> AIR_FRAME = ABILITIES.register("air_frame", AirFrame::new);
    public static RegistryObject<TimeCellMoonPalace> TIME_CELL_MOON_PALACE = ABILITIES.register("time_cell_moon_palace", TimeCellMoonPalace::new);
    public static RegistryObject<ProjectionSorceryImbuement> PROJECTION_SORCERY_IMBUEMENT = ABILITIES.register("projection_sorcery_imbuement", ProjectionSorceryImbuement::new);

    public static ResourceLocation getKey(Ability ability) {
        return ABILITY_REGISTRY.get().getKey(ability);
    }

    public static Ability getValue(ResourceLocation key) {
        return ABILITY_REGISTRY.get().getValue(key);
    }

    public static boolean hasToggled(LivingEntity owner, Ability ability) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.hasToggled(ability);
    }

    @Nullable
    public static CursedSpirit createCurse(LivingEntity owner, AbsorbedCurse curse) {
        CursedSpirit entity = curse.getType() == EntityType.PLAYER ? JJKEntities.ABSORBED_PLAYER.get().create(owner.level()) :
                (CursedSpirit) curse.getType().create(owner.level());

        if (entity == null) return null;

        entity.setTame(true);
        entity.setOwner(owner);

        GameProfile profile = curse.getProfile();

        if (profile != null && entity instanceof AbsorbedPlayerEntity absorbed) {
            absorbed.setPlayer(profile);
        }

        Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
        Vec3 pos = owner.position()
                .subtract(direction.multiply(entity.getBbWidth(), 0.0D, entity.getBbWidth()));
        entity.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());

        return entity;
    }

    public static float getCurseExperience(AbsorbedCurse curse) {
        ISorcererData data = new SorcererData();
        data.deserializeNBT(curse.getData());
        return data.getExperience();
    }

    public static float getCurseCost(AbsorbedCurse curse) {
        return Math.max(1.0F, getCurseExperience(curse) * 0.01F);
    }

    @Nullable
    public static Entity summonCurse(LivingEntity owner, AbsorbedCurse curse, boolean charge) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        List<AbsorbedCurse> curses = cap.getCurses();

        if (!curses.contains(curse)) return null;

        return summonCurse(owner, curses.indexOf(curse), charge);
    }

    @Nullable
    public static Entity summonCurse(LivingEntity owner, int index, boolean charge) {
        if (owner.hasEffect(JJKEffects.UNLIMITED_VOID.get()) || hasToggled(owner, DOMAIN_AMPLIFICATION.get())) return null;

        ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        List<AbsorbedCurse> curses = ownerCap.getCurses();

        if (index >= curses.size()) return null;

        AbsorbedCurse curse = curses.get(index);

        if (charge) {
            float cost = getCurseCost(curse);

            if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
                if (ownerCap.getEnergy() < cost) {
                    return null;
                }
                ownerCap.useEnergy(cost);
            }
        }

        CursedSpirit entity = createCurse(owner, curse);

        if (entity == null) return null;

        owner.level().addFreshEntity(entity);

        ISorcererData curseCap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        curseCap.deserializeNBT(curse.getData());

        ownerCap.addSummon(entity);
        ownerCap.removeCurse(curse);

        if (owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerCap.serializeNBT()), player);
        }
        return entity;
    }

    @Nullable
    public static JujutsuType getType(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return null;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getType();
    }

    public static Set<Ability> getToggled(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return Set.of();
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getToggled();
    }

    public static boolean hasTamed(LivingEntity owner, EntityType<?> type) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;

        for (RegistryObject<Ability> ability : ABILITIES.getEntries()) {
            if (!(ability.get() instanceof Summon<?> summon)) continue;
            if (!summon.getTypes().contains(type)) continue;
            return summon.isTamed(owner);
        }
        return false;
    }

    public static boolean isDead(LivingEntity owner, EntityType<?> type) {
        if (!owner.getCapability(TenShadowsDataHandler.INSTANCE).isPresent()) return false;
        ITenShadowsData cap = owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
        Registry<EntityType<?>> registry = owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
        return cap.isDead(registry, type);
    }

    public static boolean isChanneling(LivingEntity owner, Ability ability) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.isChanneling(ability);
    }

    public static boolean hasTrait(LivingEntity owner, Trait trait) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.hasTrait(trait);
    }

    public static List<Ability> getAbilities(LivingEntity owner) {
        Set<Ability> abilities = new LinkedHashSet<>(List.of(JJKAbilities.HEAL.get(), JJKAbilities.RCT1.get(), JJKAbilities.RCT2.get(), JJKAbilities.RCT3.get()));

        if (owner instanceof JogoatEntity) {
            for (RegistryObject<ICursedTechnique> entry : JJKCursedTechniques.CURSED_TECHNIQUES.getEntries()) {
                ICursedTechnique technique = entry.get();
                
                abilities.addAll(technique.getAbilities());

                Ability domain = technique.getDomain();

                if (domain != null) {
                    abilities.add(domain);
                }
            }
            return new ArrayList<>(abilities);
        }

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (owner instanceof ISorcerer sorcerer) {
            abilities.addAll(sorcerer.getCustom());
        }

        for (RegistryObject<Ability> entry : ABILITIES.getEntries()) {
            Ability ability = entry.get();

            if (!ability.isTechnique() && (!cap.hasTrait(Trait.HEAVENLY_RESTRICTION) || ability.getCost(owner) == 0)) {
                abilities.add(ability);
            }
        }

        if (!cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            for (ICursedTechnique technique : cap.getTechniques()) {
                abilities.addAll(technique.getAbilities());
            }

            ICursedTechnique technique = cap.getTechnique();

            if (technique != null && technique.getDomain() != null) {
                abilities.add(technique.getDomain());
            }
        }
        abilities.removeIf(ability -> !ability.isValid(owner) && !(owner instanceof ISorcerer sorcerer && sorcerer.getCustom().contains(ability)));

        return new ArrayList<>(abilities);
    }
}