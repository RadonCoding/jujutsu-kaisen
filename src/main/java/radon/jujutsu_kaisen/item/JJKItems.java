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
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import radon.jujutsu_kaisen.item.armor.JJKArmorMaterial;

public class JJKItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JujutsuKaisen.MOD_ID);

    public static RegistryObject<Item> INVERTED_SPEAR_OF_HEAVEN = ITEMS.register("inverted_spear_of_heaven", () ->
            new InvertedSpearOfHeavenItem(JJKTiers.CURSED_TOOL, 6, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> PLAYFUL_CLOUD = ITEMS.register("playful_cloud",
            () -> new PlayfulCloudItem(JJKTiers.CURSED_TOOL,9, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> SPLIT_SOUL_KATANA = ITEMS.register("split_soul_katana",
            () -> new SplitSoulKatana(JJKTiers.CURSED_TOOL,6, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> CHAIN = ITEMS.register("chain",
            () -> new ChainItem(JJKTiers.CURSED_TOOL,0, 0.0F, new Item.Properties()));
    public static RegistryObject<Item> NYOI_STAFF = ITEMS.register("nyoi_staff",
            () -> new NyoiStaffItem(JJKTiers.CURSED_TOOL, 6, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> SLAUGHTER_DEMON = ITEMS.register("slaughter_demon",
            () -> new SlaughterDemonItem(JJKTiers.CURSED_TOOL, 4, -2.8F, new Item.Properties()));

    public static RegistryObject<Item> YUTA_OKKOTSU_SWORD = ITEMS.register("yuta_okkotsu_sword",
            () -> new YutaOkkotsuSword(JJKTiers.CURSED_TOOL, 6, -2.8F, new Item.Properties()));
    public static RegistryObject<Item> JET_BLACK_SHADOW_SWORD = ITEMS.register("jet_black_shadow_sword",
            () -> new JetBlackShadowSwordItem(JJKTiers.CURSED_TOOL, 6, -2.8F, new Item.Properties()));

    public static RegistryObject<ArmorItem> INVENTORY_CURSE = ITEMS.register("inventory_curse",
            () -> new InventoryCurseItem(JJKArmorMaterials.INVENTORY_CURSE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> SATORU_BLINDFOLD = ITEMS.register("satoru_blindfold",
            () -> new ArmorItem(JJKArmorMaterials.SATORU_BLINDFOLD, ArmorItem.Type.HELMET, new Item.Properties()));
    public static RegistryObject<ArmorItem> SATORU_CHESTPLATE = ITEMS.register("satoru_chestplate",
            () -> new ArmorItem(JJKArmorMaterials.SATORU_OUTFIT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> SATORU_LEGGINGS = ITEMS.register("satoru_leggings",
            () -> new ArmorItem(JJKArmorMaterials.SATORU_OUTFIT, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static RegistryObject<ArmorItem> SATORU_BOOTS = ITEMS.register("satoru_boots",
            () -> new ArmorItem(JJKArmorMaterials.SATORU_OUTFIT, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static RegistryObject<ArmorItem> YUJI_CHESTPLATE = ITEMS.register("yuji_chestplate",
            () -> new ArmorItem(JJKArmorMaterials.YUJI_OUTFIT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> YUJI_LEGGINGS = ITEMS.register("yuji_leggings",
            () -> new ArmorItem(JJKArmorMaterials.YUJI_OUTFIT, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static RegistryObject<ArmorItem> YUJI_BOOTS = ITEMS.register("yuji_boots",
            () -> new ArmorItem(JJKArmorMaterials.YUJI_OUTFIT, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static RegistryObject<ArmorItem> MEGUMI_CHESTPLATE = ITEMS.register("megumi_chestplate",
            () -> new ArmorItem(JJKArmorMaterials.MEGUMI_OUTFIT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> MEGUMI_LEGGINGS = ITEMS.register("megumi_leggings",
            () -> new ArmorItem(JJKArmorMaterials.MEGUMI_OUTFIT, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static RegistryObject<ArmorItem> MEGUMI_BOOTS = ITEMS.register("megumi_boots",
            () -> new ArmorItem(JJKArmorMaterials.MEGUMI_OUTFIT, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static RegistryObject<ArmorItem> TOGE_HELMET = ITEMS.register("toge_helmet",
            () -> new ArmorItem(JJKArmorMaterials.TOGE_OUTFIT, ArmorItem.Type.HELMET, new Item.Properties()));
    public static RegistryObject<ArmorItem> TOGE_CHESTPLATE = ITEMS.register("toge_chestplate",
            () -> new ArmorItem(JJKArmorMaterials.TOGE_OUTFIT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> TOGE_LEGGINGS = ITEMS.register("toge_leggings",
            () -> new ArmorItem(JJKArmorMaterials.TOGE_OUTFIT, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static RegistryObject<ArmorItem> TOGE_BOOTS = ITEMS.register("toge_boots",
            () -> new ArmorItem(JJKArmorMaterials.TOGE_OUTFIT, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static RegistryObject<ArmorItem> YUTA_CHESTPLATE = ITEMS.register("yuta_chestplate",
            () -> new ArmorItem(JJKArmorMaterials.YUTA_OUTFIT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> YUTA_LEGGINGS = ITEMS.register("yuta_leggings",
            () -> new ArmorItem(JJKArmorMaterials.YUTA_OUTFIT, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static RegistryObject<ArmorItem> YUTA_BOOTS = ITEMS.register("yuta_boots",
            () -> new ArmorItem(JJKArmorMaterials.YUTA_OUTFIT, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static RegistryObject<ArmorItem> SUGURU_CHESTPLATE = ITEMS.register("suguru_chestplate",
            () -> new ArmorItem(JJKArmorMaterials.SUGURU_OUTFIT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> SUGURU_LEGGINGS = ITEMS.register("suguru_leggings",
            () -> new ArmorItem(JJKArmorMaterials.SUGURU_OUTFIT, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static RegistryObject<ArmorItem> SUGURU_BOOTS = ITEMS.register("suguru_boots",
            () -> new ArmorItem(JJKArmorMaterials.SUGURU_OUTFIT, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static RegistryObject<Item> DISPLAY_CASE = ITEMS.register("display_case",
            () -> new DisplayCaseItem(JJKBlocks.DISPLAY_CASE.get(), new Item.Properties()));

    public static RegistryObject<Item> SUKUNA_FINGER = ITEMS.register("sukuna_finger",
            () -> new SukunaFingerItem(new Item.Properties().food(JJKFoods.CURSED_OBJECT)));
    public static RegistryObject<Item> CURSED_TOTEM = ITEMS.register("cursed_totem",
            () -> new CursedTotemItem(new Item.Properties().food(JJKFoods.CURSED_OBJECT)));
    public static RegistryObject<Item> CURSED_SPIRIT_ORB = ITEMS.register("cursed_spirit_orb",
            () -> new CursedSpiritOrbItem(new Item.Properties().food(JJKFoods.CURSED_OBJECT)));


    public static RegistryObject<Item> VEIL_ROD = ITEMS.register("veil_rod",
            () -> new VeilRodItem(JJKBlocks.VEIL_ROD.get(), new Item.Properties()));
    public static RegistryObject<Item> ALTAR = ITEMS.register("altar",
            () -> new BlockItem(JJKBlocks.ALTAR.get(), new Item.Properties()));

    public static RegistryObject<ForgeSpawnEggItem> TOJI_FUSHIGURO_SPAWN_EGG = ITEMS.register("toji_fushiguro_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.TOJI_FUSHIGURO, 0x2D2D2D, 0xD7D7D7, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> SATORU_GOJO_SPAWN_EGG = ITEMS.register("satoru_gojo_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.SATORU_GOJO, 0xFFFFFF, 0x00D0FF, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> SUKUNA_RYOMEN_SPAWN_EGG = ITEMS.register("sukuna_ryomen_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.SUKUNA_RYOMEN, 0xCC8E8C, 0xBF3030, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> YUTA_OKKOTSU_SPAWN_EGG = ITEMS.register("yuta_okkotsu_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.YUTA_OKKOTSU, 0xF6F6F6, 0x393A3C, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> MEGUMI_FUSHIGURO_SPAWN_EGG = ITEMS.register("megumi_fushiguro_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.MEGUMI_FUSHIGURO, 0x222534, 0x755F2D, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> TOJI_ZENIN_SPAWN_EGG = ITEMS.register("toji_zenin_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.TOJI_ZENIN, 0xEAEAEA, 0xCC9104, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> MEGUNA_RYOMEN_SPAWN_EGG = ITEMS.register("meguna_ryomen_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.MEGUNA_RYOMEN, 0x222534, 0xBF3030, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> YUJI_IDATORI_SPAWN_EGG = ITEMS.register("yuji_itadori_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.YUJI_ITADORI, 0xCC8E8C, 0x704629, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> TOGE_INUMAKI_SPAWN_EGG = ITEMS.register("toge_inumaki_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.TOGE_INUMAKI, 0xD8D0BC, 0x776B90, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> SUGURU_GETO_SPAWN_EGG = ITEMS.register("suguru_geto_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.SUGURU_GETO, 0x1F2A30, 0x3B2511, new Item.Properties()));

    public static RegistryObject<ForgeSpawnEggItem> RUGBY_FIELD_CURSE_SPAWN_EGG = ITEMS.register("rugby_field_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.RUGBY_FIELD_CURSE, 0x5D8C9A, 0x888441, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> JOGO_SPAWN_EGG = ITEMS.register("jogo_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.JOGO, 0xC1BA50, 0x3B0F04, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> DAGON_SPAWN_EGG = ITEMS.register("dagon_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.DAGON, 0x8E2D33, 0xD6C0A3, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> FISH_CURSE_SPAWN_EGG = ITEMS.register("fish_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.FISH_CURSE, 0x181A21, 0x252935, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> CYCLOPS_CURSE_SPAWN_EGG = ITEMS.register("cyclops_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.CYCLOPS_CURSE, 0xBEC0C5, 0xB7ACBA, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> KUCHISAKE_ONNA_SPAWN_EGG = ITEMS.register("kuchisake_onna_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.KUCHISAKE_ONNA, 0xC3C6C8, 0xFAF4E0, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> ZOMBA_CURSE_SPAWN_EGG = ITEMS.register("zomba_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.ZOMBA_CURSE, 0x72AEB3, 0xB49E6A, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> WORM_CURSE_SPAWN_EGG = ITEMS.register("worm_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.WORM_CURSE, 0xCAC7BF, 0x4B8355, new Item.Properties()));

    public static class JJKArmorMaterials {
        public static JJKArmorMaterial INVENTORY_CURSE = new JJKArmorMaterial("inventory_curse", 0, new int[] { 0, 0, 8, 0 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                4.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial SATORU_BLINDFOLD = new JJKArmorMaterial("satoru_blindfold", 100, new int[] { 0, 0, 0, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                4.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial SATORU_OUTFIT = new JJKArmorMaterial("satoru_outfit", 100, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                4.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial YUJI_OUTFIT = new JJKArmorMaterial("yuji_outfit", 100, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                4.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial MEGUMI_OUTFIT = new JJKArmorMaterial("megumi_outfit", 100, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                4.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial TOGE_OUTFIT = new JJKArmorMaterial("toge_outfit", 100, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                4.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial YUTA_OUTFIT = new JJKArmorMaterial("yuta_outfit", 100, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                4.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial SUGURU_OUTFIT = new JJKArmorMaterial("suguru_outfit", 100, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                4.0F, 0.1F, () -> Ingredient.EMPTY);
    }
}
