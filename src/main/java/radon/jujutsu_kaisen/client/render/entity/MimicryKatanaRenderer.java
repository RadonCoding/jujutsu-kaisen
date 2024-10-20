package radon.jujutsu_kaisen.client.render.entity;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.MimicryKatanaEntity;

public class MimicryKatanaRenderer extends EntityRenderer<MimicryKatanaEntity> {
    private final ItemRenderer renderer;

    public MimicryKatanaRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.renderer = pContext.getItemRenderer();
    }

    @Override
    public void render(MimicryKatanaEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2, 0.0F);

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0F - pitch));

        this.renderer.renderStatic(pEntity.getItem().getDefaultInstance(), ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pEntity.level(), pEntity.getId());
        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MimicryKatanaEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
