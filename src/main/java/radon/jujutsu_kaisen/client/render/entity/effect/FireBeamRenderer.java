package radon.jujutsu_kaisen.client.render.entity.effect;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.client.model.entity.effect.FireBeamModel;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.effect.FireBeamEntity;

public class FireBeamRenderer extends EntityRenderer<FireBeamEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/fire_beam.png");
    private static final ResourceLocation CHARGE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/fire_beam_charge.png");
    private static final int TEXTURE_WIDTH = 16;
    private static final int TEXTURE_HEIGHT = 512;
    private static final float BEAM_RADIUS = 0.5F;
    private final FireBeamModel model;
    private boolean clearerView = false;

    public FireBeamRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        this.model = new FireBeamModel(pContext.bakeLayer(FireBeamModel.LAYER));
    }

    private static void vertex(Matrix4f matrix4f, PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, float brightness, int packedLight) {
        consumer.vertex(matrix4f, x, y, z)
                .color(brightness, brightness, brightness, 1.0F)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public void render(FireBeamEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        this.clearerView = Minecraft.getInstance().player == pEntity.getOwner() &&
                Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;

        float yaw = Mth.lerp(pPartialTick, pEntity.prevYaw, pEntity.renderYaw);
        float pitch = Mth.lerp(pPartialTick, pEntity.prevPitch, pEntity.renderPitch);

        if (!this.clearerView) {
            Vector3f color = ParticleColors.FIRE_YELLOW;

            float age = pEntity.getTime() + pPartialTick;

            pPoseStack.pushPose();

            pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw * Mth.RAD_TO_DEG));
            pPoseStack.mulPose(Axis.ZN.rotationDegrees(pitch));

            VertexConsumer charge = pBuffer.getBuffer(JJKRenderTypes.glow(CHARGE));
            this.model.setupAnim(pEntity, 0.0F, 0.0F, age, 0.0F, 0.0F);
            this.model.renderToBuffer(pPoseStack, charge, pPackedLight, OverlayTexture.NO_OVERLAY, color.x, color.y, color.z, 1.0F);

            pPoseStack.popPose();
        }

        if (pEntity.getTime() >= pEntity.getCharge()) {
            double collidePosX = Mth.lerp(pPartialTick, pEntity.prevCollidePosX, pEntity.collidePosX);
            double collidePosY = Mth.lerp(pPartialTick, pEntity.prevCollidePosY, pEntity.collidePosY);
            double collidePosZ = Mth.lerp(pPartialTick, pEntity.prevCollidePosZ, pEntity.collidePosZ);
            double posX = Mth.lerp(pPartialTick, pEntity.xo, pEntity.getX());
            double posY = Mth.lerp(pPartialTick, pEntity.yo, pEntity.getY());
            double posZ = Mth.lerp(pPartialTick, pEntity.zo, pEntity.getZ());

            float length = (float) Math.sqrt(Math.pow(collidePosX - posX, 2) + Math.pow(collidePosY - posY, 2) + Math.pow(collidePosZ - posZ, 2));
            int frame = Mth.floor((pEntity.animation - 1 + pPartialTick) * 2);

            if (frame < 0) {
                frame = pEntity.getFrames() * 2;
            }

            pPoseStack.pushPose();
            pPoseStack.scale(pEntity.getScale(), pEntity.getScale(), pEntity.getScale());
            pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2, 0.0F);

            VertexConsumer beam = pBuffer.getBuffer(JJKRenderTypes.glow(TEXTURE));

            float brightness = 1.0F - ((float) pEntity.getTime() / (pEntity.getCharge() + pEntity.getDuration() + pEntity.getFrames()));

            this.renderBeam(length, yaw * Mth.RAD_TO_DEG, pitch * Mth.RAD_TO_DEG, frame, pPoseStack, beam,
                    brightness, pPackedLight);

            pPoseStack.popPose();
        }
    }

    private void drawCube(float length, int frame, PoseStack poseStack, VertexConsumer consumer, float brightness, int packedLight) {
        float minU = 0.0F;
        float minV = TEXTURE_HEIGHT / 16.0F * frame;
        float maxU = minU + TEXTURE_WIDTH / 16.0F;
        float maxV = minV + TEXTURE_HEIGHT / 16.0F;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        float offset = this.clearerView ? -1.0F : 0.0F;

        vertex(matrix4f, pose, consumer, -BEAM_RADIUS, offset, BEAM_RADIUS, minU, minV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, -BEAM_RADIUS, length, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS, length, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS, offset, BEAM_RADIUS, maxU, minV, brightness, packedLight);

        vertex(matrix4f, pose, consumer, BEAM_RADIUS, offset, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS, length, -BEAM_RADIUS, minU, maxV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, -BEAM_RADIUS, length, -BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, -BEAM_RADIUS, offset, -BEAM_RADIUS, maxU, minV, brightness, packedLight);

        vertex(matrix4f, pose, consumer, -BEAM_RADIUS, length, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, -BEAM_RADIUS, length, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS, length, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS, length, -BEAM_RADIUS, maxU, minV, brightness, packedLight);

        vertex(matrix4f, pose, consumer, -BEAM_RADIUS, offset, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, -BEAM_RADIUS, offset, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS, offset, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS, offset, -BEAM_RADIUS, maxU, minV, brightness, packedLight);

        vertex(matrix4f, pose, consumer, -BEAM_RADIUS, length, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, -BEAM_RADIUS, length, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, -BEAM_RADIUS, offset, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, -BEAM_RADIUS, offset, -BEAM_RADIUS, maxU, minV, brightness, packedLight);

        vertex(matrix4f, pose, consumer, BEAM_RADIUS, length, -BEAM_RADIUS, minU, minV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS, length, BEAM_RADIUS, minU, maxV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS, offset, BEAM_RADIUS, maxU, maxV, brightness, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS, offset, -BEAM_RADIUS, maxU, minV, brightness, packedLight);
    }

    private void renderBeam(float length, float yaw, float pitch, int frame, PoseStack poseStack, VertexConsumer consumer, float brightness, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(pitch));

        this.drawCube(length, frame, poseStack, consumer, brightness, packedLight);

        poseStack.popPose();
    }

    @Override
    protected int getBlockLightLevel(@NotNull FireBeamEntity pEntity, @NotNull BlockPos pPos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FireBeamEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
