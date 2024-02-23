package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class SkyHandler {
    private static final ResourceLocation UNLIMITED_VOID = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/misc/unlimited_void.png");

    private static final float UNLIMITED_VOID_SPLATTER_RADIUS = 64.0F;
    private static final int UNLIMITED_VOID_SPLATTER_FRAMES = 2;

    private static TextureTarget unlimitedVoidTarget;
    private static VertexBuffer unlimitedVoidBuffer;
    private static VertexBuffer unlimitedVoidSplatterBuffer;

    private static int skyWidth = -1;
    private static int skyHeight = -1;

    public static TextureTarget getUnlimitedVoidTarget() {
        return unlimitedVoidTarget;
    }

    private static VertexBuffer getUnlimitedVoidBuffer() {
        if (unlimitedVoidBuffer == null) {
            BufferBuilder builder = Tesselator.getInstance().getBuilder();
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            int latitudeDivisions = 64;
            int longitudeDivisions = 64;

            for (int lat = 0; lat < latitudeDivisions; lat++) {
                double theta = lat * Math.PI / latitudeDivisions;
                double sinTheta = Math.sin(theta);
                double cosTheta = Math.cos(theta);

                double theta2 = (lat + 1) * Math.PI / latitudeDivisions;
                double sinTheta2 = Math.sin(theta2);
                double cosTheta2 = Math.cos(theta2);

                for (int lon = 0; lon < longitudeDivisions; lon++) {
                    double phi = lon * 2 * Math.PI / longitudeDivisions;
                    double sinPhi = Math.sin(phi);
                    double cosPhi = Math.cos(phi);

                    double phi2 = (lon + 1) * 2 * Math.PI / longitudeDivisions;
                    double sinPhi2 = Math.sin(phi2);
                    double cosPhi2 = Math.cos(phi2);

                    double x1 = cosPhi * sinTheta;
                    double y1 = cosTheta;
                    double z1 = sinPhi * sinTheta;

                    double x2 = cosPhi * sinTheta2;
                    double y2 = cosTheta2;
                    double z2 = sinPhi * sinTheta2;

                    double x3 = cosPhi2 * sinTheta2;
                    double y3 = cosTheta2;
                    double z3 = sinPhi2 * sinTheta2;

                    double x4 = cosPhi2 * sinTheta;
                    double y4 = cosTheta;
                    double z4 = sinPhi2 * sinTheta;

                    double u1 = lon / (double) longitudeDivisions;
                    double v1 = lat / (double) latitudeDivisions;
                    double u2 = (lon + 1) / (double) longitudeDivisions;
                    double v2 = (lat + 1) / (double) latitudeDivisions;

                    builder.vertex(x1 * 100, y1 * 100.0F, z1 * 100.0F).uv((float) u1, (float) v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
                    builder.vertex(x2 * 100.0F, y2 * 100.0F, z2 * 100.0F).uv((float) u1, (float) v2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
                    builder.vertex(x3 * 100.0F, y3 * 100.0F, z3 * 100.0F).uv((float) u2, (float) v2).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
                    builder.vertex(x4 * 100.0F, y4 * 100.0F, z4 * 100.0F).uv((float) u2, (float) v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
                }
            }

            BufferBuilder.RenderedBuffer buffer = builder.end();
            unlimitedVoidBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            unlimitedVoidBuffer.bind();
            unlimitedVoidBuffer.upload(buffer);
        }
        return unlimitedVoidBuffer;
    }

    private static VertexBuffer getUnlimitedVoidSplatterBuffer() {
        if (unlimitedVoidSplatterBuffer == null) {
            BufferBuilder builder = Tesselator.getInstance().getBuilder();
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            float r = UNLIMITED_VOID_SPLATTER_RADIUS / 2.0F;

            builder.vertex(r, r, 100.0F).uv(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            builder.vertex(r, -r, 100.0F).uv(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            builder.vertex(-r, -r, 100.0F).uv(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
            builder.vertex(-r, r, 100.0F).uv(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();

            BufferBuilder.RenderedBuffer buffer = builder.end();
            unlimitedVoidSplatterBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            unlimitedVoidSplatterBuffer.bind();
            unlimitedVoidSplatterBuffer.upload(buffer);
        }
        return unlimitedVoidSplatterBuffer;
    }

    private static void createUnlimitedVoidSky(PoseStack poseStack, float partialTicks, Matrix4f matrix4f) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        int ww = window.getWidth();
        int wh = window.getHeight();

        if (ww <= 0 || wh <= 0) {
            return;
        }

        boolean update = false;

        if (unlimitedVoidTarget == null || skyWidth != ww || skyHeight != wh) {
            update = true;
            skyWidth = ww;
            skyHeight = wh;
        }

        if (update) {
            if (unlimitedVoidTarget != null) {
                unlimitedVoidTarget.destroyBuffers();
            }
            unlimitedVoidTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        mc.gameRenderer.setRenderBlockOutline(false);
        unlimitedVoidTarget.bindWrite(true);

        RenderTarget current = mc.getMainRenderTarget();
        renderActualSky(poseStack, partialTicks, matrix4f);

        mc.gameRenderer.setRenderBlockOutline(true);
        unlimitedVoidTarget.unbindRead();
        unlimitedVoidTarget.unbindWrite();
        current.bindWrite(true);
    }

    public static void renderSky(PoseStack poseStack, float partialTicks, Matrix4f matrix4f) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        int ww = window.getWidth();
        int wh = window.getHeight();

        if (ww <= 0 || wh <= 0) return;

        createUnlimitedVoidSky(poseStack, partialTicks, matrix4f);
    }

    public static void renderActualSky(PoseStack poseStack, float partialTicks, Matrix4f projection) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return;

        FogRenderer.levelFogColor();
        RenderSystem.clear(16640, Minecraft.ON_OSX);
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        RenderSystem.setShaderTexture(0, UNLIMITED_VOID);

        getUnlimitedVoidBuffer().bind();
        getUnlimitedVoidBuffer().drawWithShader(poseStack.last().pose(), projection, RenderSystem.getShader());
        VertexBuffer.unbind();

        RandomSource random = RandomSource.create(2048L);

        int frame = 0;

        for (int i = 0; i < 8; i++) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(JujutsuKaisen.MOD_ID, String.format("textures/misc/unlimited_void_splatter_%d.png", frame + 1)));

            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));

            poseStack.translate((random.nextFloat() - 0.5F) * 256.0F, (random.nextFloat() - 0.5F) * 128.0F, 0.0F);

            getUnlimitedVoidSplatterBuffer().bind();
            getUnlimitedVoidSplatterBuffer().drawWithShader(poseStack.last().pose(), projection, RenderSystem.getShader());
            VertexBuffer.unbind();

            poseStack.popPose();

            if (++frame == UNLIMITED_VOID_SPLATTER_FRAMES) frame = 0;
        }
    }
}
