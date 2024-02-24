package radon.jujutsu_kaisen.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.entity.DisplayCaseBlockEntity;

public class DisplayCaseBlockRenderer implements BlockEntityRenderer<DisplayCaseBlockEntity> {
    public DisplayCaseBlockRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    @Override
    public void render(DisplayCaseBlockEntity entity, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        poseStack.pushPose();
        poseStack.scale(0.7F, 0.7F, 0.7F);
        poseStack.translate(0.215F, 0.18F, 0.215F);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.GLASS.defaultBlockState(), poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();

        ItemStack stack;

        if ((stack = entity.getItem()) != null && !stack.isEmpty()) {
            poseStack.translate(0.5F, 0.25F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getRot()));
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.getLevel(), 0);
        }
        poseStack.popPose();
    }
}
