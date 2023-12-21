package radon.jujutsu_kaisen.client.render.entity.ten_shadows;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.entity.ten_shadows.WheelEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class WheelRenderer extends GeoEntityRenderer<WheelEntity> {
    public WheelRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "wheel")));
    }

    @Override
    public void preRender(PoseStack poseStack, WheelEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        LivingEntity owner = animatable.getOwner();

        if (owner == null) return;

        poseStack.translate(0.0F, animatable.getBbHeight() / 2.0F, 0.0F);

        float yaw = Mth.lerp(partialTick, owner.yRotO, owner.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));

        float scale = owner.getBbWidth();
        poseStack.scale(scale, scale, scale);

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, WheelEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        LivingEntity owner = animatable.getOwner();

        if (owner == null) return;

        ClientVisualHandler.VisualData data = ClientVisualHandler.get(owner.getUUID());

        if (data != null && data.toggled.contains(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
            red = 0.0F;
            green = 0.0F;
            blue = 0.0F;
        }

        this.updateAnimatedTextureFrame(animatable);

        for (GeoBone group : model.topLevelBones()) {
            this.renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
