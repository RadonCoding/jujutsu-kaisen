package radon.jujutsu_kaisen.client.render.domain;


import com.mojang.blaze3d.vertex.*;
import net.minecraft.resources.ResourceLocation;

public abstract class DomainRenderer {
    protected abstract ResourceLocation getTexture();

    public void renderToBuffer(VertexBuffer buffer) {
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
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

        buffer.bind();
        buffer.upload(builder.end());
        VertexBuffer.unbind();
    }
}
