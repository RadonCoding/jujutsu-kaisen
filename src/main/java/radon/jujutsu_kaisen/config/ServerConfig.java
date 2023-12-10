package radon.jujutsu_kaisen.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;

import java.util.*;

public class ServerConfig {
    private static final Map<ResourceLocation, Float> CURSED_ENERGY_AMOUNTS = new LinkedHashMap<>();

    static {
        CURSED_ENERGY_AMOUNTS.put(JJKEntities.RIKA.getId(), Float.POSITIVE_INFINITY);
        CURSED_ENERGY_AMOUNTS.put(JJKEntities.MAHORAGA.getId(), 2000.0F);
    }

    private static final Map<SorcererGrade, Float> REQUIRED_EXPERIENCE = new LinkedHashMap<>();

    static {
        REQUIRED_EXPERIENCE.put(SorcererGrade.GRADE_4, 0.0F);
        REQUIRED_EXPERIENCE.put(SorcererGrade.GRADE_3, 100.0F);
        REQUIRED_EXPERIENCE.put(SorcererGrade.SEMI_GRADE_2, 300.0F);
        REQUIRED_EXPERIENCE.put(SorcererGrade.GRADE_2, 500.0F);
        REQUIRED_EXPERIENCE.put(SorcererGrade.SEMI_GRADE_1, 1000.0F);
        REQUIRED_EXPERIENCE.put(SorcererGrade.GRADE_1, 1500.0F);
        REQUIRED_EXPERIENCE.put(SorcererGrade.SPECIAL_GRADE_1, 2000.0F);
        REQUIRED_EXPERIENCE.put(SorcererGrade.SPECIAL_GRADE, 2500.0F);
    }

    private static final Map<ResourceLocation, Float> EXPERIENCE_MULTIPLIERS = new LinkedHashMap<>();

    static {
        EXPERIENCE_MULTIPLIERS.put(JJKEntities.SATORU_GOJO.getId(), 3.0F);
        EXPERIENCE_MULTIPLIERS.put(JJKEntities.YUTA_OKKOTSU.getId(), 2.0F);
        EXPERIENCE_MULTIPLIERS.put(JJKEntities.TOJI_FUSHIGURO.getId(), 2.0F);

        EXPERIENCE_MULTIPLIERS.put(JJKEntities.JOGO.getId(), 2.0F);
        EXPERIENCE_MULTIPLIERS.put(JJKEntities.HANAMI.getId(), 2.0F);
        EXPERIENCE_MULTIPLIERS.put(JJKEntities.DAGON.getId(), 2.0F);
    }

    public final ForgeConfigSpec.ConfigValue<List<? extends String>> cursedEnergyAmounts;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> experienceMultipliers;
    public final ForgeConfigSpec.DoubleValue cursedEnergyAmount;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> requiredExperience;
    public final ForgeConfigSpec.DoubleValue maximumExperienceAmount;
    public final ForgeConfigSpec.DoubleValue cursedObjectEnergyForGrade;
    public final ForgeConfigSpec.IntValue reverseCursedTechniqueChance;
    public final ForgeConfigSpec.DoubleValue requiredExperienceForStrongest;
    public final ForgeConfigSpec.IntValue sorcererFleshRarity;
    public final ForgeConfigSpec.IntValue curseFleshRarity;
    public final ForgeConfigSpec.DoubleValue experienceMultiplier;

    public final ForgeConfigSpec.DoubleValue sorcererHealingAmount;
    public final ForgeConfigSpec.DoubleValue curseHealingAmount;
    public final ForgeConfigSpec.DoubleValue sparkSoundThreshold;
    public final ForgeConfigSpec.BooleanValue uniqueTechniques;

    public final ForgeConfigSpec.IntValue minimumVeilSize;
    public final ForgeConfigSpec.IntValue maximumVeilSize;

    public final ForgeConfigSpec.IntValue maximumChantCount;
    public final ForgeConfigSpec.IntValue maximumChantLength;
    public final ForgeConfigSpec.DoubleValue chantSimilarityThreshold;

    public final ForgeConfigSpec.IntValue simpleDomainCost;
    public final ForgeConfigSpec.IntValue domainExpansionCost;
    public final ForgeConfigSpec.IntValue domainAmplificationCost;
    public final ForgeConfigSpec.IntValue zeroPointTwoSecondDomainExpansionCost;
    public final ForgeConfigSpec.IntValue divergentFistCost;
    public final ForgeConfigSpec.IntValue outputRCT;

