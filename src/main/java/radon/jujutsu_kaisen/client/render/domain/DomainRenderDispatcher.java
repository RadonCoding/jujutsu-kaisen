package radon.jujutsu_kaisen.client.render.domain;


import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.JJKShaders;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class DomainRenderDispatcher {
    private static final Map<ResourceLocation, DomainRenderer> renderers = new HashMap<>();
    private static final Map<ResourceLocation, TextureTarget> cached = new HashMap<>();

    static {
        renderers.put(JJKAbilities.UNLIMITED_VOID.getId(), new UnlimitedVoidRenderer());
        renderers.put(JJKAbilities.MALEVOLENT_SHRINE.getId(), new MalevolentShrineRenderer());
        renderers.put(JJKAbilities.COFFIN_OF_THE_IRON_MOUNTAIN.getId(), new CoffinOfTheIronMountainRenderer());
        renderers.put(JJKAbilities.AUTHENTIC_MUTUAL_LOVE.getId(), new AuthenticMutualLoveRenderer());
    }

    // NOTE: Temporary until all domain renderers are implemented
    private static final DomainRenderer DEFAULT_RENDERER = new DefaultDomainRenderer();

    private static int skyWidth;
    private static int skyHeight;

    public static TextureTarget get(ResourceLocation domain) {
        TextureTarget target = cached.get(domain);

        if (target == null) {
            Minecraft mc = Minecraft.getInstance();
            Window window = mc.getWindow();

            int ww = window.getWidth();
            int wh = window.getHeight();

            if (ww <= 0 || wh <= 0) {
                target = new TextureTarget(1, 1, true, Minecraft.ON_OSX);
                cached.put(domain, target);
                return target;
            }

            target = new TextureTarget(ww, wh, true, Minecraft.ON_OSX);
            target.clear(Minecraft.ON_OSX);
            target.bindWrite(true);

            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, DEFAULT_RENDERER.getTexture());
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            DEFAULT_RENDERER.render(new Matrix4f(), new Matrix4f());
            RenderSystem.disableBlend();

            target.unbindRead();
            target.unbindWrite();

            mc.getMainRenderTarget().bindWrite(true);

            cached.put(domain, target);
        }

        return target;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;

        Minecraft mc = Minecraft.getInstance();

        Window window = mc.getWindow();
        int ww = window.getWidth();
        int wh = window.getHeight();

        if (ww <= 0 || wh <= 0) return;

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

            target.clear(Minecraft.ON_OSX);

            target.bindWrite(true);

            render(key, event.getModelViewMatrix(), event.getProjectionMatrix());

            target.unbindRead();
            target.unbindWrite();

            mc.getMainRenderTarget().bindWrite(true);

            cached.put(key, target);
        }
    }

    public static void render(DomainExpansion domain, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, TextureTarget include, int time) {
        ResourceLocation key = JJKAbilities.getKey(domain);
        DomainRenderer renderer = renderers.getOrDefault(key, DEFAULT_RENDERER);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        RenderSystem.setShaderTexture(0, renderer.getTexture());
        RenderSystem.setShaderTexture(1, include.getColorTextureId());

        RenderSystem.setShader(JJKShaders::getDomainShader);

        renderer.render(modelViewMatrix, projectionMatrix);

        for (DomainRenderLayer layer : renderer.getLayers()) {
            if (!layer.shouldRender(time)) continue;

            RenderSystem.setShaderTexture(0, layer.getTexture());

            renderer.render(modelViewMatrix, projectionMatrix);
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    public static void render(ResourceLocation key, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        DomainRenderer renderer = renderers.getOrDefault(key, DEFAULT_RENDERER);

        RenderSystem.enableBlend();

        RenderSystem.setShaderTexture(0, renderer.getTexture());

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        renderer.render(modelViewMatrix, projectionMatrix);

        RenderSystem.disableBlend();
    }
}
