package radon.jujutsu_kaisen.client.dimension;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.render.domain.DomainRenderDispatcher;
import radon.jujutsu_kaisen.data.domain.DomainInfo;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.util.RotationUtil;

import javax.annotation.Nullable;
import java.util.*;

public class JJKDimensionSpecialEffects {
    public static class DomainExpansionEffects extends DimensionSpecialEffects {
        public DomainExpansionEffects() {
            super(Float.NaN, false, SkyType.NORMAL, true, false);
        }

        @Override
        public boolean renderSky(@NotNull ClientLevel level, int ticks, float partialTick, @NotNull Matrix4f modelViewMatrix, @NotNull Camera camera, @NotNull Matrix4f projectionMatrix, boolean isFoggy, @NotNull Runnable setupFog) {
            IDomainData data = level.getData(JJKAttachmentTypes.DOMAIN);

            Set<DomainInfo> domains = data.getDomains();

            float total = 0.0F;

            for (DomainInfo info : domains) {
                total += info.strength();
            }

            List<DomainInfo> sorted = new ArrayList<>(domains);
            sorted.sort((a, b) -> Float.compare(a.strength(), b.strength()));
            Collections.reverse(sorted);

            float current = 0.0F;

            for (DomainInfo info : sorted) {
                PoseStack pose = new PoseStack();

                float yaw = RotationUtil.getYaw(Vec3.ZERO.subtract(info.center()));
                pose.mulPose(Axis.YN.rotationDegrees(yaw));

                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder builder = tesselator.getBuilder();
                builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

                Matrix4f matrix4f = pose.last().pose();

                int divisions = domains.size();

                for (int lat = 0; lat < divisions; lat++) {
                    float theta = lat * Mth.PI / divisions;
                    float sinTheta = Mth.sin(theta);
                    float cosTheta = Mth.cos(theta);

                    float theta2 = (lat + 1) * Mth.PI / divisions;
                    float sinTheta2 = Mth.sin(theta2);
                    float cosTheta2 = Mth.cos(theta2);

                    for (int lon = 0; lon < divisions * (1.0F - (current / total)); lon++) {
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

                        builder.vertex(matrix4f, x1 * 100.0F, y1 * 100.0F, z1 * 100.0F)
                                .endVertex();
                        builder.vertex(matrix4f, x2 * 100.0F, y2 * 100.0F, z2 * 100.0F)
                                .endVertex();
                        builder.vertex(matrix4f, x3 * 100.0F, y3 * 100.0F, z3 * 100.0F)
                                .endVertex();
                        builder.vertex(matrix4f, x4 * 100.0F, y4 * 100.0F, z4 * 100.0F)
                                .endVertex();
                    }
                }

                BufferBuilder.RenderedBuffer rendered = builder.end();

                VertexBuffer buffer = rendered.drawState().format().getImmediateDrawVertexBuffer();
                buffer.bind();
                buffer.upload(rendered);

                DomainRenderDispatcher.render(info.ability(), modelViewMatrix, projectionMatrix, buffer);

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
