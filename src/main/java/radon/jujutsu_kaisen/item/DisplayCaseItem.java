package radon.jujutsu_kaisen.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.entity.DisplayCaseBlockEntity;
import radon.jujutsu_kaisen.client.tile.DisplayCaseItemRenderer;

public class DisplayCaseItem extends BlockItem {
    public DisplayCaseItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);

        this.updateDisplayCase(pLevel, pStack);
    }

    public void updateDisplayCase(Level level, ItemStack stack) {
        if (level.isClientSide) {
            DisplayCaseBlockEntity be = DisplayCaseItemRenderer.INSTANCE.getBlockEntity();
            be.load(stack.getOrCreateTag().getCompound(BLOCK_ENTITY_TAG));
            be.setLevel(level);
            be.rotTick();
            BlockItem.setBlockEntityData(stack, be.getType(), be.saveWithoutMetadata());
        }
    }
}
