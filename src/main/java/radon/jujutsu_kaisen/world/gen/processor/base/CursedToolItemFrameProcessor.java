package radon.jujutsu_kaisen.world.gen.processor.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.tags.JJKItemTags;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.*;


public abstract class CursedToolItemFrameProcessor extends StructureProcessor {
    private static ItemStack getRandomCursedTool(ServerLevel level) {
        Map<ItemStack, Double> pool = new HashMap<>();

        Registry<Item> registry = level.registryAccess().registryOrThrow(Registries.ITEM);

        for (Item item : registry) {
            ItemStack stack = new ItemStack(item);

            if (stack.is(JJKItemTags.CURSED_TOOL)) {
                CursedToolItem tool = (CursedToolItem) stack.getItem();
                SorcererGrade grade = tool.getGrade();
                pool.put(stack, (double) SorcererGrade.values().length - grade.ordinal());
            }
        }
        return HelperMethods.getWeightedRandom(pool, HelperMethods.RANDOM);
    }

    @Override
    public StructureTemplate.@NotNull StructureEntityInfo processEntity(@NotNull LevelReader world, @NotNull BlockPos seedPos, StructureTemplate.@NotNull StructureEntityInfo rawEntityInfo, StructureTemplate.@NotNull StructureEntityInfo entityInfo, @NotNull StructurePlaceSettings placementSettings, @NotNull StructureTemplate template) {
        ServerLevel level = ((ServerLevelAccessor) world).getLevel();

        Optional<Entity> entity = EntityType.create(entityInfo.nbt, level);

        if (entity.isPresent()) {
            if (entity.get() instanceof ItemFrame frame) {
                frame.setItem(getRandomCursedTool(level), false);
                return new StructureTemplate.StructureEntityInfo(entityInfo.pos, entityInfo.blockPos, frame.serializeNBT());
            }
        }
        return super.processEntity(world, seedPos, rawEntityInfo, entityInfo, placementSettings, template);
    }
}
