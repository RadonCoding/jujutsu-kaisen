package radon.jujutsu_kaisen.world.gen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WaterloggingFixProcessor extends StructureProcessor {
    public static final MapCodec<WaterloggingFixProcessor> CODEC = MapCodec.unit(WaterloggingFixProcessor::new);

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return JJKProcessors.WATERLOGGING_FIX_PROCESSOR.get();
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(@NotNull LevelReader pLevel, @NotNull BlockPos pOffset, @NotNull BlockPos pPos, StructureTemplate.@NotNull StructureBlockInfo pBlockInfo, StructureTemplate.@NotNull StructureBlockInfo pRelativeBlockInfo, @NotNull StructurePlaceSettings pSettings, @Nullable StructureTemplate template) {
        if (!pRelativeBlockInfo.state().getFluidState().isEmpty()) {
            if (pLevel instanceof WorldGenRegion region && !region.getCenter().equals(new ChunkPos(pRelativeBlockInfo.pos()))) {
                return pRelativeBlockInfo;
            }

            ChunkAccess chunk = pLevel.getChunk(pRelativeBlockInfo.pos());
            int minY = chunk.getMinBuildHeight();
            int maxY = chunk.getMaxBuildHeight();
            int currentY = pRelativeBlockInfo.pos().getY();

            if(currentY >= minY && currentY <= maxY) {
                ((LevelAccessor) pLevel).scheduleTick(pRelativeBlockInfo.pos(), pRelativeBlockInfo.state().getBlock(), 0);
            }
        }
        return pRelativeBlockInfo;
    }
}
