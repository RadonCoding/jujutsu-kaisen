package radon.jujutsu_kaisen.client.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.projectile.RedProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RedRenderer extends EntityRenderer<RedProjectile> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/red.png");
    private static final RenderType RENDER_TYPE = JJKRenderTypes.glow(TEXTURE);
    private static final float SIZE = 0.1F;

    public RedRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(RedProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        poseStack.pushPose();
        poseStack.translate(0.0D, entity.getBbHeight() / 2.0F, 0.0D);

        Entity viewer = mc.getCameraEntity();

        if (viewer != null) {
            float yaw = viewer.getViewYRot(partialTick);
            float pitch = viewer.getViewXRot(partialTick);
            HelperMethods.rotateQ(360.0F - yaw, 0.0F, 1.0F, 0.0F, poseStack);
            HelperMethods.rotateQ(pitch + 90.0F, 1.0F, 0.0F, 0.0F, poseStack);

            float width = SIZE;
            float height = SIZE;
            float x1 = -width;
            float y1 = -height;
            float x2 = width;
            float y2 = height;

            VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(RENDER_TYPE);
            Matrix4f pose = poseStack.last().pose();

            int ticks = entity.tickCount;
            float brightness = Math.min(1.0F, ticks * 0.1F);

            consumer.vertex(pose, x1, 0.0F, y1)
                    .color(brightness, brightness, brightness, brightness)
                    .uv(0.0F, 0.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            consumer.vertex(pose, x1, 0.0F, y2)
                    .color(brightness, brightness, brightness, brightness)
                    .uv(0.0F, 1.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            consumer.vertex(pose, x2, 0.0F, y2).
                    color(brightness, brightness, brightness, brightness)
                    .uv(1.0F, 1.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            consumer.vertex(pose, x2, 0.0F, y1)
                    .color(brightness, brightness, brightness, brightness)
                    .uv(1.0F, 0.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            mc.renderBuffers().bufferSource().endBatch(RENDER_TYPE);
        }
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RedProjectile pEntity) {
        return null;
    }

    @Override
    protected int getBlockLightLevel(@NotNull RedProjectile pEntity, @NotNull BlockPos pPos) {
        return 15;
    }
}