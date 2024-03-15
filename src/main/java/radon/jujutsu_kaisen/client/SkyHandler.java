package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class SkyHandler {
    private static final ResourceLocation UNLIMITED_VOID = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/misc/unlimited_void.png");
    private static final ResourceLocation SELF_EMDOBIMENT_OF_PERFECTION = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/misc/self_embodiment_of_perfection.png");
    private static final ResourceLocation HORIZON_OF_THE_CAPTIVATING_SKANDHA = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/misc/horizon_of_the_captivating_skandha.png");
    private static final ResourceLocation SHINING_SEA_OF_FLOWERS = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/misc/shining_sea_of_flowers.png");
    private static final ResourceLocation AUTHENTIC_MUTUAL_LOVE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/misc/authentic_mutual_love.png");

    private static final float UNLIMITED_VOID_SPLATTER_RADIUS = 64.0F;
    private static final int UNLIMITED_VOID_SPLATTER_FRAMES = 2;

    private static TextureTarget unlimitedVoidTarget;
    private static VertexBuffer unlimitedVoidBuffer;
    private static VertexBuffer unlimitedVoidSplatterBuffer;

    private static TextureTarget selfEmbodimentOfPerfectionTarget;
    private static VertexBuffer selfEmbodimentOfPerfectionBuffer;

    private static TextureTarget horizonOfTheCaptivatingSkandhaTarget;
    private static VertexBuffer horizonOfTheCaptivatingSkandhaBuffer;

    private static TextureTarget shiningSeaOfFlowersTarget;
    private static VertexBuffer shiningSeaOfFlowersBuffer;

    private static TextureTarget authenticMutualLoveTarget;
    private static VertexBuffer authenticMutualLoveBuffer;

    private static int skyWidth = -1;
    private static int skyHeight = -1;

    public static TextureTarget getUnlimitedVoidTarget() {
        return unlimitedVoidTarget;
    }

    public static TextureTarget getSelfEmbodimentOfPerfectionTarget() {
        return selfEmbodimentOfPerfectionTarget;
    }

    public static TextureTarget getHorizonOfTheCaptivatingSkandhaTarget() {
        return horizonOfTheCaptivatingSkandhaTarget;
    }

    public static TextureTarget getShiningSeaOfFlowersTarget() {
        return shiningSeaOfFlowersTarget;
    }

    public static TextureTarget getAuthenticMutualLoveTarget() {
        return authenticMutualLoveTarget;
    }

    private static BufferBuilder.RenderedBuffer createSphericalBuffer() {
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        int radius = 50;
        int rings = 20;
        int segments = 20;


        float ringFactor = (float)(1.0 / rings);
        float segmentFactor = (float)(1.0 / segments);
        int x, y, z;
        float u, v;

        for (int ring = 0; ring < rings; ring++) {
            for (int segment = 0; segment < segments; segment++) {
                u = segment * segmentFactor;
                v = ring * ringFactor;

                float theta1 = (float) (ring * Math.PI * ringFactor - Math.PI / 2);
                float theta2 = (float) ((ring + 1) * Math.PI * ringFactor - Math.PI / 2);
                float phi1 = (float) (segment * 2 * Math.PI * segmentFactor);
                float phi2 = (float) ((segment + 1) * 2 * Math.PI * segmentFactor);

                float x1 = (float) (Math.cos(theta1) * Math.cos(phi1));
                float y1 = (float) (Math.sin(theta1));
                float z1 = (float) (Math.cos(theta1) * Math.sin(phi1));

                float x2 = (float) (Math.cos(theta2) * Math.cos(phi1));
                float y2 = (float) (Math.sin(theta2));
                float z2 = (float) (Math.cos(theta2) * Math.sin(phi1));

                float x3 = (float) (Math.cos(theta2) * Math.cos(phi2));
                float y3 = (float) (Math.sin(theta2));
                float z3 = (float) (Math.cos(theta2) * Math.sin(phi2));

                float x4 = (float) (Math.cos(theta1) * Math.cos(phi2));
                float y4 = (float) (Math.sin(theta1));
                float z4 = (float) (Math.cos(theta1) * Math.sin(phi2));

                builder.vertex(x1 * radius, y1 * radius, z1 * radius).uv(u, 1 - v).color(255, 255, 255, 255).endVertex();
                builder.vertex(x2 * radius, y2 * radius, z2 * radius).uv(u, 1 - (v + ringFactor)).color(255, 255, 255, 255).endVertex();
                builder.vertex(x3 * radius, y3 * radius, z3 * radius).uv(u + segmentFactor, 1 - (v + ringFactor)).color(255, 255, 255, 255).endVertex();
                builder.vertex(x4 * radius, y4 * radius, z4 * radius).uv(u + segmentFactor, 1 - v).color(255, 255, 255, 255).endVertex();
            }
        }

        return builder.end();
    }

    private static BufferBuilder.RenderedBuffer createBoxBuffer() {
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        builder.vertex(-100.0F, -100.0F, -100.0F).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
        builder.vertex(-100.0F, -100.0F, 100.0F).uv(0.0F, 0.5F).color(255, 255, 255, 255).endVertex();
        builder.vertex(100.0F, -100.0F, 100.0F).uv(1.0F / 3.0F, 0.5F).color(255, 255, 255, 255).endVertex();
        builder.vertex(100.0F, -100.0F, -100.0F).uv(1.0F / 3.0F, 0.0F).color(255, 255, 255, 255).endVertex();

        builder.vertex(-100.0F, 100.0F, 100.0F).uv(1.0F / 3.0F, 0.0F).color(255, 255, 255, 255).endVertex();
        builder.vertex(-100.0F, 100.0F, -100.0F).uv(1.0F / 3.0F, 0.5F).color(255, 255, 255, 255).endVertex();
        builder.vertex(100.0F, 100.0F, -100.0F).uv(2.0F / 3.0F, 0.5F).color(255, 255, 255, 255).endVertex();
        builder.vertex(100.0F, 100.0F, 100.0F).uv(2.0F / 3.0F, 0.0F).color(255, 255, 255, 255).endVertex();

        builder.vertex(100.0F, 100.0F, 100.0F).uv(2.0F / 3.0F, 0.0F).color(255, 255, 255, 255).endVertex();
        builder.vertex(100.0F, -100.0F, 100.0F).uv(2.0F / 3.0F, 0.5F).color(255, 255, 255, 255).endVertex();
        builder.vertex(-100.0F, -100.0F, 100.0F).uv(1.0F, 0.5F).color(255, 255, 255, 255).endVertex();
        builder.vertex(-100.0F, 100.0F, 100.0F).uv(1.0F, 0.0F).color(255, 255, 255, 255).endVertex();

        builder.vertex(-100.0F, 100.0F, 100.0F).uv(0.0F, 0.5F).color(255, 255, 255, 255).endVertex();
        builder.vertex(-100.0F, -100.0F, 100.0F).uv(0.0F, 1.0F).color(255, 255, 255, 255).endVertex();
        builder.vertex(-100.0F, -100.0F, -100.0F).uv(1.0F / 3.0F, 1.0F).color(255, 255, 255, 255).endVertex();
        builder.vertex(-100.0F, 100.0F, -100.0F).uv(1.0F / 3.0F, 0.5F).color(255, 255, 255, 255).endVertex();

        builder.vertex(-100.0F, 100.0F, -100.0F).uv(1.0F / 3.0F, 0.5F).color(255, 255, 255, 255).endVertex();
        builder.vertex(-100.0F, -100.0F, -100.0F).uv(1.0F / 3.0F, 1.0F).color(255, 255, 255, 255).endVertex();
        builder.vertex(100.0F, -100.0F, -100.0F).uv(2.0F / 3.0F, 1.0F).color(255, 255, 255, 255).endVertex();
        builder.vertex(100.0F, 100.0F, -100.0F).uv(2.0F / 3.0F, 0.5F).color(255, 255, 255, 255).endVertex();

        builder.vertex(100.0F, 100.0F, -100.0F).uv(2.0F / 3.0F, 0.5F).color(255, 255, 255, 255).endVertex();
        builder.vertex(100.0F, -100.0F, -100.0F).uv(2.0F / 3.0F, 1.0F).color(255, 255, 255, 255).endVertex();
        builder.vertex(100.0F, -100.0F, 100.0F).uv(1.0F, 1.0F).color(255, 255, 255, 255).endVertex();
        builder.vertex(100.0F, 100.0F, 100.0F).uv(1.0F, 0.5F).color(255, 255, 255, 255).endVertex();

        return builder.end();
    }

    private static VertexBuffer getUnlimitedVoidBuffer() {
        if (unlimitedVoidBuffer == null) {
            BufferBuilder.RenderedBuffer buffer = createSphericalBuffer();
            unlimitedVoidBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            unlimitedVoidBuffer.bind();
            unlimitedVoidBuffer.upload(buffer);
            VertexBuffer.unbind();
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
            VertexBuffer.unbind();
        }
        return unlimitedVoidSplatterBuffer;
    }

    private static VertexBuffer getSelfEmbodimentOfPerfectionBuffer() {
        if (selfEmbodimentOfPerfectionBuffer == null) {
            BufferBuilder.RenderedBuffer buffer = createSphericalBuffer();
            selfEmbodimentOfPerfectionBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            selfEmbodimentOfPerfectionBuffer.bind();
            selfEmbodimentOfPerfectionBuffer.upload(buffer);
            VertexBuffer.unbind();
        }
        return selfEmbodimentOfPerfectionBuffer;
    }

    private static VertexBuffer getHorizonOfTheCaptivatingSkandhaBuffer() {
        if (horizonOfTheCaptivatingSkandhaBuffer == null) {
            BufferBuilder.RenderedBuffer buffer = createBoxBuffer();
            horizonOfTheCaptivatingSkandhaBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            horizonOfTheCaptivatingSkandhaBuffer.bind();
            horizonOfTheCaptivatingSkandhaBuffer.upload(buffer);
            VertexBuffer.unbind();
        }
        return horizonOfTheCaptivatingSkandhaBuffer;
    }

    private static VertexBuffer getShiningSeaOfFlowersBuffer() {
        if (shiningSeaOfFlowersBuffer == null) {
            BufferBuilder.RenderedBuffer buffer = createBoxBuffer();
            shiningSeaOfFlowersBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            shiningSeaOfFlowersBuffer.bind();
            shiningSeaOfFlowersBuffer.upload(buffer);
            VertexBuffer.unbind();
        }
        return shiningSeaOfFlowersBuffer;
    }

    private static VertexBuffer getAuthenticMutualLoveBuffer() {
        if (authenticMutualLoveBuffer == null) {
            BufferBuilder.RenderedBuffer buffer = createBoxBuffer();
            authenticMutualLoveBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            authenticMutualLoveBuffer.bind();
            authenticMutualLoveBuffer.upload(buffer);
            VertexBuffer.unbind();
        }
        return authenticMutualLoveBuffer;
    }

    public static void renderSky(PoseStack poseStack, Matrix4f projection, Camera camera, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        int ww = window.getWidth();
        int wh = window.getHeight();

        if (ww <= 0 || wh <= 0) return;

        boolean update = false;

        if (skyWidth != ww || skyHeight != wh) {
            update = true;
            skyWidth = ww;
            skyHeight = wh;
        }

        RenderTarget current = mc.getMainRenderTarget();

        mc.gameRenderer.setRenderBlockOutline(false);

        if (unlimitedVoidTarget == null || update) {
            if (unlimitedVoidTarget != null) {
                unlimitedVoidTarget.destroyBuffers();
            }
            unlimitedVoidTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        unlimitedVoidTarget.bindWrite(true);

        renderUnlimitedVoid(poseStack, projection);

        unlimitedVoidTarget.unbindRead();
        unlimitedVoidTarget.unbindWrite();

        if (selfEmbodimentOfPerfectionTarget == null || update) {
            if (selfEmbodimentOfPerfectionTarget != null) {
                selfEmbodimentOfPerfectionTarget.destroyBuffers();
            }
            selfEmbodimentOfPerfectionTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        selfEmbodimentOfPerfectionTarget.bindWrite(true);

        renderBasicSky(poseStack, projection, camera, partialTicks, SELF_EMDOBIMENT_OF_PERFECTION, getSelfEmbodimentOfPerfectionBuffer());

        selfEmbodimentOfPerfectionTarget.unbindRead();
        selfEmbodimentOfPerfectionTarget.unbindWrite();

        if (horizonOfTheCaptivatingSkandhaTarget == null || update) {
            if (horizonOfTheCaptivatingSkandhaTarget != null) {
                horizonOfTheCaptivatingSkandhaTarget.destroyBuffers();
            }
            horizonOfTheCaptivatingSkandhaTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        horizonOfTheCaptivatingSkandhaTarget.bindWrite(true);

        renderBasicSky(poseStack, projection, camera, partialTicks, HORIZON_OF_THE_CAPTIVATING_SKANDHA, getHorizonOfTheCaptivatingSkandhaBuffer());

        horizonOfTheCaptivatingSkandhaTarget.unbindRead();
        horizonOfTheCaptivatingSkandhaTarget.unbindWrite();

        if (shiningSeaOfFlowersTarget == null || update) {
            if (shiningSeaOfFlowersTarget != null) {
                shiningSeaOfFlowersTarget.destroyBuffers();
            }
            shiningSeaOfFlowersTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        shiningSeaOfFlowersTarget.bindWrite(true);

        renderBasicSky(poseStack, projection, camera, partialTicks, SHINING_SEA_OF_FLOWERS, getShiningSeaOfFlowersBuffer());

        shiningSeaOfFlowersTarget.unbindRead();
        shiningSeaOfFlowersTarget.unbindWrite();

        if (authenticMutualLoveTarget == null || update) {
            if (authenticMutualLoveTarget != null) {
                authenticMutualLoveTarget.destroyBuffers();
            }
            authenticMutualLoveTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        authenticMutualLoveTarget.bindWrite(true);

        renderBasicSky(poseStack, projection, camera, partialTicks, AUTHENTIC_MUTUAL_LOVE, getAuthenticMutualLoveBuffer());

        mc.gameRenderer.setRenderBlockOutline(true);
        current.bindWrite(true);
    }

    public static void renderBasicSky(PoseStack poseStack, Matrix4f projection, Camera camera, float partialTicks, ResourceLocation texture, VertexBuffer buffer) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return;

        RenderSystem.clear(16640, Minecraft.ON_OSX);

        FogRenderer.levelFogColor();
        RenderSystem.depthMask(false);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);

        buffer.bind();
        buffer.drawWithShader(poseStack.last().pose(), projection, RenderSystem.getShader());
        VertexBuffer.unbind();

        RenderSystem.depthMask(true);
        FogRenderer.setupNoFog();
    }

    public static void renderUnlimitedVoid(PoseStack poseStack, Matrix4f projection) {
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

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        FogRenderer.setupNoFog();
    }
}
