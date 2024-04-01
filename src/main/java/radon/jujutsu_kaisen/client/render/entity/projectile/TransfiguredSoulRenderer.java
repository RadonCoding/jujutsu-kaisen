package radon.jujutsu_kaisen.client.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.projectile.TransfiguredSoulProjectile;
import radon.jujutsu_kaisen.item.JJKItems;

public class TransfiguredSoulRenderer extends EntityRenderer<TransfiguredSoulProjectile> {
    private final ItemRenderer itemRenderer;

    public TransfiguredSoulRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.itemRenderer = pContext.getItemRenderer();
    }

    public void render(TransfiguredSoulProjectile pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() * 0.75F, 0.0F);

        float yaw = Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.mulPose(Axis.YN.rotationDegrees(yaw + 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(135.0F - pitch));

        ItemStack stack = JJKItems.TRANSFIGURED_SOUL.get().getDefaultInstance();

        BakedModel model = this.itemRenderer.getModel(stack, null, null, pEntity.getId());
        this.itemRenderer.render(stack, ItemDisplayContext.GROUND, false, pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY, model);
        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TransfiguredSoulProjectile pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
