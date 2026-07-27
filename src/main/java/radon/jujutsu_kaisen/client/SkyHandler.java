package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class SkyHandler {
    private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");

    private static ClientLevel overworld;
    private static TextureTarget horizonOfTheCaptivatingSkandhaTarget;
    private static TextureTarget authenticMutualLoveTarget;
    private static int skyWidth = -1;
    private static int skyHeight = -1;

    private static VertexBuffer lightBuffer;

    public record SkyStyle(
            int timeOfDay,
            int skyColor,
            int cloudColor,
            float cloudHeight,
            int sunriseColor
    ) {
        public static final SkyStyle HORIZON_OF_THE_CAPTIVATING_SKANDHA = new SkyStyle(
                1000, 0x78A7FF, 0xFFFFFF, 192.0F, 0x000000
        );

        public static final SkyStyle AUTHENTIC_MUTUAL_LOVE = new SkyStyle(
                12500, 0xF8B37A, 0xAC525C, 192.0F, 0xFED97E
        );
    }

    private static final class Cloud {
        VertexBuffer buffer;
        int x = Integer.MIN_VALUE;
        int y = Integer.MIN_VALUE;
        int z = Integer.MIN_VALUE;
    }

    private static final Map<SkyStyle, Cloud> CLOUDS = new HashMap<>();

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            render(event.getProjectionMatrix(), event.getModelViewMatrix(), event.getPartialTick());
        }
    }

    public static TextureTarget getHorizonOfTheCaptivatingSkandhaTarget() {
        return horizonOfTheCaptivatingSkandhaTarget;
    }

    public static TextureTarget getAuthenticMutualLoveTarget() {
        return authenticMutualLoveTarget;
    }

    private static VertexBuffer getLightSky() {
        if (lightBuffer == null) {
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();

            lightBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            BufferBuilder.RenderedBuffer rendered = buildSkyDisc(buffer, 16.0F);
            lightBuffer.bind();
            lightBuffer.upload(rendered);
            VertexBuffer.unbind();
        }
        return lightBuffer;
    }

    private static BufferBuilder.RenderedBuffer buildSkyDisc(BufferBuilder builder, float y) {
        float f = Math.signum(y) * 512.0F;

        RenderSystem.setShader(GameRenderer::getPositionShader);
        builder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        builder.vertex(0.0D, y, 0.0D).endVertex();

        for (int i = -180; i <= 180; i += 45) {
            builder.vertex(
                    f * Mth.cos((float) i * ((float) Math.PI / 180.0F)),
                    y,
                    512.0F * Mth.sin((float) i * ((float) Math.PI / 180.0F))
            ).endVertex();
        }
        return builder.end();
    }

    public static void render(Matrix4f projectionMatrix, Matrix4f modelViewMatrix, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        int ww = window.getWidth();
        int wh = window.getHeight();

        if (ww <= 0 || wh <= 0) {
            return;
        }

        boolean update = false;

        if (horizonOfTheCaptivatingSkandhaTarget == null || authenticMutualLoveTarget == null || skyWidth != ww || skyHeight != wh) {
            update = true;
            skyWidth = ww;
            skyHeight = wh;
        }

        if (update) {
            if (horizonOfTheCaptivatingSkandhaTarget != null) {
                horizonOfTheCaptivatingSkandhaTarget.destroyBuffers();
            }
            if (authenticMutualLoveTarget != null) {
                authenticMutualLoveTarget.destroyBuffers();
            }
            horizonOfTheCaptivatingSkandhaTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
            authenticMutualLoveTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        mc.gameRenderer.setRenderBlockOutline(false);
        RenderTarget current = mc.getMainRenderTarget();

        horizonOfTheCaptivatingSkandhaTarget.bindWrite(true);
        renderSky(projectionMatrix, modelViewMatrix, partialTick, SkyStyle.HORIZON_OF_THE_CAPTIVATING_SKANDHA);
        horizonOfTheCaptivatingSkandhaTarget.unbindRead();
        horizonOfTheCaptivatingSkandhaTarget.unbindWrite();

        authenticMutualLoveTarget.bindWrite(true);
        renderSky(projectionMatrix, modelViewMatrix, partialTick, SkyStyle.AUTHENTIC_MUTUAL_LOVE);
        authenticMutualLoveTarget.unbindRead();
        authenticMutualLoveTarget.unbindWrite();

        current.bindWrite(true);
        mc.gameRenderer.setRenderBlockOutline(true);
    }

    public static void renderSky(Matrix4f projectionMatrix, Matrix4f modelViewMatrix, float partialTick, SkyStyle style) {
        PoseStack poseStack = new PoseStack();

        poseStack.pushPose();
        poseStack.mulPose(modelViewMatrix);

        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return;

        if (overworld == null) {
            ClientPacketListener connection = mc.getConnection();

            if (connection == null) return;

            Holder<DimensionType> holder = mc.level.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(BuiltinDimensionTypes.OVERWORLD);
            overworld = new ClientLevel(connection, new ClientLevel.ClientLevelData(Difficulty.NORMAL, false, false),
                    Level.OVERWORLD, holder, 0, 0, mc::getProfiler, mc.levelRenderer, false, 0);
        }
        overworld.setDayTime(style.timeOfDay());

        Camera camera = mc.gameRenderer.getMainCamera();
        int color = style.skyColor();

        float factor = 0.25F + 0.75F * (float) mc.options.getEffectiveRenderDistance() / 32.0F;
        factor = 1.0F - (float) Math.pow(factor, 0.25D);

        float skyR = (float) ((color >> 16) & 0xFF) / 255.0F;
        float skyG = (float) ((color >> 8) & 0xFF) / 255.0F;
        float skyB = (float) (color & 0xFF) / 255.0F;

        float brightness = Mth.clamp(Mth.cos(overworld.getTimeOfDay(partialTick) * (float) (Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);
        Vec3 origin = camera.getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
        Vec3 fog = CubicSampler.gaussianSampleVec3(
                origin,
                (x, y, z) -> overworld.effects()
                        .getBrightnessDependentFogColor(
                                Vec3.fromRGB24(color), brightness
                        )
        );

        float fogR = (float) fog.x();
        float fogG = (float) fog.y();
        float fogB = (float) fog.z();

        fogR = fogR + (skyR - fogR) * factor;
        fogG = fogG + (skyG - fogG) * factor;
        fogB = fogB + (skyB - fogB) * factor;

        RenderSystem.setShaderFogColor(fogR, fogG, fogB);
        RenderSystem.clearColor(fogR, fogG, fogB, 0.0F);
        RenderSystem.clear(16640, Minecraft.ON_OSX);
        FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_SKY, mc.gameRenderer.getRenderDistance(), false, partialTick);
        RenderSystem.setShader(GameRenderer::getPositionShader);

        RenderSystem.setShaderFogColor(fogR, fogG, fogB);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(skyR, skyG, skyB, 1.0F);

        ShaderInstance shader = RenderSystem.getShader();

        VertexBuffer light = getLightSky();
        light.bind();
        light.drawWithShader(poseStack.last().pose(), projectionMatrix, shader);
        VertexBuffer.unbind();

        RenderSystem.enableBlend();

        float[] sunrise = overworld.effects().getSunriseColor(overworld.getTimeOfDay(partialTick), partialTick);

        if (sunrise != null) {
            sunrise[0] = (float) ((style.sunriseColor() >> 16) & 0xFF) / 255.0F;
            sunrise[1] = (float) ((style.sunriseColor() >> 8) & 0xFF) / 255.0F;
            sunrise[2] = (float) (style.sunriseColor() & 0xFF) / 255.0F;

            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(overworld.getSunAngle(partialTick)) < 0.0F ? 180.0F : 0.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            Matrix4f matrix4f = poseStack.last().pose();

            buffer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

            buffer.vertex(matrix4f, 0.0F, 100.0F, 0.0F)
                    .color(sunrise[0], sunrise[1], sunrise[2], sunrise[3])
                    .endVertex();

            for (int j = 0; j <= 16; ++j) {
                float rad = (float) j * ((float) Math.PI * 2.0F) / 16.0F;
                float sin = Mth.sin(rad);
                float cos = Mth.cos(rad);
                buffer.vertex(matrix4f, sin * 120.0F, cos * 120.0F, -cos * 40.0F * sunrise[3])
                        .color(sunrise[0], sunrise[1], sunrise[2], 0.0F)
                        .endVertex();
            }

            BufferUploader.drawWithShader(buffer.end());

            poseStack.popPose();
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(overworld.getTimeOfDay(partialTick) * 360.0F));

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        RenderSystem.setShaderTexture(0, SUN_LOCATION);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Matrix4f matrix4f1 = poseStack.last().pose();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.vertex(matrix4f1, -30.0F, 100.0F, -30.0F)
                .uv(0.0F, 0.0F)
                .endVertex();
        buffer.vertex(matrix4f1, 30.0F, 100.0F, -30.0F)
                .uv(1.0F, 0.0F)
                .endVertex();
        buffer.vertex(matrix4f1, 30.0F, 100.0F, 30.0F)
                .uv(1.0F, 1.0F)
                .endVertex();
        buffer.vertex(matrix4f1, -30.0F, 100.0F, 30.0F)
                .uv(0.0F, 1.0F)
                .endVertex();

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.defaultBlendFunc();

        RenderSystem.disableBlend();

        poseStack.popPose();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.depthMask(true);

        poseStack.popPose();

        renderClouds(mc, projectionMatrix, modelViewMatrix, style, camera.getPosition(), partialTick);

        FogRenderer.setupNoFog();
    }

    private static void renderClouds(Minecraft mc, Matrix4f projectionMatrix, Matrix4f modelViewMatrix,
                                     SkyStyle style, Vec3 camPos, float partialTick) {
        int ticks = mc.levelRenderer.getTicks();
        double d1 = (ticks + partialTick) * 0.03F;
        double x = (camPos.x + d1) / 12.0D;
        double y = style.cloudHeight() - (float) camPos.y + 0.33F;
        double z = camPos.z / 12.0D + 0.33F;

        x -= Mth.floor(x / 2048.0D) * 2048;
        z -= Mth.floor(z / 2048.0D) * 2048;

        float fracX = (float) (x - Mth.floor(x));
        float fracY = (float) (y / 4.0D - Mth.floor(y / 4.0D)) * 4.0F;
        float fracZ = (float) (z - Mth.floor(z));

        int cellX = (int) Math.floor(x);
        int cellY = (int) Math.floor(y / 4.0D);
        int cellZ = (int) Math.floor(z);

        Cloud cache = CLOUDS.computeIfAbsent(style, s -> new Cloud());

        if (cache.buffer == null || cellX != cache.x || cellY != cache.y || cellZ != cache.z) {
            BufferBuilder builder = Tesselator.getInstance().getBuilder();

            int cloudColor = style.cloudColor();
            Vec3 cloudColorVec = new Vec3(
                    (float) ((cloudColor >> 16) & 0xFF) / 255.0F,
                    (float) ((cloudColor >> 8) & 0xFF) / 255.0F,
                    (float) (cloudColor & 0xFF) / 255.0F
            );
            BufferBuilder.RenderedBuffer rendered = mc.levelRenderer.buildClouds(builder, x, y, z, cloudColorVec);

            if (cache.buffer != null) {
                cache.buffer.close();
            }
            cache.buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            cache.buffer.bind();
            cache.buffer.upload(rendered);
            VertexBuffer.unbind();

            cache.x = cellX;
            cache.y = cellY;
            cache.z = cellZ;
        }

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.mulPose(modelViewMatrix);
        poseStack.scale(12.0F, 1.0F, 12.0F);
        poseStack.translate(-fracX, fracY, -fracZ);

        cache.buffer.bind();

        boolean fancy = mc.options.getCloudsType() == net.minecraft.client.CloudStatus.FANCY;

        int start = fancy ? 0 : 1;

        for (int i = start; i < 2; i++) {
            RenderType type = i == 0 ? JJKRenderTypes.cloudsDepthOnly() : JJKRenderTypes.clouds();
            type.setupRenderState();
            ShaderInstance shader = RenderSystem.getShader();
            cache.buffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shader);
            type.clearRenderState();
        }
        VertexBuffer.unbind();

        poseStack.popPose();
    }
}