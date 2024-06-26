package radon.jujutsu_kaisen.client.render.entity.effect;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.effect.SkyStrikeEntity;

public class SkyStrikeRenderer extends EntityRenderer<SkyStrikeEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/sky_strike.png");

    private static final float TEXTURE_WIDTH = 208.0F;
    private static final float TEXTURE_HEIGHT = 32.0F;
    private static final float BEAM_MIN_U = 176.0F / TEXTURE_WIDTH;
    private static final float BEAM_MAX_U = 1.0F;
    private static final float PIXEL_SCALE = 1.0F / 16;
    private static final int MAX_HEIGHT = 256;
    private static final float DRAW_FADE_IN_RATE = 2.0F;
    private static final float DRAW_FADE_IN_POINT = 1.0F / DRAW_FADE_IN_RATE;
    private static final float DRAW_OPACITY_MULTIPLIER = 0.7F;
    private static final float RING_RADIUS = 1.6F;
    private static final int RING_FRAME_SIZE = 16;
    private static final int RING_FRAME_COUNT = 6;
    private static final int BEAM_FRAME_COUNT = 31;
    private static final float BEAM_DRAW_START_RADIUS = 2.0F;
    private static final float BEAM_DRAW_END_RADIUS = 0.25F;
    private static final float BEAM_STRIKE_RADIUS = 1.0F;

    public SkyStrikeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    private static void vertex(Matrix4f matrix4f, PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, float alpha, int packedLight) {
        consumer.vertex(matrix4f, x, y, z)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SkyStrikeEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(SkyStrikeEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        float maxY = (float) (MAX_HEIGHT - pEntity.getY());

        if (maxY < 0) {
            return;
        }
        boolean isStriking = pEntity.isStriking(pPartialTick);

        pPoseStack.pushPose();
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(pEntity)));

        if (isStriking) {
            this.drawStrike(pEntity, maxY, pPartialTick, pPoseStack, consumer, pPackedLight);
        }
        pPoseStack.popPose();
    }

    private void drawStrike(SkyStrikeEntity entity, float maxY, float partialTicks, PoseStack poseStack, VertexConsumer builder, int packedLightIn) {
        float drawTime = entity.getStrikeDrawTime(partialTicks);
        float strikeTime = entity.getStrikeDamageTime(partialTicks);
        boolean drawing = entity.isStrikeDrawing(partialTicks);
        float opacity = drawing && drawTime < DRAW_FADE_IN_POINT ? drawTime * DRAW_FADE_IN_RATE : 1;

        if (drawing) {
            opacity *= DRAW_OPACITY_MULTIPLIER;
        }
        this.drawRing(drawing, drawTime, strikeTime, opacity, poseStack, builder, packedLightIn);
        poseStack.mulPose(Axis.YP.rotationDegrees(-Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));
        this.drawBeam(drawing, drawTime, strikeTime, opacity, maxY, poseStack, builder, packedLightIn);
    }

    private void drawRing(boolean drawing, float drawTime, float strikeTime, float opacity, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        int frame = (int) (((drawing ? drawTime : strikeTime) * (RING_FRAME_COUNT + 1.0F)));

        if (frame > RING_FRAME_COUNT) {
            frame = RING_FRAME_COUNT;
        }
        float minU = frame * RING_FRAME_SIZE / TEXTURE_WIDTH;
        float maxU = minU + RING_FRAME_SIZE / TEXTURE_WIDTH;
        float minV = drawing ? 0.0F : RING_FRAME_SIZE / TEXTURE_HEIGHT;
        float maxV = minV + RING_FRAME_SIZE / TEXTURE_HEIGHT;
        float offset = PIXEL_SCALE * RING_RADIUS * (frame % 2);

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        vertex(matrix4f, pose, consumer, -RING_RADIUS + offset, 0.0F, -RING_RADIUS + offset, minU, minV, opacity, packedLight);
        vertex(matrix4f, pose, consumer, -RING_RADIUS + offset, 0.0F, RING_RADIUS + offset, minU, maxV, opacity, packedLight);
        vertex(matrix4f, pose, consumer, RING_RADIUS + offset, 0.0F, RING_RADIUS + offset, maxU, maxV, opacity, packedLight);
        vertex(matrix4f, pose, consumer, RING_RADIUS + offset, 0.0F, -RING_RADIUS + offset, maxU, minV, opacity, packedLight);
    }

    private void drawBeam(boolean drawing, float drawTime, float strikeTime, float opacity, float maxY, PoseStack poseStack, VertexConsumer builder, int packedLight) {
        int frame = drawing ? 0 : (int) (strikeTime * (BEAM_FRAME_COUNT + 1.0F));

        if (frame > BEAM_FRAME_COUNT) {
            frame = BEAM_FRAME_COUNT;
        }

        float radius = BEAM_STRIKE_RADIUS;

        if (drawing) {
            radius = (BEAM_DRAW_END_RADIUS - BEAM_DRAW_START_RADIUS) * drawTime + BEAM_DRAW_START_RADIUS;
        }
        float minV = frame / TEXTURE_HEIGHT;
        float maxV = (frame + 1.0F) / TEXTURE_HEIGHT;

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        vertex(matrix4f, pose, builder, -radius, 0.0F, 0.0F, BEAM_MIN_U, minV, opacity, packedLight);
        vertex(matrix4f, pose, builder, -radius, maxY, 0.0F, BEAM_MIN_U, maxV, opacity, packedLight);
        vertex(matrix4f, pose, builder, radius, maxY, 0.0F, BEAM_MAX_U, maxV, opacity, packedLight);
        vertex(matrix4f, pose, builder, radius, 0.0F, 0.0F, BEAM_MAX_U, minV, opacity, packedLight);
    }
}