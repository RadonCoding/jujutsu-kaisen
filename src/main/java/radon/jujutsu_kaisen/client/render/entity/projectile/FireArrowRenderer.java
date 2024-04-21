package radon.jujutsu_kaisen.client.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.projectile.FireArrowProjectile;

public class FireArrowRenderer extends EntityRenderer<FireArrowProjectile> {
    private static final int TEXTURE_WIDTH = 32;
    private static final int STARTUP_TEXTURE_HEIGHT = 256;
    private static final int STILL_TEXTURE_HEIGHT = 128;
    private static final ResourceLocation STARTUP = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/fire_arrow_startup.png");
    private static final ResourceLocation STILL = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/entity/fire_arrow.png");

    public FireArrowRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@NotNull FireArrowProjectile pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft mc = Minecraft.getInstance();

        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, pEntity.getBbHeight() / 2.0F, 0.0F);

        float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.mulPose(Axis.YN.rotationDegrees(yaw + 90.0F));
        pPoseStack.mulPose(Axis.ZN.rotationDegrees(pitch));

        pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

        pPoseStack.scale(1.5F, 1.5F, 1.5F);

        boolean still = pEntity.getTime() + pPartialTick >= FireArrowProjectile.DELAY;
        RenderType type = JJKRenderTypes.glow(still ? STILL : STARTUP);

        VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(type);
        Matrix4f pose = pPoseStack.last().pose();

        int frame = Mth.floor((pEntity.animation - 1 + pPartialTick) * 2);

        if (frame < 0) {
            frame = (still ? FireArrowProjectile.STILL_FRAMES : FireArrowProjectile.STARTUP_FRAMES) * 2;
        }

        float minU = 0.0F;
        float minV = 32.0F / (still ? STILL_TEXTURE_HEIGHT : STARTUP_TEXTURE_HEIGHT) * frame;
        float maxU = minU + 32.0F / TEXTURE_WIDTH;
        float maxV = minV + 32.0F / (still ? STILL_TEXTURE_HEIGHT : STARTUP_TEXTURE_HEIGHT);

        consumer.vertex(pose, -1.0F, 0.0F, -1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, -1.0F, 0.0F, 1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, 1.0F, 0.0F, 1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(pose, 1.0F, 0.0F, -1.0F)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_SKY)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();

        pPoseStack.popPose();
    }

    @Override
    protected int getBlockLightLevel(@NotNull FireArrowProjectile pEntity, @NotNull BlockPos pPos) {
        return 15;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FireArrowProjectile pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
