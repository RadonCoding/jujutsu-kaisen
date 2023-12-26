package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.effect.FireBeamEntity;
import radon.jujutsu_kaisen.entity.effect.WaterTorrentEntity;
import radon.jujutsu_kaisen.entity.projectile.FireArrowProjectile;

public class FireBeamRenderer extends EntityRenderer<FireBeamEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/fire_beam.png");
    private static final int TEXTURE_WIDTH = 16;
    private static final int TEXTURE_HEIGHT = 512;
    private static final float BEAM_RADIUS = 0.5F;
    private boolean clearerView = false;

    public FireBeamRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FireBeamEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(FireBeamEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        Entity owner = pEntity.getOwner();
        this.clearerView = owner instanceof Player && Minecraft.getInstance().player == owner &&
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

        VertexConsumer consumer = pBuffer.getBuffer(JJKRenderTypes.glow(this.getTextureLocation(pEntity)));

        float brightness = 1.0F - ((float) pEntity.getTime() / (Math.max(pEntity.getFrames(), pEntity.getDuration()) - Math.min(pEntity.getFrames(), pEntity.getDuration())));

        this.renderBeam(length, 180.0F / (float) Math.PI * yaw, 180.0F / (float) Math.PI * pitch, frame, pPoseStack, consumer,
                brightness, pPackedLight);

        pPoseStack.popPose();
    }

    private void drawCube(float length, int frame, PoseStack poseStack, VertexConsumer consumer, float brightness, int packedLight) {
        float minU = 0.0F;
        float minV = 16.0F / TEXTURE_HEIGHT * frame;
        float maxU = minU + 16.0F / TEXTURE_WIDTH;
        float maxV = minV + 16.0F / TEXTURE_HEIGHT;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        float offset = this.clearerView ? -1.0F : 0.0F;

        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, BEAM_RADIUS, minU, minV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, BEAM_RADIUS, maxU, minV, brightness, packedLight);

        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, -BEAM_RADIUS, minU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, -BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, -BEAM_RADIUS, maxU, minV, brightness, packedLight);

        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, -BEAM_RADIUS, maxU, minV, brightness, packedLight);

        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, -BEAM_RADIUS, maxU, minV, brightness, packedLight);

        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, -BEAM_RADIUS, maxU, minV, brightness, packedLight);

        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, -BEAM_RADIUS, maxU, minV, brightness, packedLight);
    }

    private void renderBeam(float length, float yaw, float pitch, int frame, PoseStack poseStack, VertexConsumer consumer, float brightness, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(pitch));

        this.drawCube(length, frame, poseStack, consumer, brightness, packedLight);

        poseStack.popPose();
    }

    public void drawVertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer, float x, float y, float z, float u, float v, float brightness, int packedLight) {
        consumer.vertex(matrix4f, x, y, z)
                .color(brightness, brightness, brightness, 1.0F)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    protected int getBlockLightLevel(@NotNull FireBeamEntity pEntity, @NotNull BlockPos pPos) {
        return 15;
    }
}
