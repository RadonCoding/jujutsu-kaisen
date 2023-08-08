package radon.jujutsu_kaisen.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class MahoragaSwordLayer extends GeoRenderLayer<MahoragaEntity> {
    private static final ResourceLocation POSITIVE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/mahoraga_positive.png");
    private static final ResourceLocation NEGATIVE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/mahoraga_negative.png");

    public MahoragaSwordLayer(GeoRenderer<MahoragaEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, MahoragaEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType type = RenderType.entityCutoutNoCull(animatable.isPositiveSword() ? POSITIVE : NEGATIVE);

        this.getRenderer().reRender(this.getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, type,
                bufferSource.getBuffer(type), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
