package radon.jujutsu_kaisen.world.gen.processor.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.base.CursedObjectItem;
import radon.jujutsu_kaisen.tags.JJKItemTags;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.HashMap;
import java.util.Map;

public abstract class CursedObjectDisplayCaseProcessor extends StructureProcessor {
    private static ItemStack getRandomCursedObject(ServerLevel level) {
        Map<ItemStack, Double> pool = new HashMap<>();

        Registry<Item> registry = level.registryAccess().registryOrThrow(Registries.ITEM);

        for (Item item : registry) {
            ItemStack stack = new ItemStack(item);

            if (stack.is(JJKItemTags.CURSED_OBJECT)) {
                CursedObjectItem tool = (CursedObjectItem) stack.getItem();
                SorcererGrade grade = tool.getGrade();
                pool.put(stack, (double) SorcererGrade.values().length - grade.ordinal());
            }
        }
        return HelperMethods.getWeightedRandom(pool, HelperMethods.RANDOM);
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(@NotNull LevelReader pLevel, @NotNull BlockPos p_74417_, @NotNull BlockPos pPos, StructureTemplate.@NotNull StructureBlockInfo pBlockInfo, StructureTemplate.@NotNull StructureBlockInfo pRelativeBlockInfo, @NotNull StructurePlaceSettings pSettings, @Nullable StructureTemplate template) {
        ServerLevel level = ((ServerLevelAccessor) pLevel).getLevel();

        if (pRelativeBlockInfo.state().is(JJKBlocks.DISPLAY_CASE.get())) {
            if (pRelativeBlockInfo.nbt() != null) {
                ItemStack stack = getRandomCursedObject(level);
                pRelativeBlockInfo.nbt().put("stack", stack.save(new CompoundTag()));
            }
        }
        return super.process(pLevel, p_74417_, pPos, pBlockInfo, pRelativeBlockInfo, pSettings, template);
    }
}
