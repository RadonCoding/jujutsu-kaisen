package radon.jujutsu_kaisen.world.level.biome;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKBiomes {
    public static final ResourceKey<Biome> DOMAIN_EXPANSION = ResourceKey.create(Registries.BIOME, new ResourceLocation(JujutsuKaisen.MOD_ID, "domain_expansion"));
}