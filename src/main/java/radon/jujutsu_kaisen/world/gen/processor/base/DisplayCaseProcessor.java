package radon.jujutsu_kaisen.world.gen.processor.base;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;

import java.util.List;

public abstract class DisplayCaseProcessor extends StructureProcessor {

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(@NotNull LevelReader pLevel, @NotNull BlockPos p_74417_, @NotNull BlockPos pPos, StructureTemplate.@NotNull StructureBlockInfo pBlockInfo, StructureTemplate.@NotNull StructureBlockInfo pRelativeBlockInfo, @NotNull StructurePlaceSettings pSettings, @Nullable StructureTemplate template) {
        if (pBlockInfo.state.is(JJKBlocks.DISPLAY_CASE.get())) {
            ServerLevel level = ((ServerLevelAccessor) pLevel).getLevel();

            LootTable loot = level.getServer().getLootTables().get(this.getLootTable());
            List<ItemStack> items = loot.getRandomItems(new LootContext.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pBlockInfo.pos))
                    .create(LootContextParamSets.CHEST));
            pBlockInfo.nbt.put("stack", items.get(0).save(new CompoundTag()));
        }
        return super.process(pLevel, p_74417_, pPos, pBlockInfo, pRelativeBlockInfo, pSettings, template);
    }

    protected abstract ResourceLocation getLootTable();
}
