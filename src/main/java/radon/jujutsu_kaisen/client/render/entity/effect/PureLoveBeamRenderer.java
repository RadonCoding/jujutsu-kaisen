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
    public PureLoveBeamRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(PureLoveBeamEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.getTime() < pEntity.getCharge()) return;
        

    }

    private void renderBeam(PureLoveBeamEntity entity, PoseStack poseStack, MultiBufferSource source, float partialTicks, float width, float length, boolean inner, boolean glowSecondPass) {
        poseStack.pushPose();

        float v = ((float) entity.tickCount + partialTicks) * -0.25F;
        float v1 = v + length * (inner ? 0.5F : 0.15F);
        float f4 = -width;
        float f5 = 0;
        float f6 = 0.0F;
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        VertexConsumer vertexconsumer = source.getBuffer(JJKRenderTypes.glow(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/bem.png")));
        for (int j = 0; j <= 4; ++j) {
            Matrix3f matrix3f = posestack$pose.normal();
            float f7 = Mth.cos((float) Math.PI + (float) j * ((float) Math.PI * 2F) / (float) 4) * width;
            float f8 = Mth.sin((float) Math.PI + (float) j * ((float) Math.PI * 2F) / (float) 4) * width;
            float f9 = (float) j + 1;
            vertexconsumer.vertex(matrix4f, f4 * 0.55F, f5 * 0.55F, 0.0F)
                    .color(255, 255, 255, 255)
                    .uv(f6, v)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(posestack$pose, 0.0F, -1.0F, 0.0F)
                    .endVertex();
            vertexconsumer.vertex(matrix4f, f4, f5, length)
                    .color(255, 255, 255, 255)
                    .uv(f6, v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(posestack$pose, 0.0F, -1F, 0.0F)
                    .endVertex();
            vertexconsumer.vertex(matrix4f, f7, f8, length)
                    .color(255, 255, 255, 255)
                    .uv(f9, v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(posestack$pose, 0.0F, -1F, 0.0F)
                    .endVertex();
            vertexconsumer.vertex(matrix4f, f7 * 0.55F, f8 * 0.55F, 0.0F)
                    .color(255, 255, 255, 255)
                    .uv(f9, v)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(LightTexture.FULL_BRIGHT)
                    .normal(posestack$pose, 0.0F, -1.0F, 0.0F)
                    .endVertex();
            f4 = f7;
            f5 = f8;
            f6 = f9;
        }
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PureLoveBeamEntity pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
