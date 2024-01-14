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
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;

public class DismantleRenderer extends EntityRenderer<DismantleProjectile> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/dismantle.png");

    public DismantleRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull DismantleProjectile pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft mc = Minecraft.getInstance();

        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2.0F, 0.0F);

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.mulPose(Axis.YN.rotationDegrees(yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch - 90.0F));

        pPoseStack.mulPose(Axis.YP.rotationDegrees(pEntity.getRoll()));

        pPoseStack.scale(1.0F, 1.0F, 0.2F);

        RenderType type = RenderType.entityTranslucent(TEXTURE);

        VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(type);
        Matrix4f pose = pPoseStack.last().pose();

        float length = pEntity.getLength() / 2.0F;

        consumer.vertex(pose, -length, 0.0F, -1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, -length, 0.0F, 1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, length, 0.0F, 1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, length, 0.0F, -1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        mc.renderBuffers().bufferSource().endBatch(type);

        pPoseStack.popPose();
    }

    @Override
    protected int getBlockLightLevel(@NotNull DismantleProjectile pEntity, @NotNull BlockPos pPos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DismantleProjectile pEntity) {
        return null;
    }
}