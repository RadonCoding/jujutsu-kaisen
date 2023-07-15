package radon.jujutsu_kaisen.ability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.gojo.*;
import radon.jujutsu_kaisen.ability.misc.*;
import radon.jujutsu_kaisen.ability.yuta.Copy;
import radon.jujutsu_kaisen.ability.yuta.PureLove;
import radon.jujutsu_kaisen.ability.sukuna.Cleave;
import radon.jujutsu_kaisen.ability.sukuna.Dismantle;
import radon.jujutsu_kaisen.ability.sukuna.FireArrow;
import radon.jujutsu_kaisen.ability.sukuna.MalevolentShrine;
import radon.jujutsu_kaisen.ability.yuta.Rika;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
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
    public static RegistryObject<Ability> HOLLOW_PURPLE = ABILITIES.register("hollow_purple", HollowPurple::new);
    public static RegistryObject<Ability> TELEPORT = ABILITIES.register("teleport", Teleport::new);
    public static RegistryObject<Ability> UNLIMITED_VOID = ABILITIES.register("unlimited_void", UnlimitedVoid::new);

    public static RegistryObject<Ability> DISMANTLE = ABILITIES.register("dismantle", Dismantle::new);
    public static RegistryObject<Ability> CLEAVE = ABILITIES.register("cleave", Cleave::new);
    public static RegistryObject<Ability> FIRE_ARROW = ABILITIES.register("fire_arrow", FireArrow::new);
    public static RegistryObject<Ability> MALEVOLENT_SHRINE = ABILITIES.register("malevolent_shrine", MalevolentShrine::new);

    public static RegistryObject<Ability> RIKA = ABILITIES.register("rika", Rika::new);
    public static RegistryObject<Ability> PURE_LOVE = ABILITIES.register("pure_love", PureLove::new);
    public static RegistryObject<Ability> COPY = ABILITIES.register("copy", Copy::new);

    public static RegistryObject<Ability> DASH = ABILITIES.register("dash", Dash::new);
    public static RegistryObject<Ability> SMASH = ABILITIES.register("smash", Smash::new);
    public static RegistryObject<Ability> RCT = ABILITIES.register("rct", radon.jujutsu_kaisen.ability.misc.RCT::new);
    public static RegistryObject<Ability> HEAL = ABILITIES.register("heal", Heal::new);
    public static RegistryObject<Ability> DOMAIN_AMPLIFICATION = ABILITIES.register("domain_amplification", DomainAmplification::new);
    public static RegistryObject<Ability> SIMPLE_DOMAIN = ABILITIES.register("simple_domain", SimpleDomain::new);

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

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
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
                    abilities.addAll(Arrays.asList(copied.getAbilities()));
                }
            }
        });
        abilities.removeIf(ability -> !ability.isUnlocked(owner));
        return abilities;
    }
}