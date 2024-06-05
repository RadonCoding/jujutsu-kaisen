package radon.jujutsu_kaisen.world.gen.processor;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.CursedToolItem;
import radon.jujutsu_kaisen.tags.JJKItemTags;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.*;


public class CursedToolItemFrameProcessor extends StructureProcessor {
    public static final MapCodec<CursedToolItemFrameProcessor> CODEC = MapCodec.unit(CursedToolItemFrameProcessor::new);

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return JJKProcessors.CURSED_TOOL_ITEM_FRAME_PROCESSOR.get();
    }

    private static ItemStack getRandomCursedTool(RandomSource random) {
        Map<ItemStack, Double> pool = new HashMap<>();

        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack stack = new ItemStack(item);

            if (stack.is(JJKItemTags.CURSED_TOOL)) {
                CursedToolItem tool = (CursedToolItem) stack.getItem();
                SorcererGrade grade = tool.getGrade();
                pool.put(stack, (double) SorcererGrade.values().length - grade.ordinal());
            }
        }
        return HelperMethods.getWeightedRandom(pool, random);
    }

    @Override
    public StructureTemplate.@NotNull StructureEntityInfo processEntity(@NotNull LevelReader world, @NotNull BlockPos seedPos, StructureTemplate.@NotNull StructureEntityInfo rawEntityInfo, StructureTemplate.@NotNull StructureEntityInfo entityInfo, @NotNull StructurePlaceSettings placementSettings, @NotNull StructureTemplate template) {
        ServerLevel level = ((ServerLevelAccessor) world).getLevel();

        Optional<Entity> entity = EntityType.create(entityInfo.nbt, level);

        if (entity.isPresent()) {
            if (entity.get() instanceof ItemFrame frame) {
                RandomSource random = RandomSource.create(Mth.getSeed(seedPos));
                frame.setItem(getRandomCursedTool(random), false);
                return new StructureTemplate.StructureEntityInfo(entityInfo.pos, entityInfo.blockPos, frame.serializeNBT(world.registryAccess()));
            }
        }
        return super.processEntity(world, seedPos, rawEntityInfo, entityInfo, placementSettings, template);
    }
}
