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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.effect.CursedEnergyBombEntity;
import radon.jujutsu_kaisen.entity.effect.PureLoveBeamEntity;

public class CursedEnergyBombRenderer extends EntityRenderer<CursedEnergyBombEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/cursed_energy_bomb.png");
    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 32;
    private static final float START_RADIUS = 1.3F;
    private static final float BEAM_RADIUS = 1.0F;
    private boolean clearerView = false;

    public CursedEnergyBombRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CursedEnergyBombEntity pEntity) {
        return TEXTURE;
    }

    @Override
    public void render(CursedEnergyBombEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        if (!(pEntity.getOwner() instanceof LivingEntity owner)) return;

        Vector3f color = ParticleColors.getCursedEnergyColor(owner);

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

        this.renderStart(frame, pPoseStack, consumer, color, pPackedLight);

        if (pEntity.getTime() > pEntity.getCharge()) {
            this.renderBeam(length, 180.0F / Mth.PI * yaw, 180.0F / Mth.PI * pitch, frame, pPoseStack, consumer, color, pPackedLight);

            pPoseStack.pushPose();
            pPoseStack.translate(collidePosX - posX, collidePosY - posY, collidePosZ - posZ);
            this.renderEnd(frame, pEntity.side, pPoseStack, consumer, color, pPackedLight);
            pPoseStack.popPose();
        }
        pPoseStack.popPose();
    }

    private void renderFlatQuad(int frame, PoseStack poseStack, VertexConsumer consumer, Vector3f color, int packedLight) {
        float minU = 16.0F / TEXTURE_WIDTH * frame;
        float minV = 0.0F;
        float maxU = minU + 16.0F / TEXTURE_WIDTH;
        float maxV = minV + 16.0F / TEXTURE_HEIGHT;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        this.drawVertex(matrix4f, matrix3f, consumer, -START_RADIUS, -START_RADIUS, 0.0F, color, minU, minV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -START_RADIUS, START_RADIUS, 0.0F, color, minU, maxV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, START_RADIUS, START_RADIUS, 0.0F, color, maxU, maxV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, START_RADIUS, -START_RADIUS, 0.0F, color, maxU, minV, 1.0F, packedLight);
    }

    private void renderStart(int frame, PoseStack poseStack, VertexConsumer consumer, Vector3f color, int packedLight) {
        if (this.clearerView) {
            return;
        }
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        this.renderFlatQuad(frame, poseStack, consumer, color, packedLight);
        poseStack.popPose();
    }

    private void renderEnd(int frame, Direction side, PoseStack poseStack, VertexConsumer consumer, Vector3f color, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        this.renderFlatQuad(frame, poseStack, consumer, color, packedLight);
        poseStack.popPose();

        if (side == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.mulPose(side.getRotation().mul(Axis.XP.rotationDegrees(90.0F)));
        poseStack.translate(0.0F, 0.0F, -0.01F);
        this.renderFlatQuad(frame, poseStack, consumer, color, packedLight);
        poseStack.popPose();
    }

    private void drawBeam(float length, int frame, PoseStack poseStack, VertexConsumer consumer, Vector3f color, int packedLight) {
        float minU = 0.0F;
        float minV = 16.0F / TEXTURE_HEIGHT + 1.0F / TEXTURE_HEIGHT * frame;
        float maxU = minU + 20.0F / TEXTURE_WIDTH;
        float maxV = minV + 1.0F / TEXTURE_HEIGHT;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        float offset = this.clearerView ? -1.0F : 0.0F;
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, 0.0F, color, minU, minV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, 0.0F, color, minU, maxV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, 0.0F, color, maxU, maxV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, 0.0F, color, maxU, minV, 1.0F, packedLight);
    }

    private void renderBeam(float length, float yaw, float pitch, int frame, PoseStack poseStack, VertexConsumer consumer, Vector3f color, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(pitch));

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Minecraft.getInstance().gameRenderer.getMainCamera().getXRot() + 90.0F));
        this.drawBeam(length, frame, poseStack, consumer, color, packedLight);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YN.rotationDegrees(Minecraft.getInstance().gameRenderer.getMainCamera().getXRot() - 90.0F));
        this.drawBeam(length, frame, poseStack, consumer, color, packedLight);
        poseStack.popPose();

        poseStack.popPose();
    }

    public void drawVertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer, float x, float y, float z, Vector3f color, float u, float v, float alpha, int packedLight) {
        consumer.vertex(matrix4f, x, y, z)
                .color(color.x, color.y, color.z, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
