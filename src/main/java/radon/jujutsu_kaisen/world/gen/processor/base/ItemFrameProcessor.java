package radon.jujutsu_kaisen.world.gen.processor.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.tags.JJKItemTags;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ItemFrameProcessor extends StructureProcessor {
    private static ItemStack getRandomCursedTool(LevelAccessor accessor) {
        List<ItemStack> pool = new ArrayList<>();

        Registry<Item> registry = accessor.registryAccess().registryOrThrow(Registries.ITEM);

        for (Item item : registry) {
            ItemStack stack = new ItemStack(item);

            if (stack.is(JJKItemTags.CURSED_TOOL)) {
                pool.add(stack);
            }
        }
        return pool.get(HelperMethods.RANDOM.nextInt(pool.size()));
    }

    @Override
    public StructureTemplate.@NotNull StructureEntityInfo processEntity(@NotNull LevelReader world, @NotNull BlockPos seedPos, StructureTemplate.@NotNull StructureEntityInfo rawEntityInfo, StructureTemplate.@NotNull StructureEntityInfo entityInfo, @NotNull StructurePlaceSettings placementSettings, @NotNull StructureTemplate template) {
        AtomicReference<CompoundTag> result = new AtomicReference<>(entityInfo.nbt);

        ServerLevel level = ((ServerLevelAccessor) world).getLevel();

        EntityType.create(entityInfo.nbt, level).ifPresent(entity -> {
            if (entity instanceof ItemFrame frame) {
                frame.setItem(getRandomCursedTool((LevelAccessor) world));
                result.set(frame.serializeNBT());
            }
        });
        return new StructureTemplate.StructureEntityInfo(entityInfo.pos, entityInfo.blockPos, result.get());
    }
}
