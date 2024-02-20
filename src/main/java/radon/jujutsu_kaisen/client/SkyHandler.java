package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.Lazy;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.mixin.client.ILevelRendererAccessor;

public class SkyHandler {
    private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");

    private static ClientLevel overworld;
    private static TextureTarget dayTarget;
    private static TextureTarget nightTarget;
    private static int skyWidth = -1;
    private static int skyHeight = -1;

    private static VertexBuffer skyBuffer;
    private static final Lazy<VertexBuffer> optional = Lazy.of(SkyHandler::getSky);

    public static TextureTarget getDayTarget() {
        return dayTarget;
    }

    public static TextureTarget getNightTarget() {
        return nightTarget;
    }

    private static VertexBuffer getSky() {
        if (skyBuffer == null) {
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();

            skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            BufferBuilder.RenderedBuffer rendered = buildSkyDisc(buffer);
            skyBuffer.bind();
            skyBuffer.upload(rendered);
            VertexBuffer.unbind();
        }
        return skyBuffer;
    }

    private static BufferBuilder.RenderedBuffer buildSkyDisc(BufferBuilder pBuilder) {
        float height = 16.0F;
        float f = Math.signum(height) * 512.0F;

        RenderSystem.setShader(GameRenderer::getPositionShader);
        pBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        pBuilder.vertex(0.0D, height, 0.0D).endVertex();

        for (int i = -180; i <= 180; i += 45) {
            pBuilder.vertex(f * Mth.cos((float) i * ((float) Math.PI / 180.0F)), height, 512.0F * Mth.sin((float) i * ((float) Math.PI / 180.0F))).endVertex();
        }
        return pBuilder.end();
    }

    private static void createDaySky(PoseStack poseStack, float partialTick, Matrix4f matrix4f) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        int ww = window.getWidth();
        int wh = window.getHeight();

        if (ww <= 0 || wh <= 0) {
            return;
        }

        boolean update = false;

        if (dayTarget == null || skyWidth != ww || skyHeight != wh) {
            update = true;
            skyWidth = ww;
            skyHeight = wh;
        }

        if (update) {
            if (dayTarget != null) {
                dayTarget.destroyBuffers();
            }
            dayTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        mc.gameRenderer.setRenderBlockOutline(false);
        dayTarget.bindWrite(true);

        RenderTarget current = mc.getMainRenderTarget();
        renderActualSky(poseStack, partialTick, matrix4f, false);

        mc.gameRenderer.setRenderBlockOutline(true);
        dayTarget.unbindRead();
        dayTarget.unbindWrite();
        current.bindWrite(true);
    }

    private static void createNightSky(PoseStack poseStack, float partialTick, Matrix4f matrix4f) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        int ww = window.getWidth();
        int wh = window.getHeight();

        if (ww <= 0 || wh <= 0) {
            return;
        }

        boolean update = false;

        if (nightTarget == null || skyWidth != ww || skyHeight != wh) {
            update = true;
            skyWidth = ww;
            skyHeight = wh;
        }

        if (update) {
            if (nightTarget != null) {
                nightTarget.destroyBuffers();
            }
            nightTarget = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
        }

        mc.gameRenderer.setRenderBlockOutline(false);
        nightTarget.bindWrite(true);

        RenderTarget current = mc.getMainRenderTarget();
        renderActualSky(poseStack, partialTick, matrix4f, true);

        mc.gameRenderer.setRenderBlockOutline(true);
        nightTarget.unbindRead();
        nightTarget.unbindWrite();
        current.bindWrite(true);
    }

    public static void renderSky(PoseStack poseStack, float partialTick, Matrix4f matrix4f) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        int ww = window.getWidth();
        int wh = window.getHeight();

        if (ww <= 0 || wh <= 0) {
            return;
        }

        createDaySky(poseStack, partialTick, matrix4f);
        createNightSky(poseStack, partialTick, matrix4f);
    }

    public static void renderActualSky(PoseStack poseStack, float partialTick, Matrix4f projection, boolean night) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return;

        if (overworld == null) {
            ClientPacketListener conn = mc.getConnection();

            if (conn == null) return;

            Holder<DimensionType> holder = mc.level.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(BuiltinDimensionTypes.OVERWORLD);
            overworld = new ClientLevel(conn, new ClientLevel.ClientLevelData(Difficulty.NORMAL, false, false),
                    Level.OVERWORLD, holder, 0, 0, mc::getProfiler, mc.levelRenderer, false, 0);
        }
        overworld.setDayTime(night ? 18000 : 1000);

        final Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 pos = camera.getPosition();

        FogRenderer.setupColor(camera, partialTick, overworld, mc.options.getEffectiveRenderDistance(), mc.gameRenderer.getDarkenWorldAmount(partialTick));
        FogRenderer.levelFogColor();
        RenderSystem.clear(16640, Minecraft.ON_OSX);
        final float distance = mc.gameRenderer.getRenderDistance();
        final boolean fog = overworld.effects().isFoggyAt(Mth.floor(pos.x), Mth.floor(pos.z)) || mc.gui.getBossOverlay().shouldCreateWorldFog();
        FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_SKY, distance, fog, partialTick);
        RenderSystem.setShader(GameRenderer::getPositionShader);

        Vec3 vec3 = mc.gameRenderer.getMainCamera().getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
        Vec3 vec31 = CubicSampler.gaussianSampleVec3(vec3, (var1, var2, var3) -> Vec3.fromRGB24(7907327));
        float f1 = Mth.cos(overworld.getTimeOfDay(partialTick) * ((float) Math.PI * 2.0F)) * 2.0F + 0.5F;
        f1 = Mth.clamp(f1, 0.0F, 1.0F);
        float f2 = (float) vec31.x * f1;
        float f3 = (float) vec31.y * f1;
        float f4 = (float) vec31.z * f1;

        FogRenderer.levelFogColor();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(f2, f3, f4, 1.0F);
        ShaderInstance shader = RenderSystem.getShader();
        optional.get().bind();
        optional.get().drawWithShader(poseStack.last().pose(), projection, shader);
        VertexBuffer.unbind();
        RenderSystem.enableBlend();

        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        poseStack.pushPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(overworld.getTimeOfDay(partialTick) * 360.0F));
        Matrix4f matrix4f1 = poseStack.last().pose();
        float f12 = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_LOCATION);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix4f1, -f12, 100.0F, -f12).uv(0.0F, 0.0F).endVertex();
        buffer.vertex(matrix4f1, f12, 100.0F, -f12).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(matrix4f1, f12, 100.0F, f12).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(matrix4f1, -f12, 100.0F, f12).uv(0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.popPose();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        if (mc.options.getCloudsType() != CloudStatus.OFF) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            ClientLevel previous = ((ILevelRendererAccessor) mc.levelRenderer).getLevelAccessor();
            ((ILevelRendererAccessor) mc.levelRenderer).setLevelAccessor(overworld);
            mc.levelRenderer.renderClouds(poseStack, projection, partialTick, pos.x, pos.y, pos.z);
            ((ILevelRendererAccessor) mc.levelRenderer).setLevelAccessor(previous);
        }

        RenderSystem.depthMask(false);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
        FogRenderer.setupNoFog();
    }
}
