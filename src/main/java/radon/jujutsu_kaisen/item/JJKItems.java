package radon.jujutsu_kaisen.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
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

    public static RegistryObject<Item> YUTA_OKKOTSU_SWORD = ITEMS.register("yuta_okkotsu_sword",
            () -> new YutaOkkotsuSword(JJKTiers.CURSED_TOOL, 9, -2.8F, new Item.Properties()));

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
    public static RegistryObject<Item> PISTOL = ITEMS.register("pistol",
            () -> new PistolItem(new Item.Properties().durability(15)));

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

    public static RegistryObject<ForgeSpawnEggItem> RUGBY_FIELD_CURSE_SPAWN_EGG = ITEMS.register("rugby_field_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.RUGBY_FIELD_CURSE, 0x5D8C9A, 0x888441, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> JOGO_SPAWN_EGG = ITEMS.register("jogo_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.JOGO, 0xBBBD25, 0x3B0F04, new Item.Properties()));

    public static class JJKArmorMaterials {
        public static JJKArmorMaterial INVENTORY_CURSE = new JJKArmorMaterial("inventory_curse", 0, new int[] { 0, 0, 8, 0 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                3.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial SATORU_BLINDFOLD = new JJKArmorMaterial("satoru_blindfold", 37, new int[] { 0, 0, 0, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                3.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial SATORU_OUTFIT = new JJKArmorMaterial("satoru_outfit", 37, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                3.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial YUJI_OUTFIT = new JJKArmorMaterial("yuji_outfit", 37, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                3.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial MEGUMI_OUTFIT = new JJKArmorMaterial("megumi_outfit", 37, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                3.0F, 0.1F, () -> Ingredient.EMPTY);
    }
}
