package radon.jujutsu_kaisen.client.render.entity.ten_shadows;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.entity.ten_shadows.WheelEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WheelRenderer extends GeoEntityRenderer<WheelEntity> {
    public WheelRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "wheel")));
    }

    @Override
    public void preRender(PoseStack poseStack, WheelEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        LivingEntity owner = animatable.getOwner();

        if (owner == null) return;

        poseStack.translate(0.0F, animatable.getBbHeight() / 2.0F, 0.0F);

        float yaw = Mth.lerp(partialTick, owner.yBodyRotO, owner.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));

        float scale = owner.getBbWidth();
        poseStack.scale(scale, scale, scale);

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, WheelEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        LivingEntity owner = animatable.getOwner();

        if (owner == null) return;

        ClientVisualHandler.ClientData client = ClientVisualHandler.get(owner.getUUID());

        if (client != null && client.toggled.contains(JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
            red = 0.0F;
            green = 0.0F;
            blue = 0.0F;
        }

        boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null && animatable.getVehicle().shouldRiderSit());
        float lerpBodyRot = 0.0F;
        float lerpHeadRot = 0.0F;
        float netHeadYaw = lerpHeadRot - lerpBodyRot;

        float limbSwingAmount = 0;
        float limbSwing = 0;

        if (!isReRender) {
            float headPitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
            AnimationState<WheelEntity> animationState = new AnimationState<>(animatable, limbSwing, limbSwingAmount, partialTick, false);
            long instanceId = getInstanceId(animatable);

            animationState.setData(DataTickets.TICK, animatable.getTick(animatable));
            animationState.setData(DataTickets.ENTITY, animatable);
            animationState.setData(DataTickets.ENTITY_MODEL_DATA, new EntityModelData(shouldSit, false, -netHeadYaw, -headPitch));
            this.model.addAdditionalStateData(animatable, instanceId, animationState::setData);
            this.model.handleAnimations(animatable, instanceId, animationState);
        }

        this.updateAnimatedTextureFrame(animatable);

        for (GeoBone group : model.topLevelBones()) {
            this.renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
