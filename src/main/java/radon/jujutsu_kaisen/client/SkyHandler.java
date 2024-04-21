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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
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

        int latitudeDivisions = 16;
        int longitudeDivisions = 16;

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
            BufferBuilder.RenderedBuffer buffer = createBoxBuffer();
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

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) return;

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

        renderUnlimitedVoid(event.getPoseStack(), event.getProjectionMatrix());

        unlimitedVoidTarget.unbindRead();
        unlimitedVoidTarget.unbindWrite();

        if (selfEmbodimentOfPerfectionTarget == null || update) {
            if (selfEmbodimentOfPerfectionTarget != null) {
                selfEmbodimentOfPerfectionTarget.destroyBuffers();
            }
            selfEmbodimentOfPerfectionTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        selfEmbodimentOfPerfectionTarget.bindWrite(true);

        renderBasicSky(event.getPoseStack(), event.getProjectionMatrix(), event.getCamera(), event.getPartialTick(),
                SELF_EMDOBIMENT_OF_PERFECTION, getSelfEmbodimentOfPerfectionBuffer());

        selfEmbodimentOfPerfectionTarget.unbindRead();
        selfEmbodimentOfPerfectionTarget.unbindWrite();

        if (horizonOfTheCaptivatingSkandhaTarget == null || update) {
            if (horizonOfTheCaptivatingSkandhaTarget != null) {
                horizonOfTheCaptivatingSkandhaTarget.destroyBuffers();
            }
            horizonOfTheCaptivatingSkandhaTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        horizonOfTheCaptivatingSkandhaTarget.bindWrite(true);

        renderBasicSky(event.getPoseStack(), event.getProjectionMatrix(), event.getCamera(), event.getPartialTick(),
                HORIZON_OF_THE_CAPTIVATING_SKANDHA, getHorizonOfTheCaptivatingSkandhaBuffer());

        horizonOfTheCaptivatingSkandhaTarget.unbindRead();
        horizonOfTheCaptivatingSkandhaTarget.unbindWrite();

        if (shiningSeaOfFlowersTarget == null || update) {
            if (shiningSeaOfFlowersTarget != null) {
                shiningSeaOfFlowersTarget.destroyBuffers();
            }
            shiningSeaOfFlowersTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        shiningSeaOfFlowersTarget.bindWrite(true);

        renderBasicSky(event.getPoseStack(), event.getProjectionMatrix(), event.getCamera(), event.getPartialTick(),
                SHINING_SEA_OF_FLOWERS, getShiningSeaOfFlowersBuffer());

        shiningSeaOfFlowersTarget.unbindRead();
        shiningSeaOfFlowersTarget.unbindWrite();

        if (authenticMutualLoveTarget == null || update) {
            if (authenticMutualLoveTarget != null) {
                authenticMutualLoveTarget.destroyBuffers();
            }
            authenticMutualLoveTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        authenticMutualLoveTarget.bindWrite(true);

        renderBasicSky(event.getPoseStack(), event.getProjectionMatrix(), event.getCamera(), event.getPartialTick(),
                AUTHENTIC_MUTUAL_LOVE, getAuthenticMutualLoveBuffer());

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
