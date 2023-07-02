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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class DismantleRenderer extends EntityRenderer<DismantleProjectile> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[8];

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = new ResourceLocation(JujutsuKaisen.MOD_ID, String.format("textures/entity/dismantle_%d.png", i));
        }
    }

    private int index;

    public DismantleRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull DismantleProjectile pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, pEntity.getBbHeight() / 2.0F, 0.0D);

        Entity viewer = mc.getCameraEntity();

        if (viewer != null) {
            float yaw = viewer.getViewYRot(pPartialTick);
            float pitch = viewer.getViewXRot(pPartialTick);
            HelperMethods.rotateQ(360.0F - yaw, 0.0F, 1.0F, 0.0F, pPoseStack);
            HelperMethods.rotateQ(pitch + 90.0F, 1.0F, 0.0F, 0.0F, pPoseStack);

            float width = 1.0F;
            float height = 1.0F;
            float x1 = -width;
            float y1 = -height;
            float x2 = width;
            float y2 = height;

            if (pEntity.getTime() % 2 == 0 && ++this.index == 8) {
                this.index = 0;
            }

            RenderType type = RenderType.entityCutoutNoCull(TEXTURES[this.index]);

            VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(type);
            Matrix4f pose = pPoseStack.last().pose();

            consumer.vertex(pose, x1, 0.0F, y1)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .uv(0.0F, 0.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            consumer.vertex(pose, x1, 0.0F, y2)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .uv(0.0F, 1.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            consumer.vertex(pose, x2, 0.0F, y2)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .uv(1.0F, 1.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            consumer.vertex(pose, x2, 0.0F, y1)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .uv(1.0F, 0.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            mc.renderBuffers().bufferSource().endBatch(type);
        }
        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DismantleProjectile pEntity) {
        return null;
    }
}
