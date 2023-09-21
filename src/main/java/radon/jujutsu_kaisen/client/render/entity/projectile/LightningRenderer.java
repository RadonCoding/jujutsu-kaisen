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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.entity.projectile.FireArrowProjectile;
import radon.jujutsu_kaisen.entity.projectile.LightningProjectile;

public class LightningRenderer extends EntityRenderer<LightningProjectile> {
    private static final float SIZE = 1.5F;
    private static final int TEXTURE_WIDTH = 32;
    private static final int TEXTURE_HEIGHT = 128;
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/lightning.png");

    public LightningRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull LightningProjectile pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft mc = Minecraft.getInstance();

        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2.0F, 0.0F);

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(pitch));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(45.0F));

        RenderType type = JJKRenderTypes.glow(TEXTURE);

        for (int i = 0; i < 2; i++) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.0F, i * 0.1F, i * 0.1F - 0.1F);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(i * 90.0F));

            VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(type);
            Matrix4f pose = pPoseStack.last().pose();

            int frame = Mth.floor((pEntity.animation - 1 + pPartialTick) * 2);

            if (frame < 0) {
                frame = FireArrowProjectile.STILL_FRAMES;
            }

            float minU = 0.0F;
            float minV = 32.0F / TEXTURE_HEIGHT * frame;
            float maxU = minU + 32.0F / TEXTURE_WIDTH;
            float maxV = minV + 32.0F / TEXTURE_HEIGHT;

            Vector3f color = pEntity.getOwner() == null ? new Vector3f(1.0F, 1.0F, 1.0F) :
                    ParticleColors.getCursedEnergyColor(ClientVisualHandler.getData(pEntity.getOwner().getUUID()).type());

            consumer.vertex(pose, -SIZE, 0.0F, -SIZE)
                    .color(color.x(), color.y(), color.z(), 1.0F)
                    .uv(minU, minV)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            consumer.vertex(pose, -SIZE, 0.0F, SIZE)
                    .color(color.x(), color.y(), color.z(), 1.0F)
                    .uv(minU, maxV)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            consumer.vertex(pose, SIZE, 0.0F, SIZE)
                    .color(color.x(), color.y(), color.z(), 1.0F)
                    .uv(maxU, maxV)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            consumer.vertex(pose, SIZE, 0.0F, -SIZE)
                    .color(color.x(), color.y(), color.z(), 1.0F)
                    .uv(maxU, minV)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            mc.renderBuffers().bufferSource().endBatch(type);

            pPoseStack.popPose();
        }
        pPoseStack.popPose();
    }

    @Override
    protected int getBlockLightLevel(@NotNull LightningProjectile pEntity, @NotNull BlockPos pPos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LightningProjectile pEntity) {
        return null;
    }
}
