package radon.jujutsu_kaisen.world.gen.biome.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.List;

public record MobSpawnBiomeModifier(BiomeMatcher matcher, List<MobSpawnSettings.SpawnerData> spawners) implements BiomeModifier {
    public static final Codec<MobSpawnBiomeModifier> CODEC = RecordCodecBuilder.create(codec -> codec.group(
            BiomeMatcher.CODEC.fieldOf("biomes").forGetter(MobSpawnBiomeModifier::matcher),
            MobSpawnSettings.SpawnerData.CODEC.listOf().fieldOf("spawners").forGetter(MobSpawnBiomeModifier::spawners)
    ).apply(codec, MobSpawnBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != BiomeModifier.Phase.ADD) return;

        if (this.matcher.test(biome)) {
            this.spawners.forEach(data -> builder.getMobSpawnSettings().addSpawn(data.type.getCategory(), data));
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}