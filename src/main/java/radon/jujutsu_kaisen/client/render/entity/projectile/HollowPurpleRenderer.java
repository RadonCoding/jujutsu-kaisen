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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.projectile.HollowPurpleProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class HollowPurpleRenderer extends EntityRenderer<HollowPurpleProjectile> {
    private static final RenderType RED = JJKRenderTypes.glow(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/red.png"));
    private static final RenderType BLUE = JJKRenderTypes.glow(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/blue.png"));
    private static final RenderType PURPLE = JJKRenderTypes.glow(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/hollow_purple.png"));

    private static final float SIZE = 1.0F;
    private static final float ANIMATION_DURATION = 20.0F;

    public HollowPurpleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(@NotNull HollowPurpleProjectile entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        if (entity.tickCount >= ANIMATION_DURATION) {
            this.render(entity, partialTick, poseStack, PURPLE, SIZE);
        } else {
            float fraction = (entity.tickCount + partialTick) / ANIMATION_DURATION;
            float size = Mth.lerp(fraction, 0.1F, SIZE);
            float offset = Mth.lerp(fraction, SIZE, 0.0F);
            Entity viewer = Minecraft.getInstance().getCameraEntity();

            if (viewer != null) {
                float yaw = viewer.getViewYRot(partialTick);
                float pitch = viewer.getViewXRot(partialTick);

                Vec3 look = Vec3.directionFromRotation(new Vec2(pitch, yaw));
                Vec3 right = new Vec3(-Math.sin(Math.toRadians(yaw)), 0.0D, Math.cos(Math.toRadians(yaw)));
                Vec3 pos = look.cross(right).normalize().scale(offset);

                poseStack.pushPose();
                poseStack.translate(pos.x(), pos.y(), pos.z());
                this.render(entity, partialTick, poseStack, RED, size);
                poseStack.popPose();

                poseStack.pushPose();
                poseStack.translate(-pos.x(), -pos.y(), -pos.z());
                this.render(entity, partialTick, poseStack, BLUE, size);
                poseStack.popPose();
            }
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HollowPurpleProjectile pEntity) {
        return null;
    }

    private void render(HollowPurpleProjectile entity, float partialTick, PoseStack poseStack, RenderType type, float size) {
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

            float width = size;
            float height = size;
            float x1 = -width;
            float y1 = -height;
            float x2 = width;
            float y2 = height;

            VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(type);
            Matrix4f pose = poseStack.last().pose();

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
        poseStack.popPose();
    }
}
