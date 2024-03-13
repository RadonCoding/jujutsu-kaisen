package radon.jujutsu_kaisen.world.gen.feature;

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
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.world.gen.feature.JJKFeatures;

public class SpawnHanami extends Feature<NoneFeatureConfiguration> {
    public SpawnHanami(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        WorldGenLevel level = pContext.level();
        RandomSource random = pContext.random();
        BlockPos pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pContext.origin().offset(8, 0, 8));

        if (JJKFeatures.isFarEnoughFromSpawn(level, pos)) {
            if (random.nextInt(ConfigHolder.SERVER.disasterCurseRarity.get() + 1) == 0) {
                JJKEntities.HANAMI.get().spawn(level.getLevel(), pos, MobSpawnType.SPAWNER);
            }
        }
        return true;
    }
}
