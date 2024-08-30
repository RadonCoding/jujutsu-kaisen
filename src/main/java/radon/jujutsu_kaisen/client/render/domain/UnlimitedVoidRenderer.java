package radon.jujutsu_kaisen.client.render.domain;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.util.RenderUtil;

public class UnlimitedVoidRenderer extends DomainRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/domain/unlimited_void.png");
    private static final float UNLIMITED_VOID_SPLATTER_RADIUS = 64.0F;
    private static final int UNLIMITED_VOID_SPLATTER_FRAMES = 3;

    @Override
    public void renderPostEffects(Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        super.renderPostEffects(modelViewMatrix, projectionMatrix);

        PoseStack poseStack = new PoseStack();

        RandomSource random = RandomSource.create(2048L);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();

        for (int i = 0; i < 8; i++) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(JujutsuKaisen.MOD_ID, String.format("textures/domain/cum_splatter_%d.png",
                    (i % UNLIMITED_VOID_SPLATTER_FRAMES) + 1)));

            RenderSystem.setShader(GameRenderer::getPositionTexShader);

            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

            poseStack.translate((random.nextFloat() - 0.5F) * 256.0F, (random.nextFloat() - 0.5F) * 128.0F, 0.0F);

            Matrix4f matrix4f = poseStack.last().pose();

            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            float r = UNLIMITED_VOID_SPLATTER_RADIUS / 2.0F;

            builder.vertex(matrix4f, r, r, 100.0F)
                    .uv(0.0F, 0.0F)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .endVertex();
            builder.vertex(matrix4f, r, -r, 100.0F)
                    .uv(0.0F, 1.0F)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .endVertex();
            builder.vertex(matrix4f, -r, -r, 100.0F)
                    .uv(1.0F, 1.0F)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .endVertex();
            builder.vertex(matrix4f, -r, r, 100.0F)
                    .uv(1.0F, 0.0F)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .endVertex();

            RenderUtil.drawWithShader(modelViewMatrix, projectionMatrix, builder.end());

            poseStack.popPose();
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
