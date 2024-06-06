package radon.jujutsu_kaisen.client.render.entity.curse;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.model.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.curse.HanamiEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HanamiRenderer extends GeoEntityRenderer<HanamiEntity> {
    public HanamiRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedTurnHeadEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "hanami")));
    }

    @Override
    public void preRender(PoseStack poseStack, HanamiEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        this.model.getBone("wrapping").orElseThrow().setHidden(!animatable.hasCast());
        this.model.getBone("left_arm").orElseThrow().setHidden(animatable.hasCast());
    }
}
