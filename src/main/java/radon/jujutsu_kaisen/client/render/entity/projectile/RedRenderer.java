package radon.jujutsu_kaisen.client.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.projectile.RedProjectile;

public class RedRenderer extends EntityRenderer<RedProjectile> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/red.png");
    private static final RenderType RENDER_TYPE = JJKRenderTypes.glow(TEXTURE);
    private static final float SIZE = 0.1F;
    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);

    public RedRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(RedProjectile pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, pEntity.getBbHeight() / 2.0F, 0.0D);

        if (pEntity.getTime() < 20) {
            this.renderLight(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        } else {
            this.renderBall(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        }
        pPoseStack.popPose();
    }

    private void renderBall(RedProjectile pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft mc = Minecraft.getInstance();

        Entity viewer = mc.getCameraEntity();

        if (viewer == null) return;

        float yaw = viewer.getViewYRot(pPartialTick);
        float pitch = viewer.getViewXRot(pPartialTick);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(360.0F - yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch + 90.0F));

        VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(RENDER_TYPE);
        Matrix4f pose = pPoseStack.last().pose();

        consumer.vertex(pose, -SIZE, 0.0F, -SIZE)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, -SIZE, 0.0F, SIZE)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, SIZE, 0.0F, SIZE)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, SIZE, 0.0F, -SIZE)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private void renderLight(RedProjectile pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        float f0 = ((float) pEntity.getTime() + pPartialTick) / 20;
        float f1 = Math.min(f0 > 0.8F ? (f0 - 0.8F) / 0.2F : 0.0F, 1.0F);
        RandomSource random = RandomSource.create(432L);
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.lightning());

        float scale = pEntity.getBbWidth() * pEntity.getBbHeight() * 2.0F;

        for (int i = 0; (float) i < (f0 + f0 * f0) / 2.0F * 60.0F; i++) {
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
            vertex2(consumer, matrix4f, f2, f3, scale);
            vertex3(consumer, matrix4f, f2, f3, scale);
            vertex01(consumer, matrix4f, j);
            vertex3(consumer, matrix4f, f2, f3, scale);
            vertex4(consumer, matrix4f, f2, f3, scale);
            vertex01(consumer, matrix4f, j);
            vertex4(consumer, matrix4f, f2, f3, scale);
            vertex2(consumer, matrix4f, f2, f3, scale);
        }
    }


    private static void vertex01(VertexConsumer pConsumer, Matrix4f pMatrix, int pAlpha) {
        pConsumer.vertex(pMatrix, 0.0F, 0.0F, 0.0F).color(255, 255, 255, pAlpha).endVertex();
    }

    private static void vertex2(VertexConsumer pConsumer, Matrix4f pMatrix, float p_253704_, float p_253701_, float pScale) {
        pConsumer.vertex(pMatrix, -HALF_SQRT_3 * p_253701_ * pScale, p_253704_ * pScale, -0.5F * p_253701_ * pScale).color(255, 0, 0, 0).endVertex();
    }

    private static void vertex3(VertexConsumer pConsumer, Matrix4f pMatrix, float p_253729_, float p_254030_, float scaleFactor) {
        pConsumer.vertex(pMatrix, HALF_SQRT_3 * p_254030_ * scaleFactor, p_253729_ * scaleFactor, -0.5F * p_254030_ * scaleFactor).color(255, 0, 0, 0).endVertex();
    }

    private static void vertex4(VertexConsumer pConsumer, Matrix4f pMatrix, float p_253649_, float p_253694_, float scaleFactor) {
        pConsumer.vertex(pMatrix, 0.0F, p_253649_ * scaleFactor, p_253694_ * scaleFactor).color(255, 0, 0, 0).endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RedProjectile pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    protected int getBlockLightLevel(@NotNull RedProjectile pEntity, @NotNull BlockPos pPos) {
        return 15;
    }
}