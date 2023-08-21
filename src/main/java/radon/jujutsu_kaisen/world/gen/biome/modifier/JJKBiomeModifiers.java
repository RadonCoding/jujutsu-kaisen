package radon.jujutsu_kaisen.world.gen.biome.modifier;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, JujutsuKaisen.MOD_ID);

    public static final RegistryObject<Codec<? extends BiomeModifier>> MOB_SPAWN = MODIFIERS.register("mob_spawn",
            () -> MobSpawnBiomeModifier.CODEC);

}
