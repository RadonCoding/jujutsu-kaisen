package radon.jujutsu_kaisen.world.gen.feature;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;

public class SpawnJogo extends Feature<NoneFeatureConfiguration> {
    public SpawnJogo(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        WorldGenLevel level = pContext.level();
        RandomSource random = pContext.random();
        BlockPos pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pContext.origin());

        if (JJKFeatures.isFarEnoughFromSpawn(level, pos)) {
            if (random.nextInt(ConfigHolder.SERVER.disasterCurseRarity.get() + 1) == 0) {
                JJKEntities.JOGO.get().spawn(level.getLevel(), pos, MobSpawnType.SPAWNER);
            }
        }
        return true;
    }
}
