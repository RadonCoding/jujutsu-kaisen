package radon.jujutsu_kaisen.client.render.domain;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.Holder;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.disaster_tides.HorizonOfTheCaptivatingSkandha;
import radon.jujutsu_kaisen.ability.mimicry.AuthenticMutualLove;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.JJKShaders;

import java.util.HashMap;
import java.util.Map;

public class DomainRenderDispatcher {
    private static final Map<Holder<Ability>, DomainRenderer> renderers = new HashMap<>();

    static {
        renderers.put(JJKAbilities.UNLIMITED_VOID, new UnlimitedVoidRenderer());
        renderers.put(JJKAbilities.AUTHENTIC_MUTUAL_LOVE, new AuthenticMutualLoveRenderer());
        renderers.put(JJKAbilities.HORIZON_OF_THE_CAPTIVATING_SKANDHA, new HorizonOfTheCaptivatingSkandhaRenderer());
    }

    private static final Map<Holder<Ability>, VertexBuffer> buffers = new HashMap<>();

    public static void render(Ability ability, Matrix4f modelViewStack, Matrix4f projectionMatrix, VertexBuffer mask) {
        Holder<Ability> holder = JJKAbilities.ABILITY_REGISTRY.getHolder(JJKAbilities.getKey(ability)).orElseThrow();

        DomainRenderer renderer = renderers.get(holder);

        if (!buffers.containsKey(holder)) {
            VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            renderer.renderToBuffer(buffer);
            buffers.put(holder, buffer);
        }

        // Render the skybox into a separate TextureTarget
        Minecraft mc = Minecraft.getInstance();

        Window window = mc.getWindow();

        TextureTarget target = new TextureTarget(window.getWidth(), window.getHeight(), true, Minecraft.ON_OSX);

        target.bindWrite(false);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        RenderSystem.setShaderTexture(0, renderer.getTexture());

        VertexBuffer buffer = buffers.get(holder);
        buffer.bind();
        buffer.drawWithShader(modelViewStack, projectionMatrix, RenderSystem.getShader());

        target.unbindWrite();

        mc.getMainRenderTarget().bindWrite(false);

        RenderSystem.depthMask(false);

        // Render the mask
        RenderSystem.setShader(JJKShaders::getDomainShader);

        RenderSystem.setShaderTexture(0, target.getColorTextureId());

        mask.bind();
        mask.drawWithShader(modelViewStack, projectionMatrix, RenderSystem.getShader());

        RenderSystem.depthMask(true);

        target.destroyBuffers();
    }
}
