package radon.jujutsu_kaisen.client.render.entity.effect;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.effect.PureLoveBeamEntity;

public class PureLoveBeamRenderer extends EntityRenderer<PureLoveBeamEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/pure_love.png");
    private static final int TEXTURE_WIDTH = 16;
    private static final int TEXTURE_HEIGHT = 16;
    private static final float BEAM_RADIUS = 0.25F;
    private boolean clearerView = false;

    public PureLoveBeamRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(PureLoveBeamEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.getTime() < pEntity.getCharge()) return;

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

        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2.0F, 0.0F);

        VertexConsumer consumer = pBuffer.getBuffer(JJKRenderTypes.glow(this.getTextureLocation(pEntity)));

        if (pEntity.getTime() > pEntity.getCharge()) {
            this.renderBeam(length, pEntity.getScale(), 180.0F / Mth.PI * yaw, 180.0F / Mth.PI * pitch, pPoseStack, consumer, pPackedLight);
        }
        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PureLoveBeamEntity pEntity) {
        return TEXTURE;
    }

    private void drawBeam(float length, float scale, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        float minU = 0.0F;
        float minV = 0.0F;
        float maxU = minU + TEXTURE_WIDTH / 16.0F;
        float maxV = minV + TEXTURE_HEIGHT / 16.0F;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        float offset = this.clearerView ? -1.0F : 0.0F;
        vertex(matrix4f, pose, consumer, -BEAM_RADIUS * scale, offset, 0.0F, minU, minV, 1.0F, packedLight);
        vertex(matrix4f, pose, consumer, -BEAM_RADIUS * scale, length, 0.0F, minU, maxV, 1.0F, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS * scale, length, 0.0F, maxU, maxV, 1.0F, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS * scale, offset, 0.0F, maxU, minV, 1.0F, packedLight);
    }

    private void renderBeam(float length, float scale, float yaw, float pitch, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(pitch));

        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        this.drawBeam(length, scale, poseStack, consumer, packedLight);

        poseStack.popPose();
    }

    private static void vertex(Matrix4f matrix4f, PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, float alpha, int packedLight) {
        consumer.vertex(matrix4f, x, y, z)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}