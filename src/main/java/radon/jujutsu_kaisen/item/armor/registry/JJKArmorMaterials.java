package radon.jujutsu_kaisen.item.armor.registry;


import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.util.EnumMap;
import java.util.List;

public class JJKArmorMaterials {
    public static DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<ArmorMaterial, ArmorMaterial> CUSTOM_MODEL = ARMOR_MATERIALS.register("custom_model", () ->
            new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), defense -> {
                defense.put(ArmorItem.Type.BOOTS, 0);
                defense.put(ArmorItem.Type.LEGGINGS, 0);
                defense.put(ArmorItem.Type.CHESTPLATE, 0);
                defense.put(ArmorItem.Type.HELMET, 0);
                defense.put(ArmorItem.Type.BODY, 0);
            }), 0, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, List.of(), 0.0F, 0.0F));
    public static DeferredHolder<ArmorMaterial, ArmorMaterial> INVENTORY_CURSE = ARMOR_MATERIALS.register("inventory_curse", () ->
            new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), defense -> {
                defense.put(ArmorItem.Type.BOOTS, 0);
                defense.put(ArmorItem.Type.LEGGINGS, 0);
                defense.put(ArmorItem.Type.CHESTPLATE, 0);
                defense.put(ArmorItem.Type.HELMET, 0);
                defense.put(ArmorItem.Type.BODY, 0);
            }), 0, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, List.of(), 0.0F, 0.0F));
    public static DeferredHolder<ArmorMaterial, ArmorMaterial> BLINDFOLD = ARMOR_MATERIALS.register("blindfold", () ->
            new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), defense -> {
                defense.put(ArmorItem.Type.BOOTS, 0);
                defense.put(ArmorItem.Type.LEGGINGS, 0);
                defense.put(ArmorItem.Type.CHESTPLATE, 0);
                defense.put(ArmorItem.Type.HELMET, 0);
                defense.put(ArmorItem.Type.BODY, 0);
            }), 0, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY, List.of(), 0.0F, 0.0F));
}
