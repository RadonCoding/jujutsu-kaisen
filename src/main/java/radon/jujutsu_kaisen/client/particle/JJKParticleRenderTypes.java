package radon.jujutsu_kaisen.client.particle;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11C;

public class JJKParticleRenderTypes {
    public static ParticleRenderType GLOW = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder buffer, @NotNull TextureManager manager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();

            RenderSystem.defaultBlendFunc();
        }
    };

    public static ParticleRenderType ADDITIVE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder buffer, @NotNull TextureManager manager) {
            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();

            RenderSystem.defaultBlendFunc();
        }
    };

    public static ParticleRenderType TRANSLUCENT = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder buffer, @NotNull TextureManager manager) {
            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }
    };

    public static ParticleRenderType CUSTOM = new ParticleRenderType() {
        @Override
        public void begin(@NotNull BufferBuilder buffer, @NotNull TextureManager manager) {
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }
    };
}