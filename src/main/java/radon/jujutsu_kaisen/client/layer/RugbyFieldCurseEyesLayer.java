package radon.jujutsu_kaisen.client.layer;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.curse.RugbyFieldCurseEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class RugbyFieldCurseEyesLayer extends GeoRenderLayer<RugbyFieldCurseEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/rugby_field_curse_eyes.png");

    public RugbyFieldCurseEyesLayer(GeoRenderer<RugbyFieldCurseEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, RugbyFieldCurseEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType type = JJKRenderTypes.eyes(TEXTURE);

        this.getRenderer().reRender(this.getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, type,
                bufferSource.getBuffer(type), partialTick, 15728640, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
