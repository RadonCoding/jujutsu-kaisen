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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ParticleAnimator;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.effect.PureLoveBeamEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class PureLoveBeamRenderer extends EntityRenderer<PureLoveBeamEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/pure_love.png");
    private static final int TEXTURE_WIDTH = 16;
    private static final int TEXTURE_HEIGHT = 16;
    private static final float BEAM_RADIUS = 0.25F;

    public PureLoveBeamRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(PureLoveBeamEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
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
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2, 0.0F);

        VertexConsumer consumer = pBuffer.getBuffer(JJKRenderTypes.glow(this.getTextureLocation(pEntity)));

        this.renderStart(pEntity, new Vec3(posX, posY + (pEntity.getBbHeight() / 2), posZ), pPartialTick);

        if (pEntity.getTime() > pEntity.getCharge()) {
            this.renderBeam(length, pEntity.getScale(), 180.0F / Mth.PI * yaw, 180.0F / Mth.PI * pitch, pPoseStack, consumer, pPackedLight);

            this.renderEnd(pEntity, new Vec3(collidePosX, collidePosY, collidePosZ), pPartialTick);
        }
        pPoseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PureLoveBeamEntity pEntity) {
        return TEXTURE;
    }

    private void renderStart(PureLoveBeamEntity entity, Vec3 start, float partialTicks) {
        float time = entity.getTime() + partialTicks;

        float intensity = Math.min(1.0F, time / entity.getCharge());

        float radius = entity.getScale() * 2.0F;

        ParticleAnimator.sphere(entity.level(), start, () -> radius * HelperMethods.RANDOM.nextFloat() * 4.0F, () -> radius * 0.2F,
                () -> radius * intensity * HelperMethods.RANDOM.nextFloat() * 0.3F, Math.round(radius * intensity),
                1.0F, true, true, (int) (entity.getCharge() - time), ParticleColors.PURE_LOVE_DARK);

        ParticleAnimator.sphere(entity.level(), start, () -> radius * 0.1F, () -> radius * intensity * 0.25F,
                () -> radius * intensity * 0.2F, Math.round(radius * intensity),
                1.0F, true, true, (int) (entity.getCharge() - time), ParticleColors.PURE_LOVE_BRIGHT);

        ParticleAnimator.lightning(entity.level(), start, radius * intensity * 0.2F, () -> radius * (1.0F + intensity) * HelperMethods.RANDOM.nextFloat() * 4.0F,
                Math.round(radius * intensity * 0.5F), 4, ParticleColors.PURE_LOVE_BRIGHT);
    }

    private void renderEnd(PureLoveBeamEntity entity, Vec3 end, float partialTicks) {
        float time = entity.getTime() + partialTicks;

        float intensity = Math.min(1.0F, time / entity.getCharge());

        float radius = entity.getScale() * 4.0F;

        ParticleAnimator.sphere(entity.level(), end, () -> radius * HelperMethods.RANDOM.nextFloat() * 4.0F, () -> radius * 0.2F,
                () -> radius * intensity * HelperMethods.RANDOM.nextFloat() * 0.3F, Math.round(radius * intensity),
                1.0F, true, true, (int) (entity.getCharge() - time), ParticleColors.PURE_LOVE_DARK);

        ParticleAnimator.sphere(entity.level(), end, () -> radius * 0.1F, () -> radius * intensity * 0.25F,
                () -> radius * intensity * 0.2F, Math.round(radius * intensity),
                1.0F, true, true, (int) (entity.getCharge() - time), ParticleColors.PURE_LOVE_BRIGHT);
    }

    private void drawBeam(float length, float scale, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        float minU = 0.0F;
        float minV = 0.0F;
        float maxU = minU + TEXTURE_WIDTH / 16.0F;
        float maxV = minV + TEXTURE_HEIGHT / 16.0F;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        vertex(matrix4f, pose, consumer, -BEAM_RADIUS * scale, 0.0F, 0.0F, minU, minV, 1.0F, packedLight);
        vertex(matrix4f, pose, consumer, -BEAM_RADIUS * scale, length, 0.0F, minU, maxV, 1.0F, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS * scale, length, 0.0F, maxU, maxV, 1.0F, packedLight);
        vertex(matrix4f, pose, consumer, BEAM_RADIUS * scale, 0.0F, 0.0F, maxU, minV, 1.0F, packedLight);
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