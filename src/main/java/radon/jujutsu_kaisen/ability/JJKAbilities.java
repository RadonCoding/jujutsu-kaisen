package radon.jujutsu_kaisen.ability;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
import radon.jujutsu_kaisen.ability.ai.max_elephant.Water;
import radon.jujutsu_kaisen.ability.ai.nue_totality.NueTotalityLightning;
import radon.jujutsu_kaisen.ability.ai.rika.ShootPureLove;
import radon.jujutsu_kaisen.ability.ai.scissor.Scissors;
import radon.jujutsu_kaisen.ability.ai.scissor.TeleportRandom;
import radon.jujutsu_kaisen.ability.ai.scissor.TeleportTowards;
import radon.jujutsu_kaisen.ability.ai.zomba_curse.SkyStrike;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.ability.boogie_woogie.BoogieWoogie;
import radon.jujutsu_kaisen.ability.boogie_woogie.Feint;
import radon.jujutsu_kaisen.ability.curse_manipulation.*;
import radon.jujutsu_kaisen.ability.cursed_speech.*;
import radon.jujutsu_kaisen.ability.disaster_flames.*;
import radon.jujutsu_kaisen.ability.disaster_plants.ForestPlatform;
import radon.jujutsu_kaisen.ability.disaster_plants.ForestSpikes;
import radon.jujutsu_kaisen.ability.disaster_plants.WoodShield;
import radon.jujutsu_kaisen.ability.disaster_tides.*;
import radon.jujutsu_kaisen.ability.dismantle_and_cleave.*;
import radon.jujutsu_kaisen.ability.divergent_fist.DivergentFist;
import radon.jujutsu_kaisen.ability.limitless.*;
import radon.jujutsu_kaisen.ability.misc.*;
import radon.jujutsu_kaisen.ability.rika.CommandPureLove;
import radon.jujutsu_kaisen.ability.rika.Copy;
import radon.jujutsu_kaisen.ability.rika.Rika;
import radon.jujutsu_kaisen.ability.ten_shadows.ChimeraShadowGarden;
import radon.jujutsu_kaisen.ability.ten_shadows.ShadowStorage;
import radon.jujutsu_kaisen.ability.ten_shadows.SwitchMode;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.NueLightning;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.PiercingWater;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.Wheel;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.*;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class JJKAbilities {
    public static DeferredRegister<Ability> ABILITIES = DeferredRegister.create(
            new ResourceLocation(JujutsuKaisen.MOD_ID, "ability"), JujutsuKaisen.MOD_ID);
    public static Supplier<IForgeRegistry<Ability>> ABILITY_REGISTRY =
            ABILITIES.makeRegistry(RegistryBuilder::new);

    public static RegistryObject<Ability> INFINITY = ABILITIES.register("infinity", Infinity::new);
    public static RegistryObject<Ability> RED = ABILITIES.register("red", Red::new);
    public static RegistryObject<Ability> MAXIMUM_RED = ABILITIES.register("maximum_red", MaximumRed::new);
    public static RegistryObject<Ability> BLUE = ABILITIES.register("blue", Blue::new);
    public static RegistryObject<Ability> MAXIMUM_BLUE = ABILITIES.register("maximum_blue", MaximumBlue::new);
    public static RegistryObject<Ability> BLUE_FISTS = ABILITIES.register("blue_fists", BlueFists::new);
    public static RegistryObject<Ability> HOLLOW_PURPLE = ABILITIES.register("hollow_purple", HollowPurple::new);
    public static RegistryObject<Ability> MAXIMUM_HOLLOW_PURPLE = ABILITIES.register("maximum_hollow_purple", MaximumHollowPurple::new);
    public static RegistryObject<Ability> TELEPORT = ABILITIES.register("teleport", Teleport::new);
    public static RegistryObject<Ability> UNLIMITED_VOID = ABILITIES.register("unlimited_void", UnlimitedVoid::new);

    public static RegistryObject<Ability> DISMANTLE = ABILITIES.register("dismantle", Dismantle::new);
    public static RegistryObject<Ability> CLEAVE = ABILITIES.register("cleave", Cleave::new);
    public static RegistryObject<Ability> SPIDERWEB = ABILITIES.register("spiderweb", Spiderweb::new);
    public static RegistryObject<Ability> DISMANTLE_BARRAGE = ABILITIES.register("dismantle_barrage", DismantleBarrage::new);
    public static RegistryObject<Ability> FIRE_ARROW = ABILITIES.register("fire_arrow", FireArrow::new);
    public static RegistryObject<Ability> MALEVOLENT_SHRINE = ABILITIES.register("malevolent_shrine", MalevolentShrine::new);

    public static RegistryObject<Summon<?>> RIKA = ABILITIES.register("rika", Rika::new);
    public static RegistryObject<Ability> COPY = ABILITIES.register("copy", Copy::new);
    public static RegistryObject<Ability> COMMAND_PURE_LOVE = ABILITIES.register("command_pure_love", CommandPureLove::new);

    public static RegistryObject<Ability> EMBER_INSECTS = ABILITIES.register("ember_insects", EmberInsects::new);
    public static RegistryObject<Ability> VOLCANO = ABILITIES.register("volcano", Volcano::new);
    public static RegistryObject<Ability> MAXIMUM_METEOR = ABILITIES.register("maximum_meteor", MaximumMeteor::new);
    public static RegistryObject<Ability> DISASTER_FLAMES = ABILITIES.register("disaster_flames", DisasterFlames::new);
    public static RegistryObject<Ability> FLAMETHROWER = ABILITIES.register("flamethrower", Flamethrower::new);
    public static RegistryObject<Ability> FIREBALL = ABILITIES.register("fireball", Fireball::new);
    public static RegistryObject<Ability> COFFIN_OF_THE_IRON_MOUNTAIN = ABILITIES.register("coffin_of_the_iron_mountain", CoffinOfTheIronMountain::new);

    public static RegistryObject<Ability> HORIZON_OF_THE_CAPTIVATING_SKANDHA = ABILITIES.register("horizon_of_the_captivating_skandha", HorizonOfTheCaptivatingSkandha::new);
    public static RegistryObject<Ability> DISASTER_TIDES = ABILITIES.register("disaster_tides", DisasterTides::new);
    public static RegistryObject<Ability> WATER_SHIELD = ABILITIES.register("water_shield", WaterShield::new);
    public static RegistryObject<Ability> FISH_SHIKIGAMI = ABILITIES.register("fish_shikigami", FishShikigami::new);
    public static RegistryObject<Ability> WATER_TORRENT = ABILITIES.register("water_torrent", WaterTorrent::new);

    public static RegistryObject<Ability> FOREST_PLATFORM = ABILITIES.register("forest_platform", ForestPlatform::new);
    public static RegistryObject<Ability> FOREST_SPIKES = ABILITIES.register("forest_spikes", ForestSpikes::new);
    public static RegistryObject<Ability> WOOD_SHIELD = ABILITIES.register("wood_shield", WoodShield::new);

    public static RegistryObject<Ability> DASH = ABILITIES.register("dash", Dash::new);
    public static RegistryObject<Ability> SMASH = ABILITIES.register("smash", Smash::new);
    public static RegistryObject<Ability> AIR_PUNCH = ABILITIES.register("air_punch", AirPunch::new);
    public static RegistryObject<Ability> BARRAGE = ABILITIES.register("barrage", Barrage::new);
    public static RegistryObject<Ability> RCT = ABILITIES.register("rct", RCT::new);
    public static RegistryObject<Ability> SHOOT_RCT = ABILITIES.register("shoot_rct", ShootRCT::new);
    public static RegistryObject<Ability> HEAL_RCT = ABILITIES.register("heal_rct", HealRCT::new);
    public static RegistryObject<Ability> HEAL = ABILITIES.register("heal", Heal::new);
    public static RegistryObject<Ability> DOMAIN_AMPLIFICATION = ABILITIES.register("domain_amplification", DomainAmplification::new);
    public static RegistryObject<Ability> SIMPLE_DOMAIN = ABILITIES.register("simple_domain", SimpleDomain::new);
    public static RegistryObject<Ability> WATER_WALKING = ABILITIES.register("water_walking", WaterWalking::new);
    public static RegistryObject<Ability> CURSED_ENERGY_FLOW = ABILITIES.register("cursed_energy_flow", CursedEnergyFlow::new);
    public static RegistryObject<Ability> LIGHTNING = ABILITIES.register("lightning", Lightning::new);


    public static RegistryObject<Summon<?>> MAHORAGA = ABILITIES.register("mahoraga", Mahoraga::new);
    public static RegistryObject<Summon<?>> DIVINE_DOGS = ABILITIES.register("divine_dogs", DivineDogs::new);
    public static RegistryObject<Summon<?>> DIVINE_DOG_TOTALITY = ABILITIES.register("divine_dog_totality", DivineDogTotality::new);
    public static RegistryObject<Summon<?>> TOAD = ABILITIES.register("toad", Toad::new);
    public static RegistryObject<Summon<?>> TOAD_TOTALITY = ABILITIES.register("toad_totality", ToadTotality::new);
    public static RegistryObject<Summon<?>> RABBIT_ESCAPE = ABILITIES.register("rabbit_escape", RabbitEscape::new);
    public static RegistryObject<Summon<?>> NUE = ABILITIES.register("nue", Nue::new);
    public static RegistryObject<Summon<?>> NUE_TOTALITY = ABILITIES.register("nue_totality", NueTotality::new);
    public static RegistryObject<Summon<?>> GREAT_SERPENT = ABILITIES.register("great_serpent", GreatSerpent::new);
    public static RegistryObject<Summon<?>> MAX_ELEPHANT = ABILITIES.register("max_elephant", MaxElephant::new);
    public static RegistryObject<Summon<?>> TRANQUIL_DEER = ABILITIES.register("tranquil_deer", TranquilDeer::new);
    public static RegistryObject<Summon<?>> PIERCING_BULL = ABILITIES.register("piercing_bull", PiercingBull::new);
    public static RegistryObject<Summon<?>> AGITO = ABILITIES.register("agito", Agito::new);
    public static RegistryObject<Ability> SWITCH_MODE = ABILITIES.register("switch_mode", SwitchMode::new);
    public static RegistryObject<Ability> RELEASE_SHIKIGAMI = ABILITIES.register("release_shikigami", ReleaseShikigami::new);
    public static RegistryObject<Ability> SHADOW_STORAGE = ABILITIES.register("shadow_storage", ShadowStorage::new);
    public static RegistryObject<Ability> CHIMERA_SHADOW_GARDEN = ABILITIES.register("chimera_shadow_garden", ChimeraShadowGarden::new);

    public static RegistryObject<Ability> NUE_LIGHTNING = ABILITIES.register("nue_lightning", NueLightning::new);
    public static RegistryObject<Ability> NUE_TOTALITY_LIGHTNING = ABILITIES.register("nue_totality_lightning", NueTotalityLightning::new);
    public static RegistryObject<Ability> PIERCING_WATER = ABILITIES.register("piercing_water", PiercingWater::new);
    public static RegistryObject<Summon<?>> WHEEL = ABILITIES.register("wheel", Wheel::new);

    public static RegistryObject<Ability> DIVERGENT_FIST = ABILITIES.register("divergent_fist", DivergentFist::new);

    public static RegistryObject<Ability> SHOOT_PURE_LOVE = ABILITIES.register("shoot_pure_love", ShootPureLove::new);
    public static RegistryObject<Ability> CYCLOPS_SMASH = ABILITIES.register("cyclops_smash", CyclopsSmash::new);
    public static RegistryObject<Ability> WATER = ABILITIES.register("water", Water::new);
    public static RegistryObject<Ability> SCISSORS = ABILITIES.register("scissors", Scissors::new);
    public static RegistryObject<Ability> SKY_STRIKE = ABILITIES.register("sky_strike", SkyStrike::new);
    public static RegistryObject<Ability> TELEPORT_TOWARDS = ABILITIES.register("teleport_towards", TeleportTowards::new);
    public static RegistryObject<Ability> TELEPORT_RANDOM = ABILITIES.register("teleport_random", TeleportRandom::new);

    public static RegistryObject<Ability> ABSORB_CURSE = ABILITIES.register("absorb_curse", AbsorbCurse::new);
    public static RegistryObject<Ability> ABSORB_TECHNIQUE = ABILITIES.register("absorb_technique", AbsorbTechnique::new);
    public static RegistryObject<Ability> RELEASE_CURSE = ABILITIES.register("release_curse", ReleaseCurse::new);
    public static RegistryObject<Ability> RELEASE_CURSES = ABILITIES.register("release_curses", ReleaseCurses::new);
    public static RegistryObject<Ability> MAXIMUM_UZUMAKI = ABILITIES.register("maximum_uzumaki", MaximumUzumaki::new);
    public static RegistryObject<Ability> MINI_UZUMAKI = ABILITIES.register("mini_uzumaki", MiniUzumaki::new);

    public static RegistryObject<Ability> DONT_MOVE = ABILITIES.register("dont_move", DontMove::new);
    public static RegistryObject<Ability> GET_CRUSHED = ABILITIES.register("get_crushed", GetCrushed::new);
    public static RegistryObject<Ability> BLAST_AWAY = ABILITIES.register("blast_away", BlastAway::new);
    public static RegistryObject<Ability> EXPLODE = ABILITIES.register("explode", Explode::new);
    public static RegistryObject<Ability> DIE = ABILITIES.register("die", Die::new);

    public static RegistryObject<Ability> BOOGIE_WOOGIE = ABILITIES.register("boogie_woogie", BoogieWoogie::new);
    public static RegistryObject<Ability> FEINT = ABILITIES.register("feint", Feint::new);

    public static ResourceLocation getKey(Ability ability) {
        return JJKAbilities.ABILITY_REGISTRY.get().getKey(ability);
    }

    public static Ability getValue(ResourceLocation key) {
        return JJKAbilities.ABILITY_REGISTRY.get().getValue(key);
    }

    public static boolean hasToggled(LivingEntity owner, Ability ability) {
        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.hasToggled(ability)));
        return result.get();
    }

    public static float getCurseCost(SorcererGrade grade) {
        return 50.0F * grade.getPower();
    }

    public static void summonCurse(LivingEntity owner, EntityType<?> type) {
        if (owner.hasEffect(JJKEffects.UNLIMITED_VOID.get()) || JJKAbilities.hasToggled(owner, JJKAbilities.DOMAIN_AMPLIFICATION.get())) return;

        Registry<EntityType<?>> registry = owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (!cap.hasCurse(registry, type)) return;

            if (type.create(owner.level) instanceof CursedSpirit curse) {
                float cost = getCurseCost(curse.getGrade());

                if (!(owner instanceof Player player) || !player.getAbilities().instabuild) {
                    if (cap.getEnergy() < cost) {
                        return;
                    }
                    cap.useEnergy(cost);
                }

                Vec3 pos = owner.position().subtract(owner.getLookAngle()
                        .multiply(curse.getBbWidth(), 0.0D, curse.getBbWidth()));
                curse.moveTo(pos.x(), pos.y(), pos.z(), owner.getYRot(), owner.getXRot());
                curse.setTame(true);
                curse.setOwner(owner);
                owner.level.addFreshEntity(curse);

                cap.addSummon(curse);

                cap.removeCurse(registry, type);

                if (owner instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }
            }
        });
    }

    @Nullable
    public static CursedTechnique getTechnique(LivingEntity owner) {
        AtomicReference<CursedTechnique> result = new AtomicReference<>(null);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.getTechnique()));
        return result.get();
    }

    @Nullable
    public static CursedTechnique getTechnique(Ability ability) {
        for (CursedTechnique technique : CursedTechnique.values()) {
            if (List.of(technique.getAbilities()).contains(ability)) return technique;
        }
        return null;
    }

    public static SorcererGrade getGrade(LivingEntity owner) {
        AtomicReference<SorcererGrade> result = new AtomicReference<>();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.getGrade()));
        return result.get();
    }

    public static List<Ability> getToggled(LivingEntity owner) {
        List<Ability> toggled = new ArrayList<>();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                toggled.addAll(cap.getToggled()));
        return toggled;
    }

    public static boolean hasTamed(LivingEntity owner, EntityType<?> type) {
        for (RegistryObject<Ability> ability : ABILITIES.getEntries()) {
            if (!(ability.get() instanceof Summon<?> summon)) continue;
            if (!summon.getTypes().contains(type)) continue;
            return summon.isTamed(owner);
        }
        return false;
    }

    public static boolean isChanneling(LivingEntity owner, Ability ability) {
        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            result.set(cap.isChanneling(ability));
        });
        return result.get();
    }

    public static boolean hasTrait(LivingEntity owner, Trait trait) {
        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.hasTrait(trait)));
        return result.get();
    }

    public static List<Ability> getAbilities(LivingEntity owner) {
        Set<Ability> abilities = new LinkedHashSet<>();

        if (owner instanceof ISorcerer sorcerer) {
            abilities.addAll(sorcerer.getCustom());
        }

        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return new ArrayList<>(abilities);
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        abilities.add(JJKAbilities.AIR_PUNCH.get());
        abilities.add(JJKAbilities.BARRAGE.get());
        abilities.add(JJKAbilities.DASH.get());

        if (!cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            abilities.add(JJKAbilities.SMASH.get());
            abilities.add(JJKAbilities.WATER_WALKING.get());
            abilities.add(JJKAbilities.CURSED_ENERGY_FLOW.get());
            abilities.add(JJKAbilities.LIGHTNING.get());

            abilities.add(JJKAbilities.HEAL.get());
            abilities.add(JJKAbilities.RCT.get());

            abilities.add(JJKAbilities.SIMPLE_DOMAIN.get());
            abilities.add(JJKAbilities.DOMAIN_AMPLIFICATION.get());

            CursedTechnique technique = cap.getTechnique();

            if (technique != null) {
                Ability domain = technique.getDomain();

                if (domain != null) {
                    abilities.add(domain);
                }
                abilities.addAll(Arrays.asList(technique.getAbilities()));
            }

            CursedTechnique additional = cap.getAdditional();
            if (additional != null) abilities.addAll(Arrays.asList(additional.getAbilities()));

            CursedTechnique copied = cap.getCurrentCopied();
            if (copied != null) abilities.addAll(Arrays.asList(copied.getAbilities()));

            CursedTechnique absorbed = cap.getCurrentAbsorbed();
            if (absorbed != null) abilities.addAll(Arrays.asList(absorbed.getAbilities()));
        }
        abilities.removeIf(ability -> !ability.isUnlocked(owner) && !(owner instanceof ISorcerer sorcerer && sorcerer.getCustom().contains(ability)));

        return new ArrayList<>(abilities);
    }
}