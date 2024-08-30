package radon.jujutsu_kaisen.client.render.domain;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import radon.jujutsu_kaisen.client.util.RenderUtil;

public abstract class DomainRenderer {
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

    public void renderPostEffects(Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {}

    protected abstract ResourceLocation getTexture();
}
