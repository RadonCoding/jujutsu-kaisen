package radon.jujutsu_kaisen.client.render.domain;


import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.JJKShaders;

import java.util.HashMap;
import java.util.Map;

public class DomainRenderDispatcher {
    private static final Map<ResourceLocation, DomainRenderer> renderers = new HashMap<>();
    private static final Map<ResourceLocation, VertexBuffer> buffers = new HashMap<>();

    static {
        renderers.put(JJKAbilities.UNLIMITED_VOID.getId(), new UnlimitedVoidRenderer());
        renderers.put(JJKAbilities.AUTHENTIC_MUTUAL_LOVE.getId(), new AuthenticMutualLoveRenderer());
        renderers.put(JJKAbilities.HORIZON_OF_THE_CAPTIVATING_SKANDHA.getId(), new HorizonOfTheCaptivatingSkandhaRenderer());
    }

    public static void render(Ability ability, Matrix4f modelViewStack, Matrix4f projectionMatrix, TextureTarget include) {
        ResourceLocation key = JJKAbilities.getKey(ability);
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
}
