package radon.jujutsu_kaisen.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class JJKFoods {
    public static final FoodProperties CURSED_OBJECT = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.8F).effect(() ->
            new MobEffectInstance(MobEffects.CONFUSION, 10 * 20), 1.0F).alwaysEat().build();
    public static final FoodProperties SORCERER_FLESH = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().build();
    public static final FoodProperties CURSE_FLESH = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().build();
    public static final FoodProperties MERGED_FLESH = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().alwaysEat().build();
    public static final FoodProperties TRANSFIGURED_SOUL = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().alwaysEat().build();
}
