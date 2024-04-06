package radon.jujutsu_kaisen.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.List;
import java.util.stream.Collectors;

public class ServerConfig {
    public final ModConfigSpec.DoubleValue cursedEnergyAmount;
    public final ModConfigSpec.DoubleValue cursedEnergyRegenerationAmount;
    public final ModConfigSpec.DoubleValue cursedObjectEnergyForGrade;
    public final ModConfigSpec.IntValue reverseCursedTechniqueChance;
    public final ModConfigSpec.DoubleValue requiredExperienceForExperienced;
    public final ModConfigSpec.IntValue sorcererFleshRarity;
    public final ModConfigSpec.IntValue curseFleshRarity;
    public final ModConfigSpec.DoubleValue deathPenalty;
    public final ModConfigSpec.DoubleValue experienceMultiplier;
    public final ModConfigSpec.IntValue blackFlashChance;
    public final ModConfigSpec.BooleanValue realisticCurses;
    public final ModConfigSpec.IntValue requiredImbuementAmount;

    public final ModConfigSpec.DoubleValue sorcererHealingAmount;
    public final ModConfigSpec.DoubleValue curseHealingAmount;
    public final ModConfigSpec.BooleanValue uniqueTechniques;
    public final ModConfigSpec.BooleanValue uniqueTraits;
    public final ModConfigSpec.BooleanValue destruction;
    public final ModConfigSpec.ConfigValue<List<? extends String>> chants;
    public final ModConfigSpec.DoubleValue forceFeedHealthRequirement;
    public final ModConfigSpec.BooleanValue realisticShikigami;

    public final ModConfigSpec.IntValue minimumVeilSize;
    public final ModConfigSpec.IntValue maximumVeilSize;
    public final ModConfigSpec.DoubleValue minimumDomainSize;
    public final ModConfigSpec.DoubleValue maximumDomainSize;

    public final ModConfigSpec.DoubleValue domainStrength;

    public final ModConfigSpec.IntValue maximumChantCount;
    public final ModConfigSpec.IntValue maximumChantLength;
    public final ModConfigSpec.DoubleValue chantSimilarityThreshold;

    public final ModConfigSpec.IntValue simpleDomainCost;
    public final ModConfigSpec.IntValue simpleDomainEnlargementCost;
    public final ModConfigSpec.IntValue quickDrawCost;
    public final ModConfigSpec.IntValue fallingBlossomEmotionCost;
    public final ModConfigSpec.IntValue domainExpansionCost;
    public final ModConfigSpec.IntValue domainAmplificationCost;
    public final ModConfigSpec.IntValue zeroPointTwoSecondDomainExpansionCost;
    public final ModConfigSpec.IntValue rct2Cost;
    public final ModConfigSpec.IntValue rct3Cost;
    public final ModConfigSpec.IntValue outputRCTCost;
    public final ModConfigSpec.IntValue abilityModeCost;
    public final ModConfigSpec.IntValue airFrameCost;
    public final ModConfigSpec.IntValue airJumpCost;
    public final ModConfigSpec.IntValue maximumCopiedTechniques;
    public final ModConfigSpec.ConfigValue<List<? extends String>> unlockableTechniques;

    public final ModConfigSpec.IntValue cursedEnergyNatureRarity;
    public final ModConfigSpec.IntValue curseRarity;
    public final ModConfigSpec.IntValue sixEyesRarity;
    public final ModConfigSpec.IntValue heavenlyRestrictionRarity;
    public final ModConfigSpec.IntValue vesselRarity;

    public final ModConfigSpec.IntValue maximumSkillLevel;
    public final ModConfigSpec.DoubleValue abilityPointInterval;
    public final ModConfigSpec.DoubleValue skillPointInterval;

    public final ModConfigSpec.IntValue minimumSpawnDangerDistance;
    public final ModConfigSpec.IntValue disasterCurseRarity;

