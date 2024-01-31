package radon.jujutsu_kaisen.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.item.armor.*;
import radon.jujutsu_kaisen.item.cursed_object.*;
import radon.jujutsu_kaisen.item.cursed_tool.*;
import radon.jujutsu_kaisen.item.veil.VeilRodItem;

public class JJKItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JujutsuKaisen.MOD_ID);

    public static RegistryObject<Item> INVERTED_SPEAR_OF_HEAVEN = ITEMS.register("inverted_spear_of_heaven", () ->
            new InvertedSpearOfHeavenItem(JJKTiers.CURSED_TOOL, 6, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> PLAYFUL_CLOUD = ITEMS.register("playful_cloud",
            () -> new PlayfulCloudItem(JJKTiers.CURSED_TOOL, 9, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> SPLIT_SOUL_KATANA = ITEMS.register("split_soul_katana",
            () -> new SplitSoulKatanaItem(JJKTiers.CURSED_TOOL, 6, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> DRAGON_BONE = ITEMS.register("dragon_bone",
            () -> new DragonBoneItem(JJKTiers.CURSED_TOOL, 6, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> CHAIN_OF_A_THOUSAND_MILES = ITEMS.register("chain_of_a_thousand_miles",
            () -> new ChainOfAThousandMilesItem(JJKTiers.CURSED_TOOL, 0, 0.0F, new Item.Properties()));
    public static RegistryObject<Item> NYOI_STAFF = ITEMS.register("nyoi_staff",
            () -> new NyoiStaffItem(JJKTiers.CURSED_TOOL, 6, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> SLAUGHTER_DEMON = ITEMS.register("slaughter_demon",
            () -> new SlaughterDemonItem(JJKTiers.CURSED_TOOL, 4, -2.0F, new Item.Properties()));
    public static RegistryObject<Item> KAMUTOKE_DAGGER = ITEMS.register("kamutoke_dagger",
            () -> new KamutokeDaggerItem(JJKTiers.CURSED_TOOL, 4, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> HITEN_STAFF = ITEMS.register("hiten_staff",
            () -> new HitenStaffItem(JJKTiers.CURSED_TOOL, 4, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> POLEARM_STAFF = ITEMS.register("polearm_staff",
            () -> new PolearmStaffItem(JJKTiers.CURSED_TOOL, 4, -2.4F, new Item.Properties()));
    public static RegistryObject<Item> STEEL_GAUNTLET = ITEMS.register("steel_gauntlet",
            () -> new SteelGauntletItem(JJKTiers.CURSED_TOOL, 4, 0.0F, new Item.Properties()));

    public static RegistryObject<Item> GREEN_HANDLE_KATANA = ITEMS.register("green_handle_katana",
            () -> new GreenHandleKatana(JJKTiers.CURSED_TOOL, 6, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> RED_HANDLE_KATANA = ITEMS.register("red_handle_katana",
            () -> new RedHandleKatana(JJKTiers.CURSED_TOOL, 6, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> JET_BLACK_SHADOW_SWORD = ITEMS.register("jet_black_shadow_sword",
            () -> new JetBlackShadowSwordItem(JJKTiers.CURSED_TOOL, 6, -2.8F, new Item.Properties()));

    public static RegistryObject<ArmorItem> INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING = ITEMS.register("instant_body_of_distorted_killing",
            () -> new InstantSpiritBodyOfDistortedKillingItem(JJKArmorMaterials.CUSTOM_MODEL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> ARM_BLADE = ITEMS.register("arm_blade",
            () -> new ArmBladeItem(JJKArmorMaterials.CUSTOM_MODEL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> GUN = ITEMS.register("gun",
            () -> new GunItem(JJKArmorMaterials.CUSTOM_MODEL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> HORSE_LEGS = ITEMS.register("horse_legs",
            () -> new HorseLegsItem(JJKArmorMaterials.CUSTOM_MODEL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static RegistryObject<ArmorItem> WINGS = ITEMS.register("wings",
            () -> new WingsItem(JJKArmorMaterials.CUSTOM_MODEL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static RegistryObject<ArmorItem> INVENTORY_CURSE = ITEMS.register("inventory_curse",
            () -> new InventoryCurseItem(JJKArmorMaterials.INVENTORY_CURSE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<Item> BLINDFOLD = ITEMS.register("blindfold",
            () -> new BlindfoldItem(JJKArmorMaterials.BLINDFOLD, ArmorItem.Type.HELMET, new Item.Properties()));

    public static RegistryObject<Item> DISPLAY_CASE = ITEMS.register("display_case",
            () -> new DisplayCaseItem(JJKBlocks.DISPLAY_CASE.get(), new Item.Properties()));

    public static RegistryObject<Item> SUKUNA_FINGER = ITEMS.register("sukuna_finger",
            () -> new SukunaFingerItem(new Item.Properties().food(JJKFoods.CURSED_OBJECT).stacksTo(20)));
    public static RegistryObject<Item> CURSED_TOTEM = ITEMS.register("cursed_totem",
            () -> new CursedTotemItem(new Item.Properties().food(JJKFoods.CURSED_OBJECT)));
    public static RegistryObject<Item> CURSED_COMPASS = ITEMS.register("cursed_compass",
            () -> new CursedCompassItem(new Item.Properties().food(JJKFoods.CURSED_OBJECT)));
    public static RegistryObject<Item> CURSED_MUSIC_DISC = ITEMS.register("cursed_music_disc",
            () -> new CursedMusicDiscItem(new Item.Properties().food(JJKFoods.CURSED_OBJECT)));
    public static RegistryObject<Item> CURSED_EYE_OF_ENDER = ITEMS.register("cursed_eye_of_ender",
            () -> new CursedEyeOfEnderItem(new Item.Properties().food(JJKFoods.CURSED_OBJECT)));

    public static RegistryObject<Item> CURSED_SPIRIT_ORB = ITEMS.register("cursed_spirit_orb",
            () -> new CursedSpiritOrbItem(new Item.Properties().food(JJKFoods.CURSED_OBJECT)));

    public static RegistryObject<Item> TRANSFIGURED_SOUL = ITEMS.register("transfigured_soul",
            () -> new TransfiguredSoulItem(new Item.Properties().food(JJKFoods.TRANSFIGURED_SOUL)));

    public static RegistryObject<Item> SORCERER_FLESH = ITEMS.register("sorcerer_flesh",
            () -> new SorcererFleshItem(new Item.Properties().food(JJKFoods.SORCERER_FLESH)));
    public static RegistryObject<Item> CURSE_FLESH = ITEMS.register("curse_flesh",
            () -> new CurseFleshItem(new Item.Properties().food(JJKFoods.CURSE_FLESH)));
    public static RegistryObject<Item> MERGED_FLESH = ITEMS.register("merged_flesh",
            () -> new MergedFleshItem(new Item.Properties().food(JJKFoods.MERGED_FLESH)));

    public static RegistryObject<Item> VEIL_ROD = ITEMS.register("veil_rod",
            () -> new VeilRodItem(JJKBlocks.VEIL_ROD.get(), new Item.Properties()));
    public static RegistryObject<Item> ALTAR = ITEMS.register("altar",
            () -> new BlockItem(JJKBlocks.ALTAR.get(), new Item.Properties()));

    public static RegistryObject<ForgeSpawnEggItem> TOJI_FUSHIGURO_SPAWN_EGG = ITEMS.register("toji_fushiguro_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.TOJI_FUSHIGURO, 0x2d2d2d, 0xfefefe, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> SATORU_GOJO_SPAWN_EGG = ITEMS.register("satoru_gojo_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.SATORU_GOJO, 0xffffff, 0x00d0ff, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> YUTA_OKKOTSU_SPAWN_EGG = ITEMS.register("yuta_okkotsu_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.YUTA_OKKOTSU, 0xffffff, 0x1e1e1e, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> MEGUMI_FUSHIGURO_SPAWN_EGG = ITEMS.register("megumi_fushiguro_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.MEGUMI_FUSHIGURO, 0x2a2d3d, 0x655834, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> YUJI_IDATORI_SPAWN_EGG = ITEMS.register("yuji_itadori_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.YUJI_ITADORI, 0xd59d9b, 0x704629, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> TOGE_INUMAKI_SPAWN_EGG = ITEMS.register("toge_inumaki_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.TOGE_INUMAKI, 0xe2dccc, 0x776b90, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> SUGURU_GETO_SPAWN_EGG = ITEMS.register("suguru_geto_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.SUGURU_GETO, 0x20203b, 0xad9444, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> NAOYA_ZENIN_SPAWN_EGG = ITEMS.register("naoya_zenin_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.NAOYA_ZENIN, 0xceb97b, 0xa27b50, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> HAJIME_KASHIMO_SPAWN_EGG = ITEMS.register("hajime_kashimo_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.HAJIME_KASHIMO, 0xc3ebdd, 0x54a38a, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> MAKI_ZENIN_SPAWN_EGG = ITEMS.register("maki_zenin_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.MAKI_ZENIN, 0x233c3f, 0x8f856d, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> AOI_TODO_SPAWN_EGG = ITEMS.register("aoi_todo_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.AOI_TODO, 0xe1a066, 0x8c6e40, new Item.Properties()));

    public static RegistryObject<ForgeSpawnEggItem> JOGO_SPAWN_EGG = ITEMS.register("jogo_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.JOGO, 0xb4b544, 0x0e0e10, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> JOGOAT_SPAWN_EGG = ITEMS.register("jogoat_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.JOGOAT, 0x2d2d2d, 0x00d0ff, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> DAGON_SPAWN_EGG = ITEMS.register("dagon_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.DAGON, 0x9e3b41, 0xd6c0a3, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> HANAMI_SPAWN_EGG = ITEMS.register("hanami_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.HANAMI, 0xfdfcfb, 0x790510, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> RUGBY_FIELD_CURSE_SPAWN_EGG = ITEMS.register("rugby_field_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.RUGBY_FIELD_CURSE, 0x629fb0, 0xc7b46b, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> FISH_CURSE_SPAWN_EGG = ITEMS.register("fish_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.FISH_CURSE, 0x181a21, 0x252935, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> CYCLOPS_CURSE_SPAWN_EGG = ITEMS.register("cyclops_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.CYCLOPS_CURSE, 0xbec0c5, 0xb7acba, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> KUCHISAKE_ONNA_SPAWN_EGG = ITEMS.register("kuchisake_onna_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.KUCHISAKE_ONNA, 0xc3c6c8, 0xfaf4e0, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> ZOMBA_CURSE_SPAWN_EGG = ITEMS.register("zomba_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.ZOMBA_CURSE, 0x72aeb3, 0xc1b385, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> WORM_CURSE_SPAWN_EGG = ITEMS.register("worm_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.WORM_CURSE, 0xcac7bf, 0x4b8355, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> FELINE_CURSE_SPAWN_EGG = ITEMS.register("feline_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.FELINE_CURSE, 0x79592d, 0x481f0d, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> FUGLY_CURSE_SPAWN_EGG = ITEMS.register("fugly_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.FUGLY_CURSE, 0xd7d2cb, 0x64685a, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> BIRD_CURSE_SPAWN_EGG = ITEMS.register("bird_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.BIRD_CURSE, 0xc79b54, 0xc7ad87, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> FINGER_BEARER_SPAWN_EGG = ITEMS.register("finger_bearer_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.FINGER_BEARER, 0xb7ccd5, 0x303a58, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> RAINBOW_DRAGON_SPAWN_EGG = ITEMS.register("rainbow_dragon_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.RAINBOW_DRAGON, 0xffffff, 0xffea59, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> DINO_CURSE_SPAWN_EGG = ITEMS.register("dino_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.DINO_CURSE, 0x2d405c, 0xb7f6fe, new Item.Properties()));

    public static class JJKArmorMaterials {
        public static JJKArmorMaterial CUSTOM_MODEL = new JJKArmorMaterial("custom_model", 0, new int[] { 0, 0, 0, 0 }, 0, SoundEvents.EMPTY, 0.0F, 0.0F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial INVENTORY_CURSE = new JJKArmorMaterial("inventory_curse", 0, new int[] { 0, 0, 0, 0 }, 0, SoundEvents.ARMOR_EQUIP_LEATHER,
                0.0F, 0.0F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial BLINDFOLD = new JJKArmorMaterial("blindfold", 0, new int[] { 0, 0, 0, 0 }, 0, SoundEvents.ARMOR_EQUIP_LEATHER,
                0.0F, 0.0F, () -> Ingredient.EMPTY);
    }
}
