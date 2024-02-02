package radon.jujutsu_kaisen.world.gen.biome.modifier;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, JujutsuKaisen.MOD_ID);

    public static final DeferredHolder<Codec<? extends BiomeModifier>, Codec<? extends BiomeModifier>> MOB_SPAWN = BIOME_MODIFIERS.register("mob_spawn",
            () -> MobSpawnBiomeModifier.CODEC);

}
