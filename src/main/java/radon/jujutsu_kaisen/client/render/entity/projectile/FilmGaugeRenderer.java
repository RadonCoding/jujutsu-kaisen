package radon.jujutsu_kaisen.client.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.projectile.FilmGaugeProjectile;
import radon.jujutsu_kaisen.util.RotationUtil;

public class FilmGaugeRenderer extends EntityRenderer<FilmGaugeProjectile> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/film_gauge.png");

    public FilmGaugeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull FilmGaugeProjectile pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, pEntity.getBbHeight() / 2.0F, 0.0D);
        Vec3 startPos = pEntity.getStart();
        Vec3 projectilePos = getPosition(pEntity, pEntity.getBbHeight() / 2.0F, pPartialTick);
        Vec3 relative = startPos.subtract(projectilePos);
        float f0 = (float) relative.length();
        relative = relative.normalize();
        float f1 = (float) Math.acos(relative.y);
        float f2 = (float) Math.atan2(relative.z, relative.x);
        pPoseStack.mulPose(Axis.YP.rotationDegrees((((float) Math.PI / 2.0F) - f2) * (180.0F / (float) Math.PI)));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(f1 * (180.0F / (float) Math.PI)));

        float f3 = -1.0F;
        int j = 255;
        int k = 255;
        int l = 255;
        float f4 = 0.0F;
        float f5 = 0.2F;
        float f6 = 0.0F;
        float f7 = -0.2F;
        float f8 = Mth.cos(f3 + ((float) Math.PI / 2.0F)) * 0.2F;
        float f9 = Mth.sin(f3 + ((float) Math.PI / 2.0F)) * 0.2F;
        float f10 = Mth.cos(f3 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f11 = Mth.sin(f3 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f12 = 0.0F;
        float f13 = f0 + f12;
        float f14 = 0.75F;
        float f15 = f0 + f14;

        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        PoseStack.Pose pose = pPoseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        pPoseStack.pushPose();
        vertex(consumer, matrix4f, matrix3f, f4, f0, f5, j, k, l, 0.4999F, f13, pPackedLight);
        vertex(consumer, matrix4f, matrix3f, f4, 0.0F, f5, j, k, l, 0.4999F, f12, pPackedLight);
        vertex(consumer, matrix4f, matrix3f, f6, 0.0F, f7, j, k, l, 0.0F, f12, pPackedLight);
        vertex(consumer, matrix4f, matrix3f, f6, f0, f7, j, k, l, 0.0F, f13, pPackedLight);

        vertex(consumer, matrix4f, matrix3f, f8, f0, f9, j, k, l, 0.4999F, f15, pPackedLight);
        vertex(consumer, matrix4f, matrix3f, f8, 0.0F, f9, j, k, l, 0.4999F, f14, pPackedLight);
        vertex(consumer, matrix4f, matrix3f, f10, 0.0F, f11, j, k, l, 0.0F, f14, pPackedLight);
        vertex(consumer, matrix4f, matrix3f, f10, f0, f11, j, k, l, 0.0F, f15, pPackedLight);
        pPoseStack.popPose();
        pPoseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float z, int r, int g, int b, float u, float v, int packedLight) {
        consumer.vertex(matrix4f, x, y, z)
                .color(r, g, b, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private static Vec3 getPosition(Entity entity, double yOffset, float pPartialTick) {
        double d0 = entity.xOld + (entity.getX() - entity.xOld) * (double) pPartialTick;
        double d1 = yOffset + entity.yOld + (entity.getY() - entity.yOld) * (double) pPartialTick;
        double d2 = entity.zOld + (entity.getZ() - entity.zOld) * (double) pPartialTick;
        return new Vec3(d0, d1, d2);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FilmGaugeProjectile pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
