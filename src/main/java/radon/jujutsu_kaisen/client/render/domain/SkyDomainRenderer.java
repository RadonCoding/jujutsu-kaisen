package radon.jujutsu_kaisen.client.render.domain;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.client.JJKShaders;
import radon.jujutsu_kaisen.client.SkyHandler;
import radon.jujutsu_kaisen.client.util.RenderUtil;

public abstract class SkyDomainRenderer extends DomainRenderer {
    @Override
    public void render(Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        RenderSystem.setShaderTexture(0, this.getTarget().getColorTextureId());

        RenderSystem.setShader(JJKShaders::getSkyShader);

        super.render(modelViewMatrix, projectionMatrix);
    }

    protected abstract TextureTarget getTarget();
}
