package radon.jujutsu_kaisen.item.registry;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.item.*;
import radon.jujutsu_kaisen.item.armor.*;
import radon.jujutsu_kaisen.item.armor.registry.JJKArmorMaterials;
import radon.jujutsu_kaisen.item.cursed_object.SukunaFingerItem;
import radon.jujutsu_kaisen.item.cursed_tool.*;
import radon.jujutsu_kaisen.item.veil.VeilRodItem;

import java.util.ArrayList;

public class JJKItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<Item, SwordItem> INVERTED_SPEAR_OF_HEAVEN = ITEMS.register("inverted_spear_of_heaven", () ->
            new InvertedSpearOfHeavenItem(JJKTiers.SPECIAL_GRADE, new Item.Properties()
                    .attributes(SwordItem.createAttributes(JJKTiers.SPECIAL_GRADE, 0, -1.2F))));
    public static DeferredHolder<Item, SwordItem> PLAYFUL_CLOUD = ITEMS.register("playful_cloud",
            () -> new PlayfulCloudItem(JJKTiers.SPECIAL_GRADE, new Item.Properties()
                    .attributes(SwordItem.createAttributes(JJKTiers.SPECIAL_GRADE, 0, -2.4F))));
    public static DeferredHolder<Item, SwordItem> SPLIT_SOUL_KATANA = ITEMS.register("split_soul_katana",
            () -> new SplitSoulKatanaItem(JJKTiers.SPECIAL_GRADE, new Item.Properties()
                    .attributes(SwordItem.createAttributes(JJKTiers.SPECIAL_GRADE, 0, -2.4F))));
    public static DeferredHolder<Item, SwordItem> DRAGON_BONE = ITEMS.register("dragon_bone",
            () -> new DragonBoneItem(JJKTiers.SPECIAL_GRADE, new Item.Properties()
                    .attributes(SwordItem.createAttributes(JJKTiers.SPECIAL_GRADE, 0, -2.4F))));
    public static DeferredHolder<Item, SwordItem> CHAIN_OF_A_THOUSAND_MILES = ITEMS.register("chain_of_a_thousand_miles",
            () -> new ChainOfAThousandMilesItem(JJKTiers.SPECIAL_GRADE, new Item.Properties()
                    .attributes(SwordItem.createAttributes(JJKTiers.SPECIAL_GRADE, -5, 0.0F))));
    public static DeferredHolder<Item, SwordItem> NYOI_STAFF = ITEMS.register("nyoi_staff",
            () -> new NyoiStaffItem(JJKTiers.SPECIAL_GRADE, new Item.Properties()
                    .attributes(SwordItem.createAttributes(JJKTiers.SPECIAL_GRADE, 0, -2.4F))));
    public static DeferredHolder<Item, SwordItem> SLAUGHTER_DEMON = ITEMS.register("slaughter_demon",
            () -> new SlaughterDemonItem(JJKTiers.GRADE_4, new Item.Properties()
                    .attributes(SwordItem.createAttributes(JJKTiers.GRADE_4, 0, -1.2F))));
    public static DeferredHolder<Item, SwordItem> KAMUTOKE_DAGGER = ITEMS.register("kamutoke_dagger",
            () -> new KamutokeDaggerItem(JJKTiers.SPECIAL_GRADE, new Item.Properties()
                    .attributes(SwordItem.createAttributes(JJKTiers.SPECIAL_GRADE, 0, -1.2F))));
    public static DeferredHolder<Item, SwordItem> HITEN_STAFF = ITEMS.register("hiten_staff",
            () -> new HitenStaffItem(JJKTiers.SPECIAL_GRADE, new Item.Properties()
                    .attributes(SwordItem.createAttributes(JJKTiers.SPECIAL_GRADE, 0, -2.4F))));
    public static DeferredHolder<Item, SwordItem> POLEARM_STAFF = ITEMS.register("polearm_staff",
            () -> new PolearmStaffItem(JJKTiers.GRADE_2, new Item.Properties()
                    .attributes(SwordItem.createAttributes(JJKTiers.GRADE_2, 0, -1.2F))));
    public static DeferredHolder<Item, SwordItem> STEEL_GAUNTLET = ITEMS.register("steel_gauntlet",
            () -> new SteelGauntletItem(JJKTiers.GRADE_1, new Item.Properties()
                    .attributes(SwordItem.createAttributes(JJKTiers.GRADE_1, -4, 0.0F))));

    public static DeferredHolder<Item, SwordItem> GREEN_HANDLE_KATANA = ITEMS.register("green_handle_katana",
            () -> new KatanaItem(JJKTiers.GRADE_4, new Item.Properties()));
    public static DeferredHolder<Item, SwordItem> RED_HANDLE_KATANA = ITEMS.register("red_handle_katana",
            () -> new KatanaItem(JJKTiers.GRADE_4, new Item.Properties()));
    public static DeferredHolder<Item, SwordItem> MIMICRY_KATANA_BLACK = ITEMS.register("mimicry_katana_black",
            () -> new MimicryKatanaItem(JJKTiers.GRADE_4, new Item.Properties()));
    public static DeferredHolder<Item, SwordItem> MIMICRY_KATANA_WHITE = ITEMS.register("mimicry_katana_white",
            () -> new MimicryKatanaItem(JJKTiers.GRADE_4, new Item.Properties()));
    public static DeferredHolder<Item, SwordItem> JET_BLACK_SHADOW_SWORD = ITEMS.register("jet_black_shadow_sword",
            () -> new JetBlackShadowSwordItem(JJKTiers.GRADE_3, new Item.Properties()));

    public static DeferredHolder<Item, ArmorItem> INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING = ITEMS.register("instant_body_of_distorted_killing",
            () -> new InstantSpiritBodyOfDistortedKillingItem(JJKArmorMaterials.CUSTOM_MODEL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static DeferredHolder<Item, ArmorItem> ARM_BLADE = ITEMS.register("arm_blade",
            () -> new ArmBladeItem(JJKArmorMaterials.CUSTOM_MODEL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static DeferredHolder<Item, ArmorItem> GUN = ITEMS.register("gun",
            () -> new GunItem(JJKArmorMaterials.CUSTOM_MODEL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static DeferredHolder<Item, ArmorItem> HORSE_LEGS = ITEMS.register("horse_legs",
            () -> new HorseLegsItem(JJKArmorMaterials.CUSTOM_MODEL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static DeferredHolder<Item, ArmorItem> WINGS = ITEMS.register("wings",
            () -> new WingsItem(JJKArmorMaterials.CUSTOM_MODEL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static DeferredHolder<Item, ArmorItem> INVENTORY_CURSE = ITEMS.register("inventory_curse",
            () -> new InventoryCurseItem(JJKArmorMaterials.INVENTORY_CURSE, ArmorItem.Type.CHESTPLATE, new Item.Properties()
                    .component(JJKDataComponentTypes.HIDDEN_INVENTORY.get(), new ArrayList<>())));
    public static DeferredHolder<Item, Item> BLINDFOLD = ITEMS.register("blindfold",
            () -> new BlindfoldItem(JJKArmorMaterials.BLINDFOLD, ArmorItem.Type.HELMET, new Item.Properties()));

    public static DeferredHolder<Item, Item> SUKUNA_FINGER = ITEMS.register("sukuna_finger",
            () -> new SukunaFingerItem(new Item.Properties().fireResistant().food(JJKFoods.CURSED_OBJECT).stacksTo(20)));

    public static DeferredHolder<Item, Item> CURSED_SPIRIT_ORB = ITEMS.register("cursed_spirit_orb",
            () -> new CursedSpiritOrbItem(new Item.Properties().food(JJKFoods.CURSED_OBJECT)));

    public static DeferredHolder<Item, Item> TRANSFIGURED_SOUL = ITEMS.register("transfigured_soul",
            () -> new TransfiguredSoulItem(new Item.Properties().food(JJKFoods.TRANSFIGURED_SOUL)));

    public static DeferredHolder<Item, Item> VEIL_ROD = ITEMS.register("veil_rod",
            () -> new VeilRodItem(JJKBlocks.VEIL_ROD.get(), new Item.Properties()));
    public static DeferredHolder<Item, Item> ALTAR = ITEMS.register("altar",
            () -> new BlockItem(JJKBlocks.ALTAR.get(), new Item.Properties()));
    public static DeferredHolder<Item, Item> MISSION = ITEMS.register("mission",
            () -> new BlockItem(JJKBlocks.MISSION.get(), new Item.Properties()));

    public static DeferredHolder<Item, Item> CURSE_SPAWNER = ITEMS.register("curse_spawner",
            () -> new BlockItem(JJKBlocks.CURSE_SPAWNER.get(), new Item.Properties()));
    public static DeferredHolder<Item, Item> CURSE_BOSS_SPAWNER = ITEMS.register("curse_boss_spawner",
            () -> new BlockItem(JJKBlocks.CURSE_BOSS_SPAWNER.get(), new Item.Properties()));

    public static DeferredHolder<Item, Item> TARGETING_STICK = ITEMS.register("targeting_stick",
            () -> new TargetingStickItem(new Item.Properties()));

    public static DeferredHolder<Item, DeferredSpawnEggItem> TOJI_FUSHIGURO_SPAWN_EGG = ITEMS.register("toji_fushiguro_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.TOJI_FUSHIGURO, 0x2d2d2d, 0xfefefe, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> SATORU_GOJO_SPAWN_EGG = ITEMS.register("satoru_gojo_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.SATORU_GOJO, 0xffffff, 0x00d0ff, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> YUTA_OKKOTSU_SPAWN_EGG = ITEMS.register("yuta_okkotsu_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.YUTA_OKKOTSU, 0xffffff, 0x1e1e1e, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> MEGUMI_FUSHIGURO_SPAWN_EGG = ITEMS.register("megumi_fushiguro_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.MEGUMI_FUSHIGURO, 0x2a2d3d, 0x655834, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> YUJI_IDATORI_SPAWN_EGG = ITEMS.register("yuji_itadori_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.YUJI_ITADORI, 0xd59d9b, 0x704629, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> TOGE_INUMAKI_SPAWN_EGG = ITEMS.register("toge_inumaki_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.TOGE_INUMAKI, 0xe2dccc, 0x776b90, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> SUGURU_GETO_SPAWN_EGG = ITEMS.register("suguru_geto_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.SUGURU_GETO, 0x20203b, 0xad9444, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> NAOYA_ZENIN_SPAWN_EGG = ITEMS.register("naoya_zenin_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.NAOYA_ZENIN, 0xceb97b, 0xa27b50, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> HAJIME_KASHIMO_SPAWN_EGG = ITEMS.register("hajime_kashimo_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.HAJIME_KASHIMO, 0xc3ebdd, 0x54a38a, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> MAKI_ZENIN_SPAWN_EGG = ITEMS.register("maki_zenin_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.MAKI_ZENIN, 0x233c3f, 0x8f856d, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> AOI_TODO_SPAWN_EGG = ITEMS.register("aoi_todo_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.AOI_TODO, 0xe1a066, 0x8c6e40, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> MIWA_KASUMI_SPAWN_EGG = ITEMS.register("miwa_kasumi_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.MIWA_KASUMI, 0x313850, 0x85b5ce, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> WINDOW_SPAWN_EGG = ITEMS.register("window_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.WINDOW, 0x363936, 0x6b462e, new Item.Properties()));

    public static DeferredHolder<Item, DeferredSpawnEggItem> JOGO_SPAWN_EGG = ITEMS.register("jogo_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.JOGO, 0xb4b544, 0x0e0e10, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> JOGOAT_SPAWN_EGG = ITEMS.register("jogoat_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.JOGOAT, 0x2d2d2d, 0x00d0ff, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> DAGON_SPAWN_EGG = ITEMS.register("dagon_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.DAGON, 0x9e3b41, 0xd6c0a3, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> HANAMI_SPAWN_EGG = ITEMS.register("hanami_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.HANAMI, 0xfdfcfb, 0x790510, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> RUGBY_FIELD_CURSE_SPAWN_EGG = ITEMS.register("rugby_field_curse_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.RUGBY_FIELD_CURSE, 0x629fb0, 0xc7b46b, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> FISH_CURSE_SPAWN_EGG = ITEMS.register("fish_curse_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.FISH_CURSE, 0x181a21, 0x252935, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> CYCLOPS_CURSE_SPAWN_EGG = ITEMS.register("cyclops_curse_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.CYCLOPS_CURSE, 0xbec0c5, 0xb7acba, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> KUCHISAKE_ONNA_SPAWN_EGG = ITEMS.register("kuchisake_onna_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.KUCHISAKE_ONNA, 0xc3c6c8, 0xfaf4e0, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> ZOMBA_CURSE_SPAWN_EGG = ITEMS.register("zomba_curse_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.ZOMBA_CURSE, 0x72aeb3, 0xc1b385, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> WORM_CURSE_SPAWN_EGG = ITEMS.register("worm_curse_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.WORM_CURSE, 0xcac7bf, 0x4b8355, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> FELINE_CURSE_SPAWN_EGG = ITEMS.register("feline_curse_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.FELINE_CURSE, 0x79592d, 0x481f0d, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> FUGLY_CURSE_SPAWN_EGG = ITEMS.register("fugly_curse_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.FUGLY_CURSE, 0xd7d2cb, 0x64685a, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> BIRD_CURSE_SPAWN_EGG = ITEMS.register("bird_curse_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.BIRD_CURSE, 0xc79b54, 0xc7ad87, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> FINGER_BEARER_SPAWN_EGG = ITEMS.register("finger_bearer_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.FINGER_BEARER, 0xb7ccd5, 0x303a58, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> RAINBOW_DRAGON_SPAWN_EGG = ITEMS.register("rainbow_dragon_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.RAINBOW_DRAGON, 0xffffff, 0xffea59, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> DINO_CURSE_SPAWN_EGG = ITEMS.register("dino_curse_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.DINO_CURSE, 0x2d405c, 0xb7f6fe, new Item.Properties()));
    public static DeferredHolder<Item, DeferredSpawnEggItem> KO_GUY_SPAWN_EGG = ITEMS.register("ko_guy_spawn_egg",
            () -> new DeferredSpawnEggItem(JJKEntities.KO_GUY, 0x8e855a, 0x59476b, new Item.Properties()));
}
