package radon.jujutsu_kaisen.client.render.domain;


import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.JJKShaders;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class DomainRenderDispatcher {
    private static final Map<ResourceLocation, DomainRenderer> renderers = new HashMap<>();
    private static final Map<ResourceLocation, VertexBuffer> buffers = new HashMap<>();
    private static final Map<ResourceLocation, TextureTarget> cached = new HashMap<>();

    static {
        renderers.put(JJKAbilities.UNLIMITED_VOID.getId(), new UnlimitedVoidRenderer());
    }

    private static int skyWidth;
    private static int skyHeight;

    public static TextureTarget get(ResourceLocation domain) {
        return cached.get(domain);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;

        Minecraft mc = Minecraft.getInstance();

        Window window = mc.getWindow();
        int ww = window.getWidth();
        int wh = window.getHeight();

        if (ww <= 0 || wh <= 0) return;

        RenderTarget current = mc.getMainRenderTarget();

        for (ResourceLocation key : renderers.keySet()) {
            boolean update = false;

            if (skyWidth != ww || skyHeight != wh) {
                update = true;
                skyWidth = ww;
                skyHeight = wh;
            }

            TextureTarget target = cached.get(key);

            if (target == null || update) {
                if (target != null) {
                    target.destroyBuffers();
                }
                target = new TextureTarget(skyWidth, skyHeight, true, Minecraft.ON_OSX);
            }

            target.bindWrite(true);

            render(key, event.getModelViewMatrix(), event.getProjectionMatrix());

            target.unbindRead();
            target.unbindWrite();

            cached.put(key, target);
        }

        current.bindWrite(true);
    }

    public static void render(DomainExpansion domain, Matrix4f modelViewStack, Matrix4f projectionMatrix, TextureTarget include) {
        ResourceLocation key = JJKAbilities.getKey(domain);
        DomainRenderer renderer = renderers.get(key);

        if (!buffers.containsKey(key)) {
            VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            renderer.renderToBuffer(buffer);
            buffers.put(key, buffer);
        }

        RenderSystem.enableBlend();

        RenderSystem.setShader(JJKShaders::getDomainShader);

        RenderSystem.setShaderTexture(0, renderer.getTexture());
        RenderSystem.setShaderTexture(1, include.getColorTextureId());

        VertexBuffer buffer = buffers.get(key);
        buffer.bind();
        buffer.drawWithShader(modelViewStack, projectionMatrix, RenderSystem.getShader());

        RenderSystem.disableBlend();
    }

    public static void render(ResourceLocation key, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        DomainRenderer renderer = renderers.get(key);

        if (!buffers.containsKey(key)) {
            VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            renderer.renderToBuffer(buffer);
            buffers.put(key, buffer);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        RenderSystem.setShaderTexture(0, renderer.getTexture());

        VertexBuffer buffer = buffers.get(key);
        buffer.bind();
        buffer.drawWithShader(modelViewMatrix, projectionMatrix, RenderSystem.getShader());
    }
}
