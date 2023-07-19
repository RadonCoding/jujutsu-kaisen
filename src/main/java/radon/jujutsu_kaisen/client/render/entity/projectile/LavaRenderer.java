package radon.jujutsu_kaisen.client.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.entity.projectile.LavaProjectile;

public class LavaRenderer extends EntityRenderer<LavaProjectile> {
    private static final float SIZE = 0.25F;

    public LavaRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull LavaProjectile pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft mc = Minecraft.getInstance();
        TextureAtlasSprite sprite = mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(this.getTextureLocation(pEntity));

        pPoseStack.pushPose();
        pPoseStack.translate(0.0D, pEntity.getBbHeight() / 2.0F, 0.0D);

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());
        pPoseStack.mulPose(Axis.YP.rotationDegrees(360.0F - yaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch - 90.0F));

        VertexConsumer consumer = pBuffer.getBuffer(Sheets.cutoutBlockSheet());
        Matrix4f pose = pPoseStack.last().pose();

        cube(sprite, pose, consumer);

        pPoseStack.popPose();
    }

    private static void cube(TextureAtlasSprite sprite, Matrix4f pose, VertexConsumer consumer) {
        float minU = sprite.getU0();
        float minV = sprite.getV0();
        float maxU = sprite.getU1();
        float maxV = sprite.getV1();

        // Front face
        vertex(consumer, pose, -SIZE, -SIZE, SIZE, minU, maxV, 0.0F, 0.0F, 1.0F);
        vertex(consumer, pose, SIZE, -SIZE, SIZE, maxU, maxV, 0.0F, 0.0F, 1.0F);
        vertex(consumer, pose, SIZE, SIZE, SIZE, maxU, minV, 0.0F, 0.0F, 1.0F);
        vertex(consumer, pose, -SIZE, SIZE, SIZE, minU, minV, 0.0F, 0.0F, 10.0F);

        // Back face
        vertex(consumer, pose, -SIZE, -SIZE, -SIZE, minU, maxV, 0.0F, 0.0F, -10.0F);
        vertex(consumer, pose, -SIZE, SIZE, -SIZE, minU, minV, 0.0F, 0.0F, -10.0F);
        vertex(consumer, pose, SIZE, SIZE, -SIZE, maxU, minV, 0.0F, 0.0F, -10.0F);
        vertex(consumer, pose, SIZE, -SIZE, -SIZE, maxU, maxV, 0.0F, 0.0F, -10.0F);

        // Left face
        vertex(consumer, pose, -SIZE, -SIZE, SIZE, minU, maxV, -10.F, 00.F, 00.F);
        vertex(consumer, pose, -SIZE, SIZE, SIZE, maxU, maxV, -10.F, 00.F, 00.F);
        vertex(consumer, pose, -SIZE, SIZE, -SIZE, maxU, minV, -1.0F, 0.0F, -0.0F);
        vertex(consumer, pose, -SIZE, -SIZE, -SIZE, minU, minV, -1.0F, -0.0F, -0.0F);

        // Right face
        vertex(consumer, pose, SIZE, -SIZE, SIZE, minU, maxV, 1.0F, 0.0F, 0.0F);
        vertex(consumer, pose, SIZE, -SIZE, -SIZE, maxU, maxV, 1.0F, 0.0F, 0.0F);
        vertex(consumer, pose, SIZE, SIZE, -SIZE, maxU, minV, 1.0F, 0.0F, 0.0F);
        vertex(consumer, pose, SIZE, SIZE, SIZE, minU, minV, 1.0F, 0.0F, 0.0F);

        // Top face
        vertex(consumer, pose, -SIZE, SIZE, SIZE, minU, maxV, 0.0F, 10.0F, 0.0F);
        vertex(consumer, pose, SIZE, SIZE, SIZE, maxU, maxV, 0.0F, 10.0F, 0.0F);
        vertex(consumer, pose, SIZE, SIZE, -SIZE, maxU, minV, 0.0F, 10.0F, 0.0F);
        vertex(consumer, pose, -SIZE, SIZE, -SIZE, minU, minV, 0.0F, 10.0F, 0.0F);

        // Bottom face
        vertex(consumer, pose, -SIZE, -SIZE, SIZE, minU, maxV, 0.0F, -10.0F, 0.0F);
        vertex(consumer, pose, -SIZE, -SIZE, -SIZE, maxU, maxV, 0.0F, -10.0F, 0.0F);
        vertex(consumer, pose, SIZE, -SIZE, -SIZE, maxU, minV, 0.0F, -10.0F, 0.0F);
        vertex(consumer, pose, SIZE, -SIZE, SIZE, minU, minV, 0.0F, -10.0F, 0.0F);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        consumer.vertex(pose, x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(nx, ny, nz).endVertex();
    }

    @Override
    protected int getBlockLightLevel(@NotNull LavaProjectile pEntity, @NotNull BlockPos pPos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LavaProjectile pEntity) {
        return new ResourceLocation("block/lava_flow");
    }
}
