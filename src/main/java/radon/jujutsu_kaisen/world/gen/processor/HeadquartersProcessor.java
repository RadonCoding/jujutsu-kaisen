package radon.jujutsu_kaisen.world.gen.processor;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.VeilRodBlock;

public class HeadquartersProcessor extends StructureProcessor {
    public static final MapCodec<HeadquartersProcessor> CODEC = MapCodec.unit(HeadquartersProcessor::new);

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return JJKProcessors.HEADQUARTERS_PROCESSOR.get();
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(@NotNull LevelReader pLevel, @NotNull BlockPos pOffset, @NotNull BlockPos pPos, StructureTemplate.@NotNull StructureBlockInfo pBlockInfo, StructureTemplate.@NotNull StructureBlockInfo pRelativeBlockInfo, @NotNull StructurePlaceSettings pSettings, @Nullable StructureTemplate template) {
        if (!pRelativeBlockInfo.state().is(JJKBlocks.VEIL_ROD.get())) return pRelativeBlockInfo;

        pRelativeBlockInfo.state().setValue(VeilRodBlock.SPAWN_VEIL_MASTER, true);

        ((LevelAccessor) pLevel).scheduleTick(pRelativeBlockInfo.pos(), pRelativeBlockInfo.state().getBlock(), 0);

        return pRelativeBlockInfo;
    }
}