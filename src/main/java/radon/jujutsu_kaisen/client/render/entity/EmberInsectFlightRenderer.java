package radon.jujutsu_kaisen.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.EmberInsectFlightEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EmberInsectFlightRenderer extends GeoEntityRenderer<EmberInsectFlightEntity> {
    public EmberInsectFlightRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(JujutsuKaisen.MOD_ID, "ember_insect")));
    }

    @Override
    public void render(@NotNull EmberInsectFlightEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        LivingEntity owner = entity.getOwner();

        if (owner == null) return;

        float yaw = Mth.lerp(partialTick, owner.yBodyRotO, owner.yBodyRot);

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));

        for (int i = 0; i < 2; i++) {
            poseStack.pushPose();
            poseStack.translate(owner.getBbWidth() / 2.0F * (i == 0 ? -1 : 1), entity.getBbHeight() / 2.0F, 0.0F);
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }

    @Override
    public void actuallyRender(PoseStack poseStack, EmberInsectFlightEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();

        if (!isReRender) {
            AnimationState<EmberInsectFlightEntity> state = new AnimationState<>(animatable, 0.0F, 0.0F, partialTick, false);
            long instanceId = getInstanceId(animatable);

            state.setData(DataTickets.TICK, animatable.getTick(animatable));
            state.setData(DataTickets.ENTITY, animatable);
            state.setData(DataTickets.ENTITY_MODEL_DATA, new EntityModelData(false, false, 0.0F, 0.0F));
            this.model.addAdditionalStateData(animatable, instanceId, state::setData);
            this.model.handleAnimations(animatable, instanceId, state);
        }

        poseStack.translate(0.0F, 0.01F, 0.0F);

        this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        this.updateAnimatedTextureFrame(animatable);

        for (GeoBone group : model.topLevelBones()) {
            this.renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
        poseStack.popPose();
    }
}
