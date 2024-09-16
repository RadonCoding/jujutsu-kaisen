package radon.jujutsu_kaisen.client.render.domain;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.util.RenderUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class DomainRenderer {
    private final List<DomainRenderLayer> layers = new ArrayList<>();

    public List<DomainRenderLayer> getLayers() {
        return this.layers;
    }

    protected void addLayer(DomainRenderLayer layer) {
        this.layers.add(layer);
    }

    public final void render(Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        // Bottom
        builder.vertex(-100.0F, -100.0F, -100.0F)
                .uv(0.0F, 0.0F)
                .endVertex();
        builder.vertex(-100.0F, -100.0F, 100.0F)
                .uv(0.0F, 0.5F)
                .endVertex();
        builder.vertex(100.0F, -100.0F, 100.0F)
                .uv(1.0F / 3.0F, 0.5F)
                .endVertex();
        builder.vertex(100.0F, -100.0F, -100.0F)
                .uv(1.0F / 3.0F, 0.0F)
                .endVertex();

        // Top
        builder.vertex(-100.0F, 100.0F, 100.0F)
                .uv(1.0F / 3.0F, 0.0F)
                .endVertex();
        builder.vertex(-100.0F, 100.0F, -100.0F)
                .uv(1.0F / 3.0F, 0.5F)
                .endVertex();
        builder.vertex(100.0F, 100.0F, -100.0F)
                .uv(2.0F / 3.0F, 0.5F)
                .endVertex();
        builder.vertex(100.0F, 100.0F, 100.0F)
                .uv(2.0F / 3.0F, 0.0F)
                .endVertex();

        // Back
        builder.vertex(-100.0F, 100.0F, 100.0F)
                .uv(2.0F / 3.0F, 0.0F)
                .endVertex();
        builder.vertex(-100.0F, -100.0F, 100.0F)
                .uv(2.0F / 3.0F, 0.5F)
                .endVertex();
        builder.vertex(-100.0F, -100.0F, -100.0F)
                .uv(1.0F, 0.5F)
                .endVertex();
        builder.vertex(-100.0F, 100.0F, -100.0F)
                .uv(1.0F, 0.0F)
                .endVertex();

        // Front
        builder.vertex(100.0F, 100.0F, -100.0F)
                .uv(0.0F, 0.5F)
                .endVertex();
        builder.vertex(100.0F, -100.0F, -100.0F)
                .uv(0.0F, 1.0F)
                .endVertex();
        builder.vertex(100.0F, -100.0F, 100.0F)
                .uv(1.0F / 3.0F, 1.0F)
                .endVertex();
        builder.vertex(100.0F, 100.0F, 100.0F)
                .uv(1.0F / 3.0F, 0.5F)
                .endVertex();

        // Left
        builder.vertex(-100.0F, 100.0F, -100.0F)
                .uv(1.0F / 3.0F, 0.5F)
                .endVertex();
        builder.vertex(-100.0F, -100.0F, -100.0F)
                .uv(1.0F / 3.0F, 1.0F)
                .endVertex();
        builder.vertex(100.0F, -100.0F, -100.0F)
                .uv(2.0F / 3.0F, 1.0F)
                .endVertex();
        builder.vertex(100.0F, 100.0F, -100.0F)
                .uv(2.0F / 3.0F, 0.5F)
                .endVertex();

        // Right
        builder.vertex(100.0F, 100.0F, 100.0F)
                .uv(2.0F / 3.0F, 0.5F)
                .endVertex();
        builder.vertex(100.0F, -100.0F, 100.0F)
                .uv(2.0F / 3.0F, 1.0F)
                .endVertex();
        builder.vertex(-100.0F, -100.0F, 100.0F)
                .uv(1.0F, 1.0F)
                .endVertex();
        builder.vertex(-100.0F, 100.0F, 100.0F)
                .uv(1.0F, 0.5F)
                .endVertex();

        RenderUtil.drawWithShader(modelViewMatrix, projectionMatrix, builder.end());
    }

    protected abstract ResourceLocation getTexture();
}
