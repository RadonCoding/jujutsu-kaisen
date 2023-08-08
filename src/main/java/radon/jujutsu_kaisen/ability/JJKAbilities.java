package radon.jujutsu_kaisen.ability;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.disaster_flames.*;
import radon.jujutsu_kaisen.ability.limitless.*;
import radon.jujutsu_kaisen.ability.misc.*;
import radon.jujutsu_kaisen.ability.rika.Copy;
import radon.jujutsu_kaisen.ability.rika.PureLove;
import radon.jujutsu_kaisen.ability.rika.Rika;
import radon.jujutsu_kaisen.ability.dismantle_and_cleave.Cleave;
import radon.jujutsu_kaisen.ability.dismantle_and_cleave.Dismantle;
import radon.jujutsu_kaisen.ability.dismantle_and_cleave.FireArrow;
import radon.jujutsu_kaisen.ability.dismantle_and_cleave.MalevolentShrine;
import radon.jujutsu_kaisen.ability.ten_shadows.DivineDogs;
import radon.jujutsu_kaisen.ability.ten_shadows.Mahoraga;
import radon.jujutsu_kaisen.ability.ten_shadows.Wheel;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class JJKAbilities {
    public static DeferredRegister<Ability> ABILITIES = DeferredRegister.create(
            new ResourceLocation(JujutsuKaisen.MOD_ID, "ability"), JujutsuKaisen.MOD_ID);
    public static Supplier<IForgeRegistry<Ability>> ABILITY_REGISTRY =
            ABILITIES.makeRegistry(RegistryBuilder::new);

    public static RegistryObject<Ability> INFINITY = ABILITIES.register("infinity", Infinity::new);
    public static RegistryObject<Ability> RED = ABILITIES.register("red", Red::new);
    public static RegistryObject<Ability> BLUE = ABILITIES.register("blue", Blue::new);
    public static RegistryObject<Ability> MAXIMUM_BLUE = ABILITIES.register("maximum_blue", MaximumBlue::new);
    public static RegistryObject<Ability> HOLLOW_PURPLE = ABILITIES.register("hollow_purple", HollowPurple::new);
    public static RegistryObject<Ability> MAXIMUM_HOLLOW_PURPLE = ABILITIES.register("maximum_hollow_purple", MaximumHollowPurple::new);
    public static RegistryObject<Ability> TELEPORT = ABILITIES.register("teleport", Teleport::new);
    public static RegistryObject<Ability> UNLIMITED_VOID = ABILITIES.register("unlimited_void", UnlimitedVoid::new);

    public static RegistryObject<Ability> DISMANTLE = ABILITIES.register("dismantle", Dismantle::new);
    public static RegistryObject<Ability> CLEAVE = ABILITIES.register("cleave", Cleave::new);
    public static RegistryObject<Ability> FIRE_ARROW = ABILITIES.register("fire_arrow", FireArrow::new);
    public static RegistryObject<Ability> MALEVOLENT_SHRINE = ABILITIES.register("malevolent_shrine", MalevolentShrine::new);

    public static RegistryObject<Ability> RIKA = ABILITIES.register("rika", Rika::new);
    public static RegistryObject<Ability> PURE_LOVE = ABILITIES.register("pure_love", PureLove::new);
    public static RegistryObject<Ability> COPY = ABILITIES.register("copy", Copy::new);

    public static RegistryObject<Ability> EMBER_INSECTS = ABILITIES.register("ember_insects", EmberInsects::new);
    public static RegistryObject<Ability> VOLCANO = ABILITIES.register("volcano", Volcano::new);
    public static RegistryObject<Ability> MAXIMUM_METEOR = ABILITIES.register("maximum_meteor", MaximumMeteor::new);
    public static RegistryObject<Ability> DISASTER_FLAMES = ABILITIES.register("disaster_flames", DisasterFlames::new);
    public static RegistryObject<Ability> COFFIN_OF_IRON_MOUNTAIN = ABILITIES.register("coffin_of_iron_mountain", CoffinOfIronMountain::new);

    public static RegistryObject<Ability> DASH = ABILITIES.register("dash", Dash::new);
    public static RegistryObject<Ability> SMASH = ABILITIES.register("smash", Smash::new);
    public static RegistryObject<Ability> AIR_PUNCH = ABILITIES.register("air_punch", AirPunch::new);
    public static RegistryObject<Ability> UPPERCUT = ABILITIES.register("uppercut", Uppercut::new);
    public static RegistryObject<Ability> RCT = ABILITIES.register("rct", radon.jujutsu_kaisen.ability.misc.RCT::new);
    public static RegistryObject<Ability> HEAL = ABILITIES.register("heal", Heal::new);
    public static RegistryObject<Ability> DOMAIN_AMPLIFICATION = ABILITIES.register("domain_amplification", DomainAmplification::new);
    public static RegistryObject<Ability> SIMPLE_DOMAIN = ABILITIES.register("simple_domain", SimpleDomain::new);

    public static RegistryObject<Ability> MAHORAGA = ABILITIES.register("mahoraga", Mahoraga::new);
    public static RegistryObject<Ability> DIVINE_DOGS = ABILITIES.register("divine_dogs", DivineDogs::new);

    public static RegistryObject<Ability> WHEEL = ABILITIES.register("wheel", Wheel::new);

    public static ResourceLocation getKey(Ability ability) {
        return JJKAbilities.ABILITY_REGISTRY.get().getKey(ability);
    }

    public static Ability getValue(ResourceLocation key) {
        return JJKAbilities.ABILITY_REGISTRY.get().getValue(key);
    }

    public static boolean hasToggled(LivingEntity owner, Ability ability) {
        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            result.set(cap.hasToggled(ability));
        });
        return result.get();
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
        List<Ability> abilities = new ArrayList<>();

        if (owner instanceof RikaEntity) abilities.add(JJKAbilities.PURE_LOVE.get());
        if (owner instanceof MahoragaEntity) abilities.add(JJKAbilities.WHEEL.get());

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            abilities.add(JJKAbilities.AIR_PUNCH.get());
            abilities.add(JJKAbilities.UPPERCUT.get());

            if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                abilities.add(JJKAbilities.DASH.get());
            } else {
                abilities.add(JJKAbilities.SMASH.get());

                if (cap.isCurse()) {
                    abilities.add(JJKAbilities.HEAL.get());
                } else if (cap.hasTrait(Trait.REVERSE_CURSED_TECHNIQUE)) {
                    abilities.add(JJKAbilities.RCT.get());
                }

                if (cap.hasTrait(Trait.SIMPLE_DOMAIN)) abilities.add(JJKAbilities.SIMPLE_DOMAIN.get());

                CursedTechnique technique = cap.getTechnique();

                if (technique != null) {
                    if (cap.hasTrait(Trait.DOMAIN_EXPANSION)) {
                        abilities.add(JJKAbilities.DOMAIN_AMPLIFICATION.get());

                        Ability domain = technique.getDomain();

                        if (domain != null) {
                            abilities.add(domain);
                        }
                    }
                    abilities.addAll(Arrays.asList(technique.getAbilities()));
                }

                CursedTechnique copied = cap.getCopied();

                if (copied != null) {
                    Ability domain = copied.getDomain();

                    if (domain != null) {
                        abilities.add(copied.getDomain());
                    }
                    abilities.addAll(Arrays.asList(copied.getAbilities()));
                }

                if (cap.hasTamed(owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE), JJKEntities.MAHORAGA.get())) {
                    abilities.add(JJKAbilities.WHEEL.get());
                }
            }
        });
        abilities.removeIf(ability -> !ability.isUnlocked(owner));
        return abilities;
    }
}