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
import radon.jujutsu_kaisen.ability.ai.cyclops.CyclopsSmash;
import radon.jujutsu_kaisen.ability.ai.dino_curse.BlueFire;
import radon.jujutsu_kaisen.ability.ai.max_elephant.Water;
import radon.jujutsu_kaisen.ability.ai.nue_totality.NueTotalityLightning;
import radon.jujutsu_kaisen.ability.ai.rika.ShootPureLove;
import radon.jujutsu_kaisen.ability.ai.scissor.Scissors;
import radon.jujutsu_kaisen.ability.ai.zomba_curse.SkyStrike;
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
import radon.jujutsu_kaisen.ability.projection_sorcery.AirFrame;
import radon.jujutsu_kaisen.ability.projection_sorcery.ProjectionSorcery;
import radon.jujutsu_kaisen.ability.projection_sorcery.TimeCellMoonPalace;
import radon.jujutsu_kaisen.ability.projection_sorcery.TwentyFourFrameRule;
import radon.jujutsu_kaisen.ability.mimicry.CommandPureLove;
import radon.jujutsu_kaisen.ability.mimicry.Mimicry;
import radon.jujutsu_kaisen.ability.mimicry.Rika;
import radon.jujutsu_kaisen.ability.ten_shadows.*;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.NueLightning;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.PiercingWater;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.Wheel;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.*;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
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

    public static RegistryObject<Ability> INFINITY = ABILITIES.register("infinity", Infinity::new);
    public static RegistryObject<Ability> RED = ABILITIES.register("red", Red::new);
    public static RegistryObject<Ability> BLUE_STILL = ABILITIES.register("blue_still", BlueStill::new);
    public static RegistryObject<Ability> BLUE_MOTION = ABILITIES.register("blue_motion", BlueMotion::new);
    public static RegistryObject<Ability> BLUE_FISTS = ABILITIES.register("blue_fists", BlueFists::new);
    public static RegistryObject<Ability> HOLLOW_PURPLE = ABILITIES.register("hollow_purple", HollowPurple::new);
    public static RegistryObject<Ability> TELEPORT = ABILITIES.register("teleport", Teleport::new);
    public static RegistryObject<Ability> FLY = ABILITIES.register("fly", Fly::new);
    public static RegistryObject<Ability> UNLIMITED_VOID = ABILITIES.register("unlimited_void", UnlimitedVoid::new);

    public static RegistryObject<Ability> DISMANTLE = ABILITIES.register("dismantle", Dismantle::new);
    public static RegistryObject<Ability> DISMANTLE_NET = ABILITIES.register("dismantle_net", DismantleNet::new);
    public static RegistryObject<Ability> DISMANTLE_SKATING = ABILITIES.register("dismantle_skating", DismantleSkating::new);
    public static RegistryObject<Ability> CLEAVE = ABILITIES.register("cleave", Cleave::new);
    public static RegistryObject<Ability> SPIDERWEB = ABILITIES.register("spiderweb", Spiderweb::new);
    public static RegistryObject<Ability> FIRE_ARROW = ABILITIES.register("fire_arrow", FireArrow::new);
    public static RegistryObject<Ability> MALEVOLENT_SHRINE = ABILITIES.register("malevolent_shrine", MalevolentShrine::new);

    public static RegistryObject<Summon<?>> RIKA = ABILITIES.register("rika", Rika::new);
    public static RegistryObject<Ability> MIMICRY = ABILITIES.register("mimicry", Mimicry::new);
    public static RegistryObject<Ability> COMMAND_PURE_LOVE = ABILITIES.register("command_pure_love", CommandPureLove::new);

    public static RegistryObject<Ability> EMBER_INSECTS = ABILITIES.register("ember_insects", EmberInsects::new);
    public static RegistryObject<Ability> EMBER_INSECT_FLIGHT = ABILITIES.register("ember_insect_flight", EmberInsectFlight::new);
    public static RegistryObject<Ability> VOLCANO = ABILITIES.register("volcano", Volcano::new);
    public static RegistryObject<Ability> MAXIMUM_METEOR = ABILITIES.register("maximum_meteor", MaximumMeteor::new);
    public static RegistryObject<Ability> DISASTER_FLAMES = ABILITIES.register("disaster_flames", DisasterFlames::new);
    public static RegistryObject<Ability> FLAMETHROWER = ABILITIES.register("flamethrower", Flamethrower::new);
    public static RegistryObject<Ability> FIREBALL = ABILITIES.register("fireball", Fireball::new);
    public static RegistryObject<Ability> FIRE_BEAM = ABILITIES.register("fire_beam", FireBeam::new);
    public static RegistryObject<Ability> COFFIN_OF_THE_IRON_MOUNTAIN = ABILITIES.register("coffin_of_the_iron_mountain", CoffinOfTheIronMountain::new);

    public static RegistryObject<Ability> HORIZON_OF_THE_CAPTIVATING_SKANDHA = ABILITIES.register("horizon_of_the_captivating_skandha", HorizonOfTheCaptivatingSkandha::new);
    public static RegistryObject<Ability> DISASTER_TIDES = ABILITIES.register("disaster_tides", DisasterTides::new);
    public static RegistryObject<Ability> WATER_SHIELD = ABILITIES.register("water_shield", WaterShield::new);
    public static RegistryObject<Ability> DEATH_SWARM = ABILITIES.register("death_swarm", DeathSwarm::new);
    public static RegistryObject<Ability> FISH_SHIKIGAMI = ABILITIES.register("fish_shikigami", FishShikigami::new);
    public static RegistryObject<Ability> WATER_TORRENT = ABILITIES.register("water_torrent", WaterTorrent::new);
    public static RegistryObject<Ability> EEL_GRAPPLE = ABILITIES.register("eel_grapple", EelGrapple::new);

    public static RegistryObject<Ability> FOREST_PLATFORM = ABILITIES.register("forest_platform", ForestPlatform::new);
    public static RegistryObject<Ability> FOREST_SPIKES = ABILITIES.register("forest_spikes", ForestSpikes::new);
    public static RegistryObject<Ability> WOOD_SHIELD = ABILITIES.register("wood_shield", WoodShield::new);
    public static RegistryObject<Ability> CURSED_BUD = ABILITIES.register("cursed_bud", CursedBud::new);
    public static RegistryObject<Ability> FOREST_WAVE = ABILITIES.register("forest_wave", ForestWave::new);
    public static RegistryObject<Ability> FOREST_ROOTS = ABILITIES.register("forest_roots", ForestRoots::new);
    public static RegistryObject<Ability> FOREST_DASH = ABILITIES.register("forest_dash", ForestDash::new);
    public static RegistryObject<Ability> DISASTER_PLANT = ABILITIES.register("disaster_plant", DisasterPlant::new);
    public static RegistryObject<Ability> SHINING_SEA_OF_FLOWERS = ABILITIES.register("shining_sea_of_flowers", ShiningSeaOfFlowers::new);

    public static RegistryObject<Ability> IDLE_TRANSFIGURATION = ABILITIES.register("idle_transfiguration", IdleTransfiguration::new);
    public static RegistryObject<Ability> SOUL_DECIMATION = ABILITIES.register("soul_decimation", SoulDecimation::new);
    public static RegistryObject<Ability> SOUL_REINFORCEMENT = ABILITIES.register("soul_reinforcement", SoulReinforcement::new);
    public static RegistryObject<Ability> SOUL_RESTORATION = ABILITIES.register("soul_restoration", SoulRestoration::new);
    public static RegistryObject<Ability> ARM_BLADE = ABILITIES.register("arm_blade", ArmBlade::new);
    public static RegistryObject<Ability> GUN = ABILITIES.register("gun", Gun::new);
    public static RegistryObject<Ability> HORSE_LEGS = ABILITIES.register("horse_legs", HorseLegs::new);
    public static RegistryObject<Ability> WINGS = ABILITIES.register("wings", Wings::new);
    public static RegistryObject<Summon<?>> TRANSFIGURED_SOUL_SMALL = ABILITIES.register("transfigured_soul_small", TransfiguredSoulSmall::new);
    public static RegistryObject<Summon<?>> TRANSFIGURED_SOUL_NORMAL = ABILITIES.register("transfigured_soul_normal", TransfiguredSoulNormal::new);
    public static RegistryObject<Summon<?>> TRANSFIGURED_SOUL_LARGE = ABILITIES.register("transfigured_soul_large", TransfiguredSoulLarge::new);
    public static RegistryObject<Summon<?>> POLYMORPHIC_SOUl_ISOMER = ABILITIES.register("polymorphic_soul_isomer", PolymorphicSoulIsomer::new);
    public static RegistryObject<Ability> INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING = ABILITIES.register("instant_spirit_body_of_distorted_killing", InstantSpiritBodyOfDistortedKilling::new);
    public static RegistryObject<Ability> SELF_EMBODIMENT_OF_PERFECTION = ABILITIES.register("self_embodiment_of_perfection", SelfEmbodimentOfPerfection::new);

    public static RegistryObject<Ability> DASH = ABILITIES.register("dash", Dash::new);
    public static RegistryObject<Ability> PUNCH = ABILITIES.register("punch", Punch::new);
    public static RegistryObject<Ability> SLAM = ABILITIES.register("slam", Slam::new);
    public static RegistryObject<Ability> BARRAGE = ABILITIES.register("barrage", Barrage::new);
    public static RegistryObject<RCT1> RCT1 = ABILITIES.register("rct1", RCT1::new);
    public static RegistryObject<RCT1> RCT2 = ABILITIES.register("rct2", RCT2::new);
    public static RegistryObject<RCT1> RCT3 = ABILITIES.register("rct3", RCT3::new);
    public static RegistryObject<Ability> OUTPUT_RCT = ABILITIES.register("output_rct", OutputRCT::new);
    public static RegistryObject<Ability> HEAL = ABILITIES.register("heal", Heal::new);
    public static RegistryObject<Ability> DOMAIN_AMPLIFICATION = ABILITIES.register("domain_amplification", DomainAmplification::new);
    public static RegistryObject<Ability> SIMPLE_DOMAIN = ABILITIES.register("simple_domain", SimpleDomain::new);
    public static RegistryObject<Ability> QUICK_DRAW = ABILITIES.register("quick_draw", QuickDraw::new);
    public static RegistryObject<Ability> FALLING_BLOSSOM_EMOTION = ABILITIES.register("falling_blossom_emotion", FallingBlossomEmotion::new);
    public static RegistryObject<Ability> CURSED_ENERGY_FLOW = ABILITIES.register("cursed_energy_flow", CursedEnergyFlow::new);
    public static RegistryObject<Ability> CURSED_ENERGY_SHIELD = ABILITIES.register("cursed_energy_shield", CursedEnergyShield::new);
    public static RegistryObject<Ability> LIGHTNING = ABILITIES.register("lightning", Lightning::new);
    public static RegistryObject<Ability> DISCHARGE = ABILITIES.register("discharge", Discharge::new);
    public static RegistryObject<Ability> ZERO_POINT_TWO_SECOND_DOMAIN_EXPANSION = ABILITIES.register("zero_point_two_second_domain_expansion", ZeroPointTwoSecondDomainExpansion::new);
    public static RegistryObject<Ability> SWITCH = ABILITIES.register("switch", Switch::new);
    public static RegistryObject<Ability> CURSED_ENERGY_BOMB = ABILITIES.register("cursed_energy_bomb", CursedEnergyBomb::new);
    public static RegistryObject<Ability> CURSED_ENERGY_BLAST = ABILITIES.register("cursed_energy_blast", CursedEnergyBlast::new);


    public static RegistryObject<Ability> SWITCH_MODE = ABILITIES.register("switch_mode", SwitchMode::new);
    public static RegistryObject<Ability> RELEASE_SHIKIGAMI = ABILITIES.register("release_shikigami", ReleaseShikigami::new);
    public static RegistryObject<Ability> SHADOW_STORAGE = ABILITIES.register("shadow_storage", ShadowStorage::new);
    public static RegistryObject<Ability> SHADOW_TRAVEL = ABILITIES.register("shadow_travel", ShadowTravel::new);
    public static RegistryObject<Ability> NUE_LIGHTNING = ABILITIES.register("nue_lightning", NueLightning::new);
    public static RegistryObject<Ability> NUE_TOTALITY_LIGHTNING = ABILITIES.register("nue_totality_lightning", NueTotalityLightning::new);
    public static RegistryObject<Ability> PIERCING_WATER = ABILITIES.register("piercing_water", PiercingWater::new);
    public static RegistryObject<Summon<?>> WHEEL = ABILITIES.register("wheel", Wheel::new);
    public static RegistryObject<Ability> GREAT_SERPENT_GRAB = ABILITIES.register("great_serpent_grab", GreatSerpentGrab::new);
    public static RegistryObject<Summon<?>> MAHORAGA = ABILITIES.register("mahoraga", Mahoraga::new);
    public static RegistryObject<Summon<?>> DIVINE_DOGS = ABILITIES.register("divine_dogs", DivineDogs::new);
    public static RegistryObject<Summon<?>> DIVINE_DOG_TOTALITY = ABILITIES.register("divine_dog_totality", DivineDogTotality::new);
    public static RegistryObject<Summon<?>> TOAD = ABILITIES.register("toad", Toad::new);
    public static RegistryObject<Summon<?>> TOAD_FUSION = ABILITIES.register("toad_fusion", ToadFusion::new);
    public static RegistryObject<Summon<?>> RABBIT_ESCAPE = ABILITIES.register("rabbit_escape", RabbitEscape::new);
    public static RegistryObject<Summon<?>> NUE = ABILITIES.register("nue", Nue::new);
    public static RegistryObject<Summon<?>> NUE_TOTALITY = ABILITIES.register("nue_totality", NueTotality::new);
    public static RegistryObject<Summon<?>> GREAT_SERPENT = ABILITIES.register("great_serpent", GreatSerpent::new);
    public static RegistryObject<Summon<?>> MAX_ELEPHANT = ABILITIES.register("max_elephant", MaxElephant::new);
    public static RegistryObject<Summon<?>> TRANQUIL_DEER = ABILITIES.register("tranquil_deer", TranquilDeer::new);
    public static RegistryObject<Summon<?>> PIERCING_BULL = ABILITIES.register("piercing_bull", PiercingBull::new);
    public static RegistryObject<Summon<?>> AGITO = ABILITIES.register("agito", Agito::new);

    public static RegistryObject<Ability> CHIMERA_SHADOW_GARDEN = ABILITIES.register("chimera_shadow_garden", ChimeraShadowGarden::new);

    public static RegistryObject<Ability> SHOOT_PURE_LOVE = ABILITIES.register("shoot_pure_love", ShootPureLove::new);
    public static RegistryObject<Ability> CYCLOPS_SMASH = ABILITIES.register("cyclops_smash", CyclopsSmash::new);
    public static RegistryObject<Ability> WATER = ABILITIES.register("water", Water::new);
    public static RegistryObject<Ability> SCISSORS = ABILITIES.register("scissors", Scissors::new);
    public static RegistryObject<Ability> SKY_STRIKE = ABILITIES.register("sky_strike", SkyStrike::new);
    public static RegistryObject<Ability> BLUE_FIRE = ABILITIES.register("blue_fire", BlueFire::new);

    public static RegistryObject<Ability> CURSE_ABSORPTION = ABILITIES.register("curse_absorption", CurseAbsorption::new);
    public static RegistryObject<Ability> RELEASE_CURSE = ABILITIES.register("release_curse", ReleaseCurse::new);
    public static RegistryObject<Ability> RELEASE_CURSES = ABILITIES.register("release_curses", ReleaseCurses::new);
    public static RegistryObject<Ability> SUMMON_ALL = ABILITIES.register("summon_all", SummonAll::new);
    public static RegistryObject<Ability> ENHANCE_CURSE = ABILITIES.register("enhance_curse", EnhanceCurse::new);
    public static RegistryObject<Ability> MAXIMUM_UZUMAKI = ABILITIES.register("maximum_uzumaki", MaximumUzumaki::new);
    public static RegistryObject<Ability> MINI_UZUMAKI = ABILITIES.register("mini_uzumaki", MiniUzumaki::new);
    public static RegistryObject<Ability> WORM_CURSE_GRAB = ABILITIES.register("worm_curse_grab", WormCurseGrab::new);

    public static RegistryObject<Ability> DONT_MOVE = ABILITIES.register("dont_move", DontMove::new);
    public static RegistryObject<Ability> GET_CRUSHED = ABILITIES.register("get_crushed", GetCrushed::new);
    public static RegistryObject<Ability> BLAST_AWAY = ABILITIES.register("blast_away", BlastAway::new);
    public static RegistryObject<Ability> EXPLODE = ABILITIES.register("explode", Explode::new);
    public static RegistryObject<Ability> DIE = ABILITIES.register("die", Die::new);

    public static RegistryObject<Ability> SWAP_SELF = ABILITIES.register("swap_self", SwapSelf::new);
    public static RegistryObject<Ability> SWAP_OTHERS = ABILITIES.register("swap_others", SwapOthers::new);
    public static RegistryObject<Ability> FEINT = ABILITIES.register("feint", Feint::new);
    public static RegistryObject<Ability> CE_THROW = ABILITIES.register("ce_throw", CEThrow::new);
    public static RegistryObject<Ability> ITEM_SWAP = ABILITIES.register("item_swap", ItemSwap::new);
    public static RegistryObject<Ability> SHUFFLE = ABILITIES.register("shuffle", Shuffle::new);

    public static RegistryObject<Ability> PROJECTION_SORCERY = ABILITIES.register("projection_sorcery", ProjectionSorcery::new);
    public static RegistryObject<Ability> TWENTY_FOUR_FRAME_RULE = ABILITIES.register("twenty_four_frame_rule", TwentyFourFrameRule::new);
    public static RegistryObject<Ability> AIR_FRAME = ABILITIES.register("air_frame", AirFrame::new);
    public static RegistryObject<Ability> TIME_CELL_MOON_PALACE = ABILITIES.register("time_cell_moon_palace", TimeCellMoonPalace::new);

    public static String getName(Ability ability) {
        return getKey(ability).getPath();
    }

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

    public static Set<CursedTechnique> getTechniques(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return Set.of();
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getTechniques();
    }

    @Nullable
    public static CursedTechnique getTechnique(Ability ability) {
        for (CursedTechnique technique : CursedTechnique.values()) {
            if (List.of(technique.getAbilities()).contains(ability)) return technique;
        }
        return null;
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
            for (CursedTechnique technique : CursedTechnique.values()) {
                abilities.addAll(Arrays.asList(technique.getAbilities()));

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
            for (CursedTechnique technique : cap.getTechniques()) {
                abilities.addAll(Arrays.asList(technique.getAbilities()));
            }

            CursedTechnique technique = cap.getTechnique();

            if (technique != null && technique.getDomain() != null) {
                abilities.add(technique.getDomain());
            }
        }
        abilities.removeIf(ability -> !ability.isValid(owner) && !(owner instanceof ISorcerer sorcerer && sorcerer.getCustom().contains(ability)));

        return new ArrayList<>(abilities);
    }
}