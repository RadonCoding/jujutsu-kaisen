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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;

public class DismantleRenderer extends EntityRenderer<DismantleProjectile> {
    private static final float SIZE = 1.0F;
    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 32;
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/dismantle.png");

    public DismantleRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull DismantleProjectile pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft mc = Minecraft.getInstance();

        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, pEntity.getBbHeight() / 2.0F, 0.0D);

        Entity viewer = mc.getCameraEntity();

        if (viewer == null) return;

        float yaw = viewer.getViewYRot(pPartialTick);
        float pitch = viewer.getViewXRot(pPartialTick);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(360.0F - yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch + 90.0F));

        RenderType type = RenderType.entityCutoutNoCull(this.getTextureLocation(pEntity));

        VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(type);
        Matrix4f pose = pPoseStack.last().pose();

        int frame = Mth.floor((pEntity.animation - 1 + pPartialTick) * 2);

        if (frame < 0) {
            frame = DismantleProjectile.FRAMES * 2;
        }

        float minU = 32.0F / TEXTURE_WIDTH * frame;
        float minV = 0.0F;
        float maxU = minU + 32.0F / TEXTURE_WIDTH;
        float maxV = minV + 32.0F / TEXTURE_HEIGHT;

        consumer.vertex(pose, -SIZE, 0.0F, -SIZE)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, -SIZE, 0.0F, SIZE)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, SIZE, 0.0F, SIZE)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, SIZE, 0.0F, -SIZE)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        mc.renderBuffers().bufferSource().endBatch(type);

        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DismantleProjectile pEntity) {
        return TEXTURE;
    }
}
