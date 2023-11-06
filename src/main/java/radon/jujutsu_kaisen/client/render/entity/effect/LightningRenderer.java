package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.entity.effect.LightningEntity;

public class LightningRenderer extends EntityRenderer<LightningEntity> {
    public LightningRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull LightningEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        float yaw = 180.0F / (float) Math.PI * (pEntity.prevYaw + (pEntity.renderYaw - pEntity.prevYaw) * pPartialTick);
        float pitch = 180.0F / (float) Math.PI * (pEntity.prevPitch + (pEntity.renderPitch - pEntity.prevPitch) * pPartialTick);

        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(yaw - 90.0F));
        pPoseStack.mulPose(Axis.XN.rotationDegrees(pitch));

        VertexConsumer consumer = pBuffer.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = pPoseStack.last().pose();

        float[] offsetX = new float[8];
        float[] offsetZ = new float[8];

        float totalOffsetX = 0.0F;
        float totalOffsetZ = 0.0F;

        RandomSource random1 = RandomSource.create(pEntity.seed);

        for (int i = 7; i >= 0; --i) {
            offsetX[i] = totalOffsetX;
            offsetZ[i] = totalOffsetZ;
            totalOffsetX += (float) (random1.nextInt(11) - 5);
            totalOffsetZ += (float) (random1.nextInt(11) - 5);
        }

        for (int j = 0; j < 4; ++j) {
            RandomSource random2 = RandomSource.create(pEntity.seed);

            for (int k = 0; k < 3; ++k) {
                int l = 7;
                int i1 = 0;

                if (k > 0) {
                    l = 7 - k;
                }

                if (k > 0) {
                    i1 = l - 2;
                }

                float currentOffsetX = offsetX[l] - totalOffsetX;
                float currentOffsetZ = offsetZ[l] - totalOffsetZ;

                for (int j1 = l; j1 >= i1; --j1) {
                    float previousOffsetX = currentOffsetX;
                    float previousOffsetZ = currentOffsetZ;

                    if (k == 0) {
                        currentOffsetX += (float) (random2.nextInt(11) - 5);
                        currentOffsetZ += (float) (random2.nextInt(11) - 5);
                    } else {
                        currentOffsetX += (float) (random2.nextInt(31) - 15);
                        currentOffsetZ += (float) (random2.nextInt(31) - 15);
                    }

                    float scale1 = 0.1F + (float) j * 0.2F;

                    if (k == 0) {
                        scale1 *= (float) j1 * 0.1F + 1.0F;
                    }

                    float scale2 = 0.1F + (float) j * 0.2F;

                    if (k == 0) {
                        scale2 *= ((float) j1 - 1.0F) * 0.1F + 1.0F;
                    }

                    quad(matrix4f, consumer, currentOffsetX, currentOffsetZ, j1, previousOffsetX, previousOffsetZ, 0.45F, 0.45F, 0.5F, scale1, scale2, false, false, true, false);
                    quad(matrix4f, consumer, currentOffsetX, currentOffsetZ, j1, previousOffsetX, previousOffsetZ, 0.45F, 0.45F, 0.5F, scale1, scale2, true, false, true, true);
                    quad(matrix4f, consumer, currentOffsetX, currentOffsetZ, j1, previousOffsetX, previousOffsetZ, 0.45F, 0.45F, 0.5F, scale1, scale2, true, true, false, true);
                    quad(matrix4f, consumer, currentOffsetX, currentOffsetZ, j1, previousOffsetX, previousOffsetZ, 0.45F, 0.45F, 0.5F, scale1, scale2, false, true, false, false);
                }
            }
        }
        pPoseStack.popPose();
    }

    private static void quad(Matrix4f matrix, VertexConsumer consumer, float x1, float z1, int y, float x2, float z2, float red, float green, float blue, float scale1, float scale2, boolean flipX, boolean flipZ, boolean nextX, boolean nextZ) {
        consumer.vertex(matrix, x1 + (flipX ? scale2 : -scale2), (float) (y * 16), z1 + (flipZ ? scale2 : -scale2))
                .color(red, green, blue, 0.3F)
                .endVertex();
        consumer.vertex(matrix, x2 + (flipX ? scale1 : -scale1), (float) ((y + 1) * 16), z2 + (flipZ ? scale1 : -scale1))
                .color(red, green, blue, 0.3F)
                .endVertex();
        consumer.vertex(matrix, x2 + (nextX ? scale1 : -scale1), (float) ((y + 1) * 16), z2 + (nextZ ? scale1 : -scale1))
                .color(red, green, blue, 0.3F)
                .endVertex();
        consumer.vertex(matrix, x1 + (nextX ? scale2 : -scale2), (float) (y * 16), z1 + (nextZ ? scale2 : -scale2))
                .color(red, green, blue, 0.3F)
                .endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LightningEntity pEntity) {
        return null;
    }
}
