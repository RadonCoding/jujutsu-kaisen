package radon.jujutsu_kaisen.client.render.domain;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public abstract class DomainRenderer {
    protected abstract ResourceLocation getTexture();

    public void renderToBuffer(VertexBuffer buffer) {
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

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

        builder.vertex(100.0F, 100.0F, 100.0F)
                .uv(2.0F / 3.0F, 0.0F)
                .endVertex();
        builder.vertex(100.0F, -100.0F, 100.0F)
                .uv(2.0F / 3.0F, 0.5F)
                .endVertex();
        builder.vertex(-100.0F, -100.0F, 100.0F)
                .uv(1.0F, 0.5F)
                .endVertex();
        builder.vertex(-100.0F, 100.0F, 100.0F)
                .uv(1.0F, 0.0F)
                .endVertex();

        builder.vertex(-100.0F, 100.0F, 100.0F)
                .uv(0.0F, 0.5F)
                .endVertex();
        builder.vertex(-100.0F, -100.0F, 100.0F)
                .uv(0.0F, 1.0F)
                .endVertex();
        builder.vertex(-100.0F, -100.0F, -100.0F)
                .uv(1.0F / 3.0F, 1.0F)
                .endVertex();
        builder.vertex(-100.0F, 100.0F, -100.0F)
                .uv(1.0F / 3.0F, 0.5F)
                .endVertex();

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

        builder.vertex(100.0F, 100.0F, -100.0F)
                .uv(2.0F / 3.0F, 0.5F)
                .endVertex();
        builder.vertex(100.0F, -100.0F, -100.0F)
                .uv(2.0F / 3.0F, 1.0F)
                .endVertex();
        builder.vertex(100.0F, -100.0F, 100.0F)
                .uv(1.0F, 1.0F)
                .endVertex();
        builder.vertex(100.0F, 100.0F, 100.0F)
                .uv(1.0F, 0.5F)
                .endVertex();

        buffer.bind();
        buffer.upload(builder.end());
        VertexBuffer.unbind();
    }
}
