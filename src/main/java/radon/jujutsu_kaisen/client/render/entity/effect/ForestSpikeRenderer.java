package radon.jujutsu_kaisen.client.render.entity.effect;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.effect.ForestSpikeEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ForestSpikeRenderer extends EntityRenderer<ForestSpikeEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/forest_spike.png");
    private static final int TEXTURE_WIDTH = 16;
    private static final int TEXTURE_HEIGHT = 32;
    private static final float WIDTH = (float) TEXTURE_WIDTH / 16;
    private static final float HEIGHT = (float) TEXTURE_HEIGHT / 16;

    public ForestSpikeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull ForestSpikeEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight(), 0.0F);

        pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch + 90.0F));

        for (int i = 0; i < 2; i++) {
            pPoseStack.mulPose(Axis.YP.rotationDegrees(i * 90.0F));

            RenderType type = RenderType.entityCutoutNoCull(TEXTURE);
            VertexConsumer consumer = pBuffer.getBuffer(type);
            PoseStack.Pose pose = pPoseStack.last();

            float minU = 0.0F;
            float minV = 0.0F;
            float maxU = minU + 16.0F / TEXTURE_WIDTH;
            float maxV = minV + 32.0F / TEXTURE_HEIGHT;
            Matrix4f matrix4f = pose.pose();
            this.drawVertex(matrix4f, pose, consumer, -WIDTH, -HEIGHT, 0.0F, minU, minV, pPackedLight);
            this.drawVertex(matrix4f, pose, consumer, -WIDTH, HEIGHT, 0.0F, minU, maxV, pPackedLight);
            this.drawVertex(matrix4f, pose, consumer, WIDTH, HEIGHT, 0.0F, maxU, maxV, pPackedLight);
            this.drawVertex(matrix4f, pose, consumer, WIDTH, -HEIGHT, 0.0F, maxU, minV, pPackedLight);
        }
        pPoseStack.popPose();
    }

    private void drawVertex(Matrix4f matrix4f, PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, int packedLight) {
        consumer.vertex(matrix4f, x, y, z)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ForestSpikeEntity pEntity) {
        return TEXTURE;
    }
}
