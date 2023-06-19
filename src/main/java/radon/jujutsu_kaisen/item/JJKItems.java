package radon.jujutsu_kaisen.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import radon.jujutsu_kaisen.item.armor.JJKArmorMaterial;

public class JJKItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JujutsuKaisen.MOD_ID);

    public static RegistryObject<Item> INVERTED_SPEAR_OF_HEAVEN = ITEMS.register("inverted_spear_of_heaven", () ->
            new InvertedSpearOfHeavenItem(JJKTiers.CURSED_TOOL, 9, -2.0F, (new Item.Properties()).fireResistant()));

    public static RegistryObject<Item> PLAYFUL_CLOUD = ITEMS.register("playful_cloud",
            () -> new PlayfulCloudItem(JJKTiers.CURSED_TOOL,13, 0.0F, new Item.Properties().fireResistant()));

    public static RegistryObject<ArmorItem> INVENTORY_CURSE = ITEMS.register("inventory_curse",
            () -> new InventoryCurseItem(JJKArmorMaterials.SORCERER, EquipmentSlot.CHEST, new Item.Properties()));

    private static class JJKArmorMaterials {
        public static JJKArmorMaterial SORCERER = new JJKArmorMaterial("sorcerer", 37, new int[] { 3, 6, 8, 3 }, 15, SoundEvents.ARMOR_EQUIP_LEATHER,
                3.0F, 0.1F, () -> Ingredient.EMPTY);
    }
}