    public ServerConfig(ModConfigSpec.Builder builder) {
        builder.comment("Progression").push("progression");
        this.cursedEnergyAmount = builder.comment("Base cursed energy amount")
                .defineInRange("cursedEnergyAmount", 300.0F, 0.0F, 100000.0F);
        this.cursedEnergyRegenerationAmount = builder.comment("Cursed energy regeneration amount (depends on food level)")
                .defineInRange("cursedEnergyRegenerationAmount", 0.25F, 0.0F, 100000.0F);
        this.cursedObjectEnergyForGrade = builder.comment("The amount of energy consuming cursed objects gives to curses (multiplied by the grade of the object)")
                .defineInRange("cursedObjectEnergyForGrade", 100.0F, 1.0F, 1000.0F);
        this.reverseCursedTechniqueChance = builder.comment("The chance of unlocking reverse cursed technique when dying (bigger number = rarer and when holding an totem it's 50% more likely)")
                .defineInRange("reverseCursedTechniqueChance", 20, 1, 1000);
        this.requiredExperienceForExperienced = builder.comment("The amount of experience required for a player to be classified as experienced (for now means they can use domain amplification during a domain expansion)")
                .defineInRange("requiredExperienceForExperienced", 3000.0F, 1.0F, 100000.0F);
        this.sorcererFleshRarity = builder.comment("Rarity of sorcerers dropping flesh (bigger value means more rare)")
                .defineInRange("sorcererFleshRarity", 20, 0, 100000);
        this.curseFleshRarity = builder.comment("Rarity of curses dropping flesh (bigger value means more rare)")
                .defineInRange("curseFleshRarity", 20, 0, 100000);
        this.deathPenalty = builder.comment("Percentage of experience lost on death")
                .defineInRange("deathPenalty", 0.05F, 0.0F, 1.0F);
        this.experienceMultiplier = builder.comment("Scale of experience you gain")
                        .defineInRange("experienceMultiplier", 1.0F, 0.0F, 100.0F);
        this.blackFlashChance = builder.comment("The chance of black flash (smaller number equals bigger chance)")
                .defineInRange("blackFlashChance", 100, 1, 1000);
        this.realisticCurses = builder.comment("When enabled curses and shikigami only take damage from cursed energy attacks")
                .define("realisticCurses", true);
        this.requiredImbuementAmount = builder.comment("Amount of times a technique has to be used to be imbued into a weapon")
                        .defineInRange("requiredImbuementAmount", 1000, 1, 100000);
        builder.pop();

        builder.comment("Difficulty").push("difficulty");
        this.minimumSpawnDangerDistance = builder.comment("The minimum distance from spawn of dangerous things such as disaster curses")
                .defineInRange("minimumDangerDistance", 1000, 0, 100000);
        this.disasterCurseRarity = builder.comment("How rare it is for disaster curses to spawn (bigger number = rarer)")
                .defineInRange("disasterCurseRarity", 900, 1, 100000);
        builder.pop();

        builder.comment("Miscellaneous").push("misc");
        this.sorcererHealingAmount = builder.comment("The base amount of health sorcerers heal per tick")
                .defineInRange("sorcererHealingAmount", 2.0F, 0.0F, 100.0F);
        this.curseHealingAmount = builder.comment("The base amount of health curses heal per tick")
                .defineInRange("curseHealingAmount", 3.0F, 0.0F, 100.0F);
        this.uniqueTechniques = builder.comment("When enabled on servers every player will have a unique technique if any are available")
                .define("uniqueTechniques", true);
        this.uniqueTraits = builder.comment("When enabled on servers there can be only one six eyes, heavenly restriction and vessel")
                .define("uniqueTraits", true);
        this.destruction = builder.comment("When enabled abilities break blocks")
                .define("destruction", true);
        this.chants = builder.comment("Possible chants for NPCs")
                .defineList("chants", () -> List.of(
                                "Nah, I'd win.",
                                "Stand proud.",
                                "You can cook.",
                                "Did you pray today?",
                                "You're strong.",
                                "Are you the strongest because?",
                                "Owari da.",
                                "I shall never forget you.",
                                "With this treasure i summon...",
                                "Have you ever trained?"
                        ),
                        ignored -> true
                );
        this.forceFeedHealthRequirement = builder.comment("The percentage of health someone has to be at to be able to force feed them cursed objects")
                .defineInRange("forceFeedHealthRequirement", 0.25F, 0.0F, 1.0F);
        this.realisticShikigami = builder.comment("When enabled shikigami will die permanently")
                .define("realisticShikigami", true);
        builder.pop();

        builder.comment("Veils").push("veils");
        this.minimumVeilSize = builder.comment("Minimum size for a veil")
                .defineInRange("minimumVeilSize", 4, 4, 64);
        this.maximumVeilSize = builder.comment("Maximum size for a veil")
                .defineInRange("maximumVeilSize", 128, 64, 256);
        builder.pop();

        builder.comment("Domains").push("domains");
        this.minimumDomainSize = builder.comment("Minimum size for a domain")
                .defineInRange("minimumDomainSize", 0.1F, 0.2F, 1.0F);
        this.maximumDomainSize = builder.comment("Maximum size for a domain")
                .defineInRange("maximumDomainSize", 1.5F, 1.0F, 10.0F);
        builder.pop();

        builder.comment("Barriers").push("barriers");
        this.domainStrength = builder.comment("The percentage of how much domain barriers are stronger than veil barriers (defaults to 150%)")
                        .defineInRange("domainStrength", 1.5F, 1.0F, 100000.0F);
        builder.pop();

        builder.comment("Chants").push("chants");
        this.maximumChantCount = builder.comment("Maximum count for chants")
                .defineInRange("maximumChantCount", 5, 1, 16);
        this.maximumChantLength = builder.comment("Maximum length for a chant")
                .defineInRange("maximumChantLength", 24, 1, 256);
        this.chantSimilarityThreshold = builder.comment("Minimum difference between chants for them to be valid")
                .defineInRange("chantSimilarityThreshold", 0.25F, 0.0F, 1.0F);
        builder.pop();

        builder.comment("Abilities").push("abilities");
        this.simpleDomainCost = builder.comment("The amount of points simple domain costs to unlock")
                .defineInRange("simpleDomainCost", 50, 1, 10000);
        this.simpleDomainEnlargementCost = builder.comment("The amount of points simple domain enlargement costs to unlock")
                .defineInRange("quickDrawCost", 50, 1, 10000);
        this.quickDrawCost = builder.comment("The amount of points quick draw costs to unlock")
                .defineInRange("quickDrawCost", 50, 1, 10000);
        this.fallingBlossomEmotionCost = builder.comment("The amount of points falling blossom emotion costs to unlock")
                .defineInRange("fallingBlossomEmotionCost", 50, 1, 10000);
        this.domainExpansionCost = builder.comment("The amount of points domain expansion costs to unlock")
                .defineInRange("domainExpansionCost", 200, 1, 10000);
        this.domainAmplificationCost = builder.comment("The amount of points domain amplification costs to unlock")
                .defineInRange("domainAmplificationCost", 100, 1, 10000);
        this.zeroPointTwoSecondDomainExpansionCost = builder.comment("The amount of points 0.2s domain expasnion costs to unlock")
                .defineInRange("zeroPointTwoSecondDomainExpansionCost", 100, 1, 10000);
        this.abilityModeCost = builder.comment("The amount of points ability mode costs to unlock")
                .defineInRange("abilityModeCost", 300, 1, 10000);
        this.airFrameCost = builder.comment("The amount of points air frame costs to unlock")
                .defineInRange("airFrameCost", 300, 1, 10000);
        this.airJumpCost = builder.comment("The amount of points air jump costs to unlock")
                .defineInRange("airJumpCost", 50, 1, 10000);
        this.rct2Cost = builder.comment("The amount of points tier 2 RCT costs to unlock")
                .defineInRange("rct2Cost", 100, 1, 10000);
        this.rct3Cost = builder.comment("The amount of points tier 3 RCT costs to unlock")
                .defineInRange("rct2Cost", 200, 1, 10000);
        this.outputRCTCost = builder.comment("The amount of points output RCT costs to unlock")
                .defineInRange("outputRCTCost", 300, 1, 10000);
        this.maximumCopiedTechniques = builder.comment("The amount of techniques mimicry can copy")
                .defineInRange("maximumCopiedTechniques", 6, 1, 10000);
        this.unlockableTechniques = builder.comment("Techniques that are unlockable by default")
                .defineList("unlockableTechniques", () -> List.of(
                                JJKCursedTechniques.getKey(JJKCursedTechniques.CURSE_MANIPULATION.get()).toString(),
                                JJKCursedTechniques.getKey(JJKCursedTechniques.LIMITLESS.get()).toString(),
                                JJKCursedTechniques.getKey(JJKCursedTechniques.DISMANTLE_AND_CLEAVE.get()).toString(),
                                JJKCursedTechniques.getKey(JJKCursedTechniques.CURSED_SPEECH.get()).toString(),
                                JJKCursedTechniques.getKey(JJKCursedTechniques.MIMICRY.get()).toString(),
                                JJKCursedTechniques.getKey(JJKCursedTechniques.DISASTER_FLAMES.get()).toString(),
                                JJKCursedTechniques.getKey(JJKCursedTechniques.DISASTER_TIDES.get()).toString(),
                                JJKCursedTechniques.getKey(JJKCursedTechniques.DISASTER_PLANTS.get()).toString(),
                                JJKCursedTechniques.getKey(JJKCursedTechniques.IDLE_TRANSFIGURATION.get()).toString(),
                                JJKCursedTechniques.getKey(JJKCursedTechniques.TEN_SHADOWS.get()).toString(),
                                JJKCursedTechniques.getKey(JJKCursedTechniques.BOOGIE_WOOGIE.get()).toString(),
                                JJKCursedTechniques.getKey(JJKCursedTechniques.PROJECTION_SORCERY.get()).toString()
                        ),
                        ignored -> true
                );
        builder.pop();

        builder.comment("Rarity").push("rarity");
        this.cursedEnergyNatureRarity = builder.comment("Rarity of a cursed energy nature other than basic (bigger value = rarer)")
                .defineInRange("cursedEnergyNatureRarity", 5, 1, 1000000);
        this.curseRarity = builder.comment("Rarity of being a curse (bigger value = rarer)")
                .defineInRange("curseRarity", 5, 1, 1000000);
        this.sixEyesRarity = builder.comment("Rarity of having six eyes (bigger value = rarer)")
                .defineInRange("sixEyesRarity", 10, 1, 1000000);
        this.heavenlyRestrictionRarity = builder.comment("Rarity of heavenly restriction (bigger value = rarer)")
                .defineInRange("heavenlyRestrictionRarity", 10, 1, 1000000);
        this.vesselRarity = builder.comment("Rarity of being a vessel (bigger value = rarer)")
                .defineInRange("vesselRarity", 10, 1, 1000000);
        builder.pop();

        builder.comment("Skills").push("skills");
        this.maximumSkillLevel = builder.comment("Maximum level you can upgrade a skill to")
                .defineInRange("maximumSkillLevel", 1000, 1, 1000000);
        this.abilityPointInterval = builder.comment("For every X experience you'll gain 1 ability points")
                .defineInRange("abilityPointInterval", 20.0D, 1.0D, 1000000.0D);
        this.skillPointInterval = builder.comment("For every X experience you'll gain 1 skill points")
                .defineInRange("skillPointInterval", 40.0D, 1.0D, 1000000.0D);
        builder.pop();
    }

    public List<ICursedTechnique> getUnlockableTechniques() {
        return this.unlockableTechniques.get().stream()
                .map(key -> JJKCursedTechniques.getValue(new ResourceLocation(key)))
                .collect(Collectors.toList());
    }
}
