package radon.jujutsu_kaisen.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DisplayCaseBlockEntity;

public class DisplayCaseItemRenderer {
    public static final DisplayCaseItemRenderer INSTANCE = new DisplayCaseItemRenderer();
    private final DisplayCaseBlockEntity be = new DisplayCaseBlockEntity(BlockPos.ZERO, JJKBlocks.DISPLAY_CASE.get().defaultBlockState());

    public DisplayCaseBlockEntity getBlockEntity() {
        return this.be;
    }

    public void renderByItem(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int overlay) {
        poseStack.pushPose();
        this.be.load(stack.getOrCreateTag().getCompound(BlockItem.BLOCK_ENTITY_TAG));
        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(this.be, poseStack, buffer, packedLight, overlay);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(((BlockItem) stack.getItem()).getBlock().defaultBlockState(), poseStack, buffer, packedLight, overlay);
        poseStack.popPose();
    }
}
