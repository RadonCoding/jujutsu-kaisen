package radon.jujutsu_kaisen.client.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.projectile.MiniUzumakiProjectile;

public class MiniUzumakiRenderer extends EntityRenderer<MiniUzumakiProjectile> {
    private static final ResourceLocation STILL = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/mini_uzumaki_still.png");
    private static final ResourceLocation BEAM = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/mini_uzumaki_beam.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 32;
    private static final float START_RADIUS = 1.3F;
    private static final float BEAM_RADIUS = 1.0F;
    private static final float STILL_SIZE = 0.1F;
    private boolean clearerView = false;

    public MiniUzumakiRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(MiniUzumakiProjectile pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.getTime() > pEntity.getCharge()) {
            this.clearerView = Minecraft.getInstance().player == pEntity.getOwner() &&
                    Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;

            double collidePosX = pEntity.prevCollidePosX + (pEntity.collidePosX - pEntity.prevCollidePosX) * pPartialTick;
            double collidePosY = pEntity.prevCollidePosY + (pEntity.collidePosY - pEntity.prevCollidePosY) * pPartialTick;
            double collidePosZ = pEntity.prevCollidePosZ + (pEntity.collidePosZ - pEntity.prevCollidePosZ) * pPartialTick;
            double posX = pEntity.xo + (pEntity.getX() - pEntity.xo) * pPartialTick;
            double posY = pEntity.yo + (pEntity.getY() - pEntity.yo) * pPartialTick;
            double posZ = pEntity.zo + (pEntity.getZ() - pEntity.zo) * pPartialTick;
            float yaw = pEntity.prevYaw + (pEntity.renderYaw - pEntity.prevYaw) * pPartialTick;
            float pitch = pEntity.prevPitch + (pEntity.renderPitch - pEntity.prevPitch) * pPartialTick;

            float length = (float) Math.sqrt(Math.pow(collidePosX - posX, 2) + Math.pow(collidePosY - posY, 2) + Math.pow(collidePosZ - posZ, 2));
            int frame = Mth.floor((pEntity.animation - 1 + pPartialTick) * 2);

            if (frame < 0) {
                frame = pEntity.getFrames() * 2;
            }

            pPoseStack.pushPose();
            pPoseStack.scale(pEntity.getScale(), pEntity.getScale(), pEntity.getScale());
            pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2.0F, 0.0F);

            VertexConsumer consumer = pBuffer.getBuffer(JJKRenderTypes.glow(BEAM));

            this.renderStart(frame, pPoseStack, consumer, pPackedLight);

            this.renderBeam(length, 180.0F / Mth.PI * yaw, 180.0F / Mth.PI * pitch, frame, pPoseStack, consumer, pPackedLight);

            pPoseStack.pushPose();
            pPoseStack.translate(collidePosX - posX, collidePosY - posY, collidePosZ - posZ);
            this.renderEnd(frame, pEntity.side, pPoseStack, consumer, pPackedLight);
            pPoseStack.popPose();

            pPoseStack.popPose();
        } else {
            Minecraft mc = Minecraft.getInstance();

            pPoseStack.pushPose();
            pPoseStack.translate(0.0D, pEntity.getBbHeight() / 2.0F, 0.0D);

            Entity viewer = mc.getCameraEntity();

            if (viewer == null) return;

            float yaw = viewer.getViewYRot(pPartialTick);
            float pitch = viewer.getViewXRot(pPartialTick);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(360.0F - yaw));
            pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch + 90.0F));

            RenderType type = RenderType.entityCutoutNoCull(STILL);
            VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(type);
            Matrix4f pose = pPoseStack.last().pose();

            consumer.vertex(pose, -STILL_SIZE, 0.0F, -STILL_SIZE)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .uv(0.0F, 0.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            consumer.vertex(pose, -STILL_SIZE, 0.0F, STILL_SIZE)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .uv(0.0F, 1.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            consumer.vertex(pose, STILL_SIZE, 0.0F, STILL_SIZE)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .uv(1.0F, 1.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
            consumer.vertex(pose, STILL_SIZE, 0.0F, -STILL_SIZE)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .uv(1.0F, 0.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_SKY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            pPoseStack.popPose();
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MiniUzumakiProjectile pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    private void renderFlatQuad(int frame, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        float minU = 16.0F / TEXTURE_WIDTH * frame;
        float minV = 0.0F;
        float maxU = minU + 16.0F / TEXTURE_WIDTH;
        float maxV = minV + 16.0F / TEXTURE_HEIGHT;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        this.drawVertex(matrix4f, matrix3f, consumer, -START_RADIUS, -START_RADIUS, 0.0F, minU, minV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -START_RADIUS, START_RADIUS, 0, minU, maxV, 1, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, START_RADIUS, START_RADIUS, 0, maxU, maxV, 1, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, START_RADIUS, -START_RADIUS, 0, maxU, minV, 1, packedLight);
    }

    private void renderStart(int frame, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        if (this.clearerView) {
            return;
        }
        poseStack.pushPose();
        Quaternionf q = this.entityRenderDispatcher.cameraOrientation();
        poseStack.mulPose(q);
        this.renderFlatQuad(frame, poseStack, consumer, packedLight);
        poseStack.popPose();
    }

    private void renderEnd(int frame, Direction side, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        poseStack.pushPose();
        Quaternionf q0 = this.entityRenderDispatcher.cameraOrientation();
        poseStack.mulPose(q0);
        this.renderFlatQuad(frame, poseStack, consumer, packedLight);
        poseStack.popPose();

        if (side == null) {
            return;
        }
        poseStack.pushPose();
        Quaternionf q1 = side.getRotation();
        q1.mul(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(q1);
        poseStack.translate(0, 0, -0.01F);
        this.renderFlatQuad(frame, poseStack, consumer, packedLight);
        poseStack.popPose();
    }

    private void drawBeam(float length, int frame, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        float minU = 0.0F;
        float minV = 16.0F / TEXTURE_HEIGHT + 1.0F / TEXTURE_HEIGHT * frame;
        float maxU = minU + 20.0F / TEXTURE_WIDTH;
        float maxV = minV + 1.0F / TEXTURE_HEIGHT;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        float offset = this.clearerView ? -1 : 0;
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, 0.0F, minU, minV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, 0, minU, maxV, 1, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, 0, maxU, maxV, 1, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, 0, maxU, minV, 1, packedLight);
    }

    private void renderBeam(float length, float yaw, float pitch, int frame, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(pitch));

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Minecraft.getInstance().gameRenderer.getMainCamera().getXRot() + 90.0F));
        this.drawBeam(length, frame, poseStack, consumer, packedLight);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YN.rotationDegrees(Minecraft.getInstance().gameRenderer.getMainCamera().getXRot() - 90.0F));
        this.drawBeam(length, frame, poseStack, consumer, packedLight);
        poseStack.popPose();

        poseStack.popPose();
    }

    public void drawVertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer, float x, float y, float z, float u, float v, float alpha, int packedLight) {
        consumer.vertex(matrix4f, x, y, z)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
