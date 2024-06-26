package radon.jujutsu_kaisen.world.level.dimension;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKDimensionTypes {
    public static ResourceKey<DimensionType> DOMAIN_EXPANSION = ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "domain_expansion"));
}
