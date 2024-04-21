package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.client.effect.JJKPostEffects;
import radon.jujutsu_kaisen.client.effect.PostEffectHandler;
import radon.jujutsu_kaisen.entity.HollowPurpleExplosion;

public class HollowPurpleExplosionRenderer extends EntityRenderer<HollowPurpleExplosion> {
    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);

    public HollowPurpleExplosionRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull HollowPurpleExplosion pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        float f0 = ((float) pEntity.getTime() + pPartialTick) / HollowPurpleExplosion.DURATION;
        float f1 = Math.min(f0 > 0.8F ? (f0 - 0.8F) / 0.2F : 0.0F, 1.0F);
        RandomSource random = RandomSource.create(432L);
        VertexConsumer consumer = pBuffer.getBuffer(JJKRenderTypes.lightning());

        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2.0F, 0.0F);

        for (int i = 0; (float) i < (f0 + f0 * f0) / 2.0F * 60.0F; i++) {
            pPoseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F + f0 * 90.0F));
            float f2 = random.nextFloat() * 20.0F + 5.0F + f1 * 10.0F;
            float f3 = random.nextFloat() * 2.0F + 1.0F + f1 * 2.0F;
            Matrix4f matrix4f = pPoseStack.last().pose();
            int j = (int) (255.0F * (1.0F - f1));
            vertex01(consumer, matrix4f, j);
            vertex2(consumer, matrix4f, f2, f3);
            vertex3(consumer, matrix4f, f2, f3);
            vertex01(consumer, matrix4f, j);
            vertex3(consumer, matrix4f, f2, f3);
            vertex4(consumer, matrix4f, f2, f3);
            vertex01(consumer, matrix4f, j);
            vertex4(consumer, matrix4f, f2, f3);
            vertex2(consumer, matrix4f, f2, f3);
        }
        pPoseStack.popPose();
    }

    private static void vertex01(VertexConsumer pConsumer, Matrix4f pMatrix, int pAlpha) {
        pConsumer.vertex(pMatrix, 0.0F, 0.0F, 0.0F).color(255, 255, 255, pAlpha).endVertex();
    }

    private static void vertex2(VertexConsumer pConsumer, Matrix4f pMatrix, float p_253704_, float p_253701_) {
        pConsumer.vertex(pMatrix, -HALF_SQRT_3 * p_253701_, p_253704_, -0.5F * p_253701_).color(255, 0, 255, 0).endVertex();
    }

    private static void vertex3(VertexConsumer pConsumer, Matrix4f pMatrix, float p_253729_, float p_254030_) {
        pConsumer.vertex(pMatrix, HALF_SQRT_3 * p_254030_, p_253729_, -0.5F * p_254030_).color(255, 0, 255, 0).endVertex();
    }

    private static void vertex4(VertexConsumer pConsumer, Matrix4f pMatrix, float p_253649_, float p_253694_) {
        pConsumer.vertex(pMatrix, 0.0F, p_253649_, p_253694_).color(255, 0, 255, 0).endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HollowPurpleExplosion pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
