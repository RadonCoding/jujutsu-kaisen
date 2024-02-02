package radon.jujutsu_kaisen.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.crafting.MergedFleshRecipe;

public class JJKRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, JujutsuKaisen.MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> MERGED_FLESH = RECIPE_SERIALIZERS.register("merged_flesh", () ->
            new SimpleCraftingRecipeSerializer<>(MergedFleshRecipe::new));
}
