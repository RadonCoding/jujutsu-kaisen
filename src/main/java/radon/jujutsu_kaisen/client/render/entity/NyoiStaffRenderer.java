package radon.jujutsu_kaisen.client.render.entity;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.NyoiStaffEntity;

public class NyoiStaffRenderer extends EntityRenderer<NyoiStaffEntity> {
    private final ItemRenderer renderer;

    public NyoiStaffRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.renderer = pContext.getItemRenderer();
    }

    @Override
    public void render(NyoiStaffEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2, 0.0F);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        this.renderer.renderStatic(pEntity.getItem(), ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pEntity.level(), pEntity.getId());
        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull NyoiStaffEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
