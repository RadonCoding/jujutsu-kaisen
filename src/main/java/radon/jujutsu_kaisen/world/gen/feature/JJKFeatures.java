package radon.jujutsu_kaisen.world.gen.feature;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.LevelData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.config.ConfigHolder;

public class JJKFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, JujutsuKaisen.MOD_ID);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SPAWN_JOGO = FEATURES.register("spawn_jogo",
            () -> new SpawnJogo(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SPAWN_DAGON = FEATURES.register("spawn_dagon",
            () -> new SpawnDagon(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SPAWN_HANAMI = FEATURES.register("spawn_hanami",
            () -> new SpawnHanami(NoneFeatureConfiguration.CODEC));

    public static boolean isFarEnoughFromSpawn(LevelAccessor level, BlockPos pos) {
        LevelData data = level.getLevelData();
        BlockPos relative = new BlockPos(data.getSpawnPos().getX(), pos.getY(), data.getSpawnPos().getZ());
        return !relative.closerThan(pos, ConfigHolder.SERVER.minimumSpawnDangerDistance.get());
    }
}
