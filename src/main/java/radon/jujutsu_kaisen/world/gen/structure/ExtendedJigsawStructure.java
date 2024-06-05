package radon.jujutsu_kaisen.world.gen.structure;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ExtendedJigsawStructure extends JigsawStructure {
    public static final MapCodec<ExtendedJigsawStructure> CODEC = RecordCodecBuilder.mapCodec(
                            instance -> instance.group(
                                            settingsCodec(instance),
                                            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                                            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                                            Codec.intRange(0, 20).fieldOf("size").forGetter(structure -> structure.maxDepth),
                                            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                                            Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
                                            Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                                            Codec.intRange(1, 256).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                                            Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(structure -> structure.poolAliases)
                                    )
                                    .apply(instance, ExtendedJigsawStructure::new)
                    );

    public ExtendedJigsawStructure(StructureSettings p_227627_, Holder<StructureTemplatePool> p_227628_, Optional<ResourceLocation> p_227629_, int p_227630_, HeightProvider p_227631_, boolean p_227632_, Optional<Heightmap.Types> p_227633_, int p_227634_, List<PoolAliasBinding> p_307354_) {
        super(p_227627_, p_227628_, p_227629_, p_227630_, p_227631_, p_227632_, p_227633_, p_227634_, p_307354_);
    }

    @Override
    public @NotNull StructureType<?> type() {
        return JJKStructureTypes.EXTENDED_JIGSAW.get();
    }
}
