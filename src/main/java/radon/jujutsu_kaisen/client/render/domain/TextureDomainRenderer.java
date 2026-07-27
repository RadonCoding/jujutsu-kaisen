package radon.jujutsu_kaisen.client.render.domain;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.client.util.RenderUtil;

public abstract class TextureDomainRenderer extends DomainRenderer {
    @Override
    public final void render(Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        RenderSystem.setShaderTexture(0, this.getTexture());

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        super.render(modelViewMatrix, projectionMatrix);
    }

    protected abstract ResourceLocation getTexture();
}
