package radon.jujutsu_kaisen.client.render.entity.curse;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.curse.RainbowDragonSegmentEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RainbowDragonLegsRenderer extends GeoEntityRenderer<RainbowDragonSegmentEntity> {
    public RainbowDragonLegsRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "rainbow_dragon_legs")));
    }

    @Override
    public void preRender(PoseStack poseStack, RainbowDragonSegmentEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float diff = animatable.getYRot() - animatable.yRotO;

        if (diff > 180.0F) {
            diff -= 360.0F;
        } else if (diff < -180.0F) {
            diff += 360.0F;
        }

        float yaw = animatable.yRotO + diff * partialTick;
        float pitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());

        poseStack.mulPose(Axis.YP.rotationDegrees(360.0F - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
