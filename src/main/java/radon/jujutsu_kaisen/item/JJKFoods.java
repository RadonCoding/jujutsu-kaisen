package radon.jujutsu_kaisen.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class JJKFoods {
    public static final FoodProperties CURSED_OBJECT = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.8F).alwaysEat().build();
    public static final FoodProperties SORCERER_FLESH = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().build();
    public static final FoodProperties CURSE_FLESH = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().effect(() ->
            new MobEffectInstance(MobEffects.POISON, 100, 0), 1.0F).build();
    public static final FoodProperties MERGED_FLESH = (new FoodProperties.Builder()).nutrition(3).saturationMod(0.3F).meat().build();
}
