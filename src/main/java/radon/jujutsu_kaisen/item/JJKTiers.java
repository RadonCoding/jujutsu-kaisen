package radon.jujutsu_kaisen.item;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import radon.jujutsu_kaisen.tags.JJKBlockTags;

public class JJKTiers {
    public static SimpleTier GRADE_4 = new SimpleTier(JJKBlockTags.INCORRECT_FOR_CURSED_TOOL, 301, 5.0F, 1.5F, 0,
            () -> Ingredient.EMPTY);
    public static SimpleTier GRADE_3 = new SimpleTier(JJKBlockTags.INCORRECT_FOR_CURSED_TOOL, 401, 6.0F, 2.0F, 0,
            () -> Ingredient.EMPTY);
    public static SimpleTier SEMI_GRADE_2 = new SimpleTier(JJKBlockTags.INCORRECT_FOR_CURSED_TOOL, 501, 7.0F, 2.5F, 0,
            () -> Ingredient.EMPTY);
    public static SimpleTier GRADE_2 = new SimpleTier(JJKBlockTags.INCORRECT_FOR_CURSED_TOOL, 601, 8.0F, 3.0F, 0,
            () -> Ingredient.EMPTY);
    public static SimpleTier SEMI_GRADE_1 = new SimpleTier(JJKBlockTags.INCORRECT_FOR_CURSED_TOOL, 701, 9.0F, 3.5F, 0,
            () -> Ingredient.EMPTY);
    public static SimpleTier GRADE_1 = new SimpleTier(JJKBlockTags.INCORRECT_FOR_CURSED_TOOL, 801, 10.0F, 4.0F, 0,
            () -> Ingredient.EMPTY);
    public static SimpleTier SPECIAL_GRADE_1 = new SimpleTier(JJKBlockTags.INCORRECT_FOR_CURSED_TOOL, 3001, 11.0F, 4.5F, 0,
            () -> Ingredient.EMPTY);
    public static SimpleTier SPECIAL_GRADE = new SimpleTier(JJKBlockTags.INCORRECT_FOR_CURSED_TOOL, 6001, 13.0F, 5.0F, 0,
            () -> Ingredient.EMPTY);
}
