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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.projectile.HollowPurpleProjectile;

public class HollowPurpleRenderer extends EntityRenderer<HollowPurpleProjectile> {
    private static final RenderType RED = RenderType.entityCutoutNoCull(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/red.png"));
    private static final RenderType BLUE = RenderType.entityCutoutNoCull(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/blue.png"));
    private static final RenderType PURPLE = RenderType.entityCutoutNoCull(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/hollow_purple.png"));

    private static final int ANIMATION_DURATION = 20;

    public HollowPurpleRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull HollowPurpleProjectile pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        float size = pEntity.getSize();

        if (pEntity.tickCount >= ANIMATION_DURATION) {
            this.render(pEntity, pPartialTick, pPoseStack, PURPLE, size);
        } else {
            float fraction = (pEntity.tickCount + pPartialTick) / ANIMATION_DURATION;
            fraction = fraction < 0.5F ? 2 * fraction * fraction : fraction;
            float offset = Mth.lerp(fraction, size * 2, 0.0F);
            Entity viewer = Minecraft.getInstance().getCameraEntity();

            if (viewer != null) {
                float yaw = viewer.getViewYRot(pPartialTick);
                float pitch = viewer.getViewXRot(pPartialTick);

                Vec3 look = Vec3.directionFromRotation(new Vec2(pitch, yaw));
                Vec3 right = new Vec3(-Math.sin(Math.toRadians(yaw)), 0.0D, Math.cos(Math.toRadians(yaw)));
                Vec3 pos = look.cross(right).normalize().scale(offset);

                pPoseStack.pushPose();
                pPoseStack.translate(pos.x(), pos.y(), pos.z());
                this.render(pEntity, pPartialTick, pPoseStack, RED, size / 2);
                pPoseStack.popPose();

                pPoseStack.pushPose();
                pPoseStack.translate(-pos.x(), -pos.y(), -pos.z());
                this.render(pEntity, pPartialTick, pPoseStack, BLUE, size / 2);
                pPoseStack.popPose();
            }
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HollowPurpleProjectile pEntity) {
        return null;
    }

    private void render(HollowPurpleProjectile entity, float partialTick, PoseStack poseStack, RenderType type, float size) {
        Minecraft mc = Minecraft.getInstance();

        poseStack.pushPose();
        poseStack.translate(0.0D, entity.getBbHeight() / 2.0F, 0.0D);

        Entity viewer = mc.getCameraEntity();

        if (viewer == null) return;

        float yaw = viewer.getViewYRot(partialTick);
        float pitch = viewer.getViewXRot(partialTick);
        poseStack.mulPose(Axis.YP.rotationDegrees(360.0F - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch + 90.0F));

        VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(type);
        Matrix4f pose = poseStack.last().pose();

        consumer.vertex(pose, -size, 0.0F, -size)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, -size, 0.0F, size)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, size, 0.0F, size)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, size, 0.0F, -size)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        mc.renderBuffers().bufferSource().endBatch(type);

        poseStack.popPose();
    }

    @Override
    protected int getBlockLightLevel(@NotNull HollowPurpleProjectile pEntity, @NotNull BlockPos pPos) {
        return 15;
    }
}
