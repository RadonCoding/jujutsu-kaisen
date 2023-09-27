package radon.jujutsu_kaisen.world.gen.processor.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.tags.JJKItemTags;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;

public abstract class CursedObjectDisplayCaseProcessor extends StructureProcessor {
    private static ItemStack getRandomCursedObject(LevelAccessor accessor) {
        List<ItemStack> pool = new ArrayList<>();

        Registry<Item> registry = accessor.registryAccess().registryOrThrow(Registries.ITEM);

        for (Item item : registry) {
            ItemStack stack = new ItemStack(item);

            if (stack.is(JJKItemTags.CURSED_OBJECT)) {
                pool.add(stack);
            }
        }
        return pool.get(HelperMethods.RANDOM.nextInt(pool.size()));
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(@NotNull LevelReader pLevel, @NotNull BlockPos p_74417_, @NotNull BlockPos pPos, StructureTemplate.@NotNull StructureBlockInfo pBlockInfo, StructureTemplate.@NotNull StructureBlockInfo pRelativeBlockInfo, @NotNull StructurePlaceSettings pSettings, @Nullable StructureTemplate template) {
        if (pBlockInfo.state().is(JJKBlocks.DISPLAY_CASE.get())) {
            pBlockInfo.nbt().put("stack", getRandomCursedObject((LevelAccessor) pLevel).save(new CompoundTag()));
        }
        return super.process(pLevel, p_74417_, pPos, pBlockInfo, pRelativeBlockInfo, pSettings, template);
    }
}
