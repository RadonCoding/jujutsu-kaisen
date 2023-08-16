package radon.jujutsu_kaisen.world.gen.processor.base;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ItemFrameProcessor extends StructureProcessor {
    @Override
    public StructureTemplate.@NotNull StructureEntityInfo processEntity(@NotNull LevelReader world, @NotNull BlockPos seedPos, StructureTemplate.@NotNull StructureEntityInfo rawEntityInfo, StructureTemplate.@NotNull StructureEntityInfo entityInfo, @NotNull StructurePlaceSettings placementSettings, @NotNull StructureTemplate template) {
        AtomicReference<CompoundTag> result = new AtomicReference<>(entityInfo.nbt);

        ServerLevel level = ((ServerLevelAccessor) world).getLevel();

        EntityType.create(entityInfo.nbt, level).ifPresent(entity -> {
            if (entity instanceof ItemFrame frame) {
                LootTable loot = level.getServer().getLootTables().get(this.getLootTable());
                List<ItemStack> items = loot.getRandomItems(new LootContext.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(entityInfo.blockPos))
                        .create(LootContextParamSets.CHEST));
                frame.setItem(items.get(0));
                result.set(frame.serializeNBT());
            }
        });
        return new StructureTemplate.StructureEntityInfo(entityInfo.pos, entityInfo.blockPos, result.get());
    }

    protected abstract ResourceLocation getLootTable();
}
