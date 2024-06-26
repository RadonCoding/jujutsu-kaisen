package radon.jujutsu_kaisen.client.dimension;


import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.client.render.domain.DomainRenderDispatcher;
import radon.jujutsu_kaisen.client.util.RenderUtil;
import radon.jujutsu_kaisen.data.DataProvider;
import radon.jujutsu_kaisen.data.domain.DomainInfo;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;

import javax.annotation.Nullable;
import java.util.*;

public class JJKDimensionSpecialEffects {
    public static class DomainExpansionEffects extends DimensionSpecialEffects {
        public DomainExpansionEffects() {
            super(Float.NaN, false, SkyType.NORMAL, true, false);
        }

        @Override
        public boolean renderSky(@NotNull ClientLevel level, int ticks, float partialTick, @NotNull Matrix4f modelViewMatrix, @NotNull Camera camera, @NotNull Matrix4f projectionMatrix, boolean isFoggy, @NotNull Runnable setupFog) {
            Optional<IDomainData> data = DataProvider.getDataIfPresent(level, JJKAttachmentTypes.DOMAIN);

            if (data.isEmpty()) return true;

            Set<DomainInfo> domains = data.get().getDomains();

            float total = 0.0F;

            for (DomainInfo info : domains) {
                total += info.strength();
            }

            List<DomainInfo> sorted = new ArrayList<>(domains);
            sorted.sort((a, b) -> Float.compare(a.strength(), b.strength()));
            Collections.reverse(sorted);

            float current = 0.0F;

            int divisions = domains.size() * 16;

            for (DomainInfo info : sorted) {
                Minecraft mc = Minecraft.getInstance();

                Window window = mc.getWindow();

                TextureTarget include = new TextureTarget(window.getWidth(), window.getHeight(), true, Minecraft.ON_OSX);
                include.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                include.clear(Minecraft.ON_OSX);
                include.copyDepthFrom(mc.getMainRenderTarget());

                include.bindWrite(false);

                RenderSystem.depthMask(false);

                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder builder = tesselator.getBuilder();

                RenderSystem.setShader(GameRenderer::getPositionShader);

                builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

                for (int lat = 0; lat < divisions; lat++) {
                    float theta = lat * Mth.PI / divisions;
                    float sinTheta = Mth.sin(theta);
                    float cosTheta = Mth.cos(theta);

                    float theta2 = (lat + 1) * Mth.PI / divisions;
                    float sinTheta2 = Mth.sin(theta2);
                    float cosTheta2 = Mth.cos(theta2);

                    int start = Math.round(divisions * (current / total));
                    int end = Math.round(start + (divisions * (info.strength() / total)));

                    for (int lon = start; lon < end; lon++) {
                        float phi = lon * 2 * Mth.PI / divisions;
                        float sinPhi = Mth.sin(phi);
                        float cosPhi = Mth.cos(phi);

                        float phi2 = (lon + 1) * 2 * Mth.PI / divisions;
                        float sinPhi2 = Mth.sin(phi2);
                        float cosPhi2 = Mth.cos(phi2);

                        float x1 = cosPhi * sinTheta;
                        float y1 = cosTheta;
                        float z1 = sinPhi * sinTheta;

                        float x2 = cosPhi * sinTheta2;
                        float y2 = cosTheta2;
                        float z2 = sinPhi * sinTheta2;

                        float x3 = cosPhi2 * sinTheta2;
                        float y3 = cosTheta2;
                        float z3 = sinPhi2 * sinTheta2;

                        float x4 = cosPhi2 * sinTheta;
                        float y4 = cosTheta;
                        float z4 = sinPhi2 * sinTheta;

                        builder.vertex(x1 * 100.0F, y1 * 100.0F, z1 * 100.0F)
                                .endVertex();
                        builder.vertex(x2 * 100.0F, y2 * 100.0F, z2 * 100.0F)
                                .endVertex();
                        builder.vertex(x3 * 100.0F, y3 * 100.0F, z3 * 100.0F)
                                .endVertex();
                        builder.vertex(x4 * 100.0F, y4 * 100.0F, z4 * 100.0F)
                                .endVertex();
                    }
                }

                RenderUtil.drawWithShader(modelViewMatrix, projectionMatrix, builder.end());

                RenderSystem.depthMask(true);

                include.unbindWrite();
                mc.getMainRenderTarget().bindWrite(false);

                DomainRenderDispatcher.render(info.ability(), modelViewMatrix, projectionMatrix, include);

                include.destroyBuffers();
                mc.getMainRenderTarget().bindWrite(false);

                current += info.strength();
            }
            return true;
        }

        @Override
        public @NotNull Vec3 getBrightnessDependentFogColor(@NotNull Vec3 pFogColor, float pBrightness) {
            return pFogColor;
        }

        @Override
        public boolean isFoggyAt(int pX, int pY) {
            return false;
        }

        @Nullable
        @Override
        public float[] getSunriseColor(float pTimeOfDay, float pPartialTicks) {
            return null;
        }
    }
}
