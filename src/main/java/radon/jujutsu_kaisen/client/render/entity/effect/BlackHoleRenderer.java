package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.effect.BlackHoleEntity;

public class BlackHoleRenderer extends EntityRenderer<BlackHoleEntity> {
    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);

    public BlackHoleRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull BlackHoleEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2.0F, 0.0F);

        Matrix4f matrix4f = pPoseStack.last().pose();

        int segments = 16;
        float r = pEntity.getSize();
        float dt = (float) (2 * Math.PI / segments);
        float dp = (float) (Math.PI / segments);

        for (int i = 0; i < segments; i++) {
            float t1 = i * dt;
            float t2 = (i + 1) * dt;

            float ct1 = Mth.cos(t1);
            float st1 = Mth.sin(t1);
            float ct2 = Mth.cos(t2);
            float st2 = Mth.sin(t2);

            for (int j = 0; j < segments; j++) {
                float p1 = j * dp;
                float p2 = (j + 1) * dp;

                float cp1 = Mth.cos(p1);
                float sp1 = Mth.sin(p1);
                float cp2 = Mth.cos(p2);
                float sp2 = Mth.sin(p2);

                float x1 = r * sp1 * ct1;
                float y1 = r * sp1 * st1;
                float z1 = r * cp1;
                float x2 = r * sp2 * ct1;
                float y2 = r * sp2 * st1;
                float z2 = r * cp2;
                float x3 = r * sp2 * ct2;
                float y3 = r * sp2 * st2;
                float z3 = r * cp2;
                float x4 = r * sp1 * ct2;
                float y4 = r * sp1 * st2;
                float z4 = r * cp1;

                float u1 = 0.5F - t1 / (float) (2 * Math.PI);
                float v1 = 0.5F - p1 / (float) Math.PI;
                float u2 = 0.5F - t1 / (float) (2 * Math.PI);
                float v2 = 0.5F - p2 / (float) Math.PI;
                float u3 = 0.5F - t2 / (float) (2 * Math.PI);
                float v3 = 0.5F - p2 / (float) Math.PI;
                float u4 = 0.5F - t2 / (float) (2 * Math.PI);
                float v4 = 0.5F - p1 / (float) Math.PI;

                Vector3f color = pEntity.getColor();

                VertexConsumer consumer = pBuffer.getBuffer(JJKRenderTypes.blackHole());
                consumer.vertex(matrix4f, x1, y1, z1)
                        .color(color.x(), color.y(), color.z(), 1.0F)
                        .uv(u1, v1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(LightTexture.FULL_SKY)
                        .normal(x1, y1, z1)
                        .endVertex();
                consumer.vertex(matrix4f, x2, y2, z2)
                        .color(color.x(), color.y(), color.z(), 1.0F)
                        .uv(u2, v2)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(LightTexture.FULL_SKY)
                        .normal(x2, y2, z2)
                        .endVertex();
                consumer.vertex(matrix4f, x3, y3, z3)
                        .color(color.x(), color.y(), color.z(), 1.0F)
                        .uv(u3, v3)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(LightTexture.FULL_SKY)
                        .normal(x3, y3, z3)
                        .endVertex();
                consumer.vertex(matrix4f, x4, y4, z4)
                        .color(color.x(), color.y(), color.z(), 1.0F)
                        .uv(u4, v4)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(LightTexture.FULL_SKY)
                        .normal(x4, y4, z4)
                        .endVertex();
            }
        }
        pPoseStack.popPose();

        renderLights(pEntity, pPartialTick, pPoseStack, pBuffer);
    }

    private static void renderLights(BlackHoleEntity pEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer) {
        float f0 = ((float) pEntity.getTime() + pPartialTick) / pEntity.getDuration();
        float f1 = Math.min(f0 > 0.8F ? (f0 - 0.8F) / 0.2F : 0.0F, 1.0F);
        RandomSource random = RandomSource.create(432L);
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.lightning());

        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2.0F, 0.0F);

        for (int i = 0; i < 30; i++) {
            pPoseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F + f0 * 90.0F));
            float f2 = random.nextFloat() * 20.0F + 5.0F + f1 * 10.0F;
            float f3 = random.nextFloat() * 2.0F + 1.0F + f1 * 2.0F;
            Matrix4f matrix4f = pPoseStack.last().pose();
            int j = (int) (255.0F * (1.0F - f1));
            vertex01(consumer, matrix4f, j);
            vertex2(consumer, matrix4f, f2, f3);
            vertex3(consumer, matrix4f, f2, f3);
            vertex01(consumer, matrix4f, j);
            vertex3(consumer, matrix4f, f2, f3);
            vertex4(consumer, matrix4f, f2, f3);
            vertex01(consumer, matrix4f, j);
            vertex4(consumer, matrix4f, f2, f3);
            vertex2(consumer, matrix4f, f2, f3);
        }
        pPoseStack.popPose();
    }

    private static void vertex01(VertexConsumer pConsumer, Matrix4f pMatrix, int pAlpha) {
        pConsumer.vertex(pMatrix, 0.0F, 0.0F, 0.0F).color(255, 255, 255, pAlpha).endVertex();
    }

    private static void vertex2(VertexConsumer pConsumer, Matrix4f pMatrix, float p_253704_, float p_253701_) {
        pConsumer.vertex(pMatrix, -HALF_SQRT_3 * p_253701_, p_253704_, -0.5F * p_253701_).color(255, 255, 255, 0).endVertex();
    }

    private static void vertex3(VertexConsumer pConsumer, Matrix4f pMatrix, float p_253729_, float p_254030_) {
        pConsumer.vertex(pMatrix, HALF_SQRT_3 * p_254030_, p_253729_, -0.5F * p_254030_).color(255, 255, 255, 0).endVertex();
    }

    private static void vertex4(VertexConsumer pConsumer, Matrix4f pMatrix, float p_253649_, float p_253694_) {
        pConsumer.vertex(pMatrix, 0.0F, p_253649_, p_253694_).color(255, 255, 255, 0).endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BlackHoleEntity pEntity) {
        return null;
    }
}
