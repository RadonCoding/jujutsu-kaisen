package radon.jujutsu_kaisen.world.level;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKLevels {
    public static ResourceKey<Level> DOMAIN_EXPANSION = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(JujutsuKaisen.MOD_ID, "domain_expansion"));
}
