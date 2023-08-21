package radon.jujutsu_kaisen.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.client.tile.DisplayCaseItemRenderer;
import radon.jujutsu_kaisen.item.DisplayCaseItem;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(ItemStack pItemStack, ItemDisplayContext pDisplayContext, boolean pLeftHand, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay, BakedModel pModel, CallbackInfo ci) {
        if (!pItemStack.isEmpty() && pItemStack.getItem() instanceof DisplayCaseItem) {
            pPoseStack.pushPose();
            pModel.getTransforms().getTransform(pDisplayContext).apply(pLeftHand, pPoseStack);
            pPoseStack.translate(-0.5F, -0.5F, -0.5F);

            DisplayCaseItemRenderer.INSTANCE.renderByItem(pItemStack, pPoseStack, pBuffer, pCombinedLight, pCombinedOverlay);

            pPoseStack.popPose();

            ci.cancel();
        }
    }
}