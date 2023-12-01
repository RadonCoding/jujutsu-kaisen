package radon.jujutsu_kaisen.item;

import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.item.crafting.MergedFleshRecipe;

public class JJKRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, JujutsuKaisen.MOD_ID);
    public static final RegistryObject<RecipeSerializer<?>> MERGED_FLESH = RECIPE_SERIALIZERS.register("merged_flesh", () ->
            new SimpleCraftingRecipeSerializer<>(MergedFleshRecipe::new));
}