    public ServerConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Progression").push("progression");
        this.cursedEnergyAmounts = builder.comment("Cursed energy amounts for NPCs (scales with experience)")
                .defineList("maxCursedEnergyNPC", CURSED_ENERGY_AMOUNTS
                        .entrySet()
                        .stream()
                        .map(x -> String.format(Locale.ROOT, "%s=%f", x.getKey().toString(), x.getValue()))
                        .toList(), obj -> obj instanceof String);
        this.experienceMultipliers = builder.comment("Experience multipliers for NPCs")
                .defineList("experienceMultipliers", EXPERIENCE_MULTIPLIERS
                        .entrySet()
                        .stream()
                        .map(x -> String.format(Locale.ROOT, "%s=%f", x.getKey().toString(), x.getValue()))
                        .toList(), obj -> obj instanceof String);
        this.cursedEnergyAmount = builder.comment("Cursed energy amount (scales with experience)")
                .defineInRange("maxCursedEnergyDefault", 500.0F, 0.0F, 100000.0F);
        this.requiredExperience = builder.comment("Required experience for grade")
                .defineList("requiredExperience", REQUIRED_EXPERIENCE
                        .entrySet()
                        .stream()
                        .map(x -> String.format(Locale.ROOT, "%s=%f", x.getKey().name(), x.getValue()))
                        .toList(), obj -> obj instanceof String);
        this.maximumExperienceAmount = builder.comment("The maximum amount of experience one can obtain")
                .defineInRange("maximumExperienceAmount", 10000.0F, 1.0F, 100000.0F);
        this.cursedObjectEnergyForGrade = builder.comment("The amount of energy consuming cursed objects gives to curses (multiplied by the grade of the object)")
                .defineInRange("cursedObjectEnergyForGrade", 100.0F, 1.0F, 1000.0F);
        this.reverseCursedTechniqueChance = builder.comment("The chance of unlocking reverse cursed technique when dying (smaller number equals bigger chance and the value is halved when holding a totem)")
                .defineInRange("reverseCursedTechniqueChance", 20, 1, 1000);
        this.requiredExperienceForStrongest = builder.comment("The amount of experience required for a player to be classified as strongest (meaning they can heal CT burnout using RCT and use domain amplification during a domain expansion)")
                .defineInRange("requiredExperienceForStrongest", 3000.0F, 1.0F, 100000.0F);
        this.sorcererFleshRarity = builder.comment("Rarity of sorcerers dropping flesh (bigger value means more rare)")
                .defineInRange("sorcererFleshRarity", 20, 0, 100000);
        this.curseFleshRarity = builder.comment("Rarity of curses dropping flesh (bigger value means more rare)")
                .defineInRange("curseFleshRarity", 20, 0, 100000);
        this.experienceMultiplier = builder.comment("Scale of experience you gain")
                        .defineInRange("experienceMultiplier", 1.0F, 0.0F, 100.0F);
        builder.pop();

        builder.comment("Miscellaneous").push("misc");
        this.sorcererHealingAmount = builder.comment("The maximum amount of health sorcerers can heal per tick (scales with experience)")
                .defineInRange("sorcererHealingAmount", 0.3F, 0.0F, 100.0F);
        this.curseHealingAmount = builder.comment("The maximum amount of health curses can heal per tick (scales with experience)")
                .defineInRange("curseHealingAmount", 0.4F, 0.0F, 100.0F);
        this.sparkSoundThreshold = builder.comment("The maximum amount of cursed energy a ability has to use to create a \"spark\"")
                .defineInRange("sparkSoundThreshold", 200.0F, 1.0F, 10000.0F);
        this.uniqueTechniques = builder.comment("When enabled on servers every player will have a unique technique if any are available")
                .define("uniqueTechniques", true);
        builder.pop();

        builder.comment("Veils").push("veils");
        this.minimumVeilSize = builder.comment("Minimum size for a veil")
                .defineInRange("minimumVeilSize", 4, 4, 64);
        this.maximumVeilSize = builder.comment("Maximum size for a veil")
                .defineInRange("maximumVeilSize", 64, 64, 256);
        builder.pop();

        builder.comment("Chants").push("chants");
        this.maximumChantCount = builder.comment("Maximum count for chants")
                .defineInRange("maximumChantCount", 5, 1, 16);
        this.maximumChantLength = builder.comment("Maximum length for a chant")
                .defineInRange("maximumChantLength", 24, 1, 256);
        this.chantSimilarityThreshold = builder.comment("Minimum difference between chants for them to be valid")
                .defineInRange("chantSimilarityThreshold", 0.25F, 0.0F, 1.0F);
        builder.pop();

        builder.comment("Abilities").push("chants");
        this.simpleDomainCost = builder.comment("The amount of points simple domain costs to unlock")
                .defineInRange("simpleDomainCost", 50, 1, 10000);
        this.domainExpansionCost = builder.comment("The amount of points domain expansion costs to unlock")
                .defineInRange("domainExpansionCost", 200, 1, 10000);
        this.domainAmplificationCost = builder.comment("The amount of points domain amplification costs to unlock")
                .defineInRange("domainAmplificationCost", 100, 1, 10000);
        this.zeroPointTwoSecondDomainExpansionCost = builder.comment("The amount of points 0.2s domain expasnion costs to unlock")
                .defineInRange("zeroPointTwoSecondDomainExpansionCost", 100, 1, 10000);
        this.divergentFistCost = builder.comment("The amount of points divergent fist costs to unlock")
                .defineInRange("divergentFistCost", 50, 1, 10000);
        this.outputRCT = builder.comment("The amount of points output RCT costs to unlock")
                .defineInRange("outputRCT", 300, 1, 10000);
        builder.pop();
    }

    public Map<ResourceLocation, Float> getCursedEnergyAmounts() {
        Map<ResourceLocation, Float> amounts = new HashMap<>();

        for (String line : this.cursedEnergyAmounts.get()) {
            String[] parts = line.split("=");
            String key = parts[0];
            float value = Float.parseFloat(parts[1]);
            amounts.put(new ResourceLocation(key), value);
        }
        return amounts;
    }

    public Map<ResourceLocation, Float> getExperienceMultipliers() {
        Map<ResourceLocation, Float> amounts = new HashMap<>();

        for (String line : this.experienceMultipliers.get()) {
            String[] parts = line.split("=");
            String key = parts[0];
            float value = Float.parseFloat(parts[1]);
            amounts.put(new ResourceLocation(key), value);
        }
        return amounts;
    }

    public Map<SorcererGrade, Float> getRequiredExperience() {
        Map<SorcererGrade, Float> required = new HashMap<>();

        for (String line : this.requiredExperience.get()) {
            String[] parts = line.split("=");
            String key = parts[0];
            float value = Float.parseFloat(parts[1]);
            required.put(SorcererGrade.valueOf(key), value);
        }
        return required;
    }
}
