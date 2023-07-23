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
            new InvertedSpearOfHeavenItem(JJKTiers.CURSED_TOOL, 9, -2.0F, new Item.Properties()));
    public static RegistryObject<Item> PLAYFUL_CLOUD = ITEMS.register("playful_cloud",
            () -> new PlayfulCloudItem(JJKTiers.CURSED_TOOL,13, 0.0F, new Item.Properties()));
    public static RegistryObject<Item> SPLIT_SOUL_KATANA = ITEMS.register("split_soul_katana",
            () -> new SplitSoulKatana(JJKTiers.CURSED_TOOL,9, 0.0F, new Item.Properties()));

    public static RegistryObject<Item> YUTA_OKKOTSU_SWORD = ITEMS.register("yuta_okkotsu_sword",
            () -> new YutaOkkotsuSword(JJKTiers.CURSED_TOOL, 13, 0.0F, new Item.Properties()));

    public static RegistryObject<ArmorItem> INVENTORY_CURSE = ITEMS.register("inventory_curse",
            () -> new InventoryCurseItem(JJKArmorMaterials.INVENTORY_CURSE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> GOJO_BLINDFOLD = ITEMS.register("gojo_blindfold",
            () -> new ArmorItem(JJKArmorMaterials.GOJO_BLINDFOLD, ArmorItem.Type.HELMET, new Item.Properties()));
    public static RegistryObject<ArmorItem> GOJO_CHESTPLATE = ITEMS.register("gojo_chestplate",
            () -> new ArmorItem(JJKArmorMaterials.GOJO_OUTFIT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> GOJO_LEGGINGS = ITEMS.register("gojo_leggings",
            () -> new ArmorItem(JJKArmorMaterials.GOJO_OUTFIT, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static RegistryObject<ArmorItem> GOJO_BOOTS = ITEMS.register("gojo_boots",
            () -> new ArmorItem(JJKArmorMaterials.GOJO_OUTFIT, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static RegistryObject<ArmorItem> YUJI_CHESTPLATE = ITEMS.register("yuji_chestplate",
            () -> new ArmorItem(JJKArmorMaterials.YUJI_OUTFIT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static RegistryObject<ArmorItem> YUJI_LEGGINGS = ITEMS.register("yuji_leggings",
            () -> new ArmorItem(JJKArmorMaterials.YUJI_OUTFIT, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static RegistryObject<ArmorItem> YUJI_BOOTS = ITEMS.register("yuji_boots",
            () -> new ArmorItem(JJKArmorMaterials.YUJI_OUTFIT, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static RegistryObject<Item> PISTOL = ITEMS.register("pistol",
            () -> new PistolItem(new Item.Properties().durability(64)));

    public static RegistryObject<ForgeSpawnEggItem> TOJI_FUSHIGURO_SPAWN_EGG = ITEMS.register("toji_fushiguro_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.TOJI_FUSHIGURO, 2960685, 14145495, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> GOJO_SATORU_SPAWN_EGG = ITEMS.register("gojo_satoru_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.GOJO_SATORU, 16777215, 53503, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> SUKUNA_RYOMEN_SPAWN_EGG = ITEMS.register("sukuna_ryomen_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.SUKUNA_RYOMEN, 13405836, 12529712, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> YUTA_OKKOTSU_SPAWN_EGG = ITEMS.register("yuta_okkotsu_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.YUTA_OKKOTSU, 16185078, 3750460, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> RUGBY_FIELD_CURSE_SPAWN_EGG = ITEMS.register("rugby_field_curse_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.RUGBY_FIELD_CURSE, 6130842, 8946753, new Item.Properties()));
    public static RegistryObject<ForgeSpawnEggItem> JOGO_SPAWN_EGG = ITEMS.register("jogo_spawn_egg",
            () -> new ForgeSpawnEggItem(JJKEntities.JOGO, 12303653, 3870468, new Item.Properties()));

    public static class JJKArmorMaterials {
        public static JJKArmorMaterial INVENTORY_CURSE = new JJKArmorMaterial("inventory_curse", 37, new int[] { 0, 0, 8, 0 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                3.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial GOJO_BLINDFOLD = new JJKArmorMaterial("gojo_blindfold", 37, new int[] { 0, 0, 0, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                3.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial GOJO_OUTFIT = new JJKArmorMaterial("gojo_outfit", 37, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                3.0F, 0.1F, () -> Ingredient.EMPTY);
        public static JJKArmorMaterial YUJI_OUTFIT = new JJKArmorMaterial("yuji_outfit", 37, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                3.0F, 0.1F, () -> Ingredient.EMPTY);
    }
}
