package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.util.ArrayList;
import java.util.List;

public class RigidBody {
    private static final ResourceLocation BLOOD = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/misc/blood.png");

    private final List<CutModelData> chunks = new ArrayList<>();

    public void add(CutModelData data) {
        this.chunks.add(data);
    }

    public void tick() {

    }

    public void render(PoseStack poseStack, ResourceLocation texture, int packedLight) {
        Matrix4f matrix4f = poseStack.last().pose();

        RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);

        RenderSystem.setShaderTexture(0, texture);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);

        for (RigidBody.CutModelData data : this.chunks) {
            data.data.tessellate(builder, matrix4f, packedLight);
        }
        tesselator.end();

        RenderSystem.setShaderTexture(0, BLOOD);

        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);

        for (RigidBody.CutModelData data : this.chunks) {
            if (data.cap == null) continue;

            data.cap.tessellate(builder, matrix4f, packedLight);
        }
        tesselator.end();
    }

    public static class CutModelData {
        public VertexData data;
        public VertexData cap;
        public boolean flip;

        public CutModelData(VertexData data, VertexData cap, boolean flip) {
            this.data = data;
            this.cap = cap;
            this.flip = flip;
        }
    }

    public static class VertexData {
        public Vec3[] positions;
        public int[] indices;
        public float[] uv;

        public void tessellate(BufferBuilder builder, Matrix4f matrix4f, int packedLight) {
            this.tessellate(builder, matrix4f, false, packedLight);
        }

        public void tessellate(BufferBuilder builder, Matrix4f matrix4f, boolean flip, int packedLight) {
            if (this.indices == null) return;

            for (int i = 0; i < this.indices.length; i += 3) {
                Vec3 a = this.positions[this.indices[i]];
                Vec3 b = this.positions[this.indices[i + 1]];
                Vec3 c = this.positions[this.indices[i + 2]];

                int tOB = 1;
                int tOC = 2;

                if (flip) {
                    Vec3 tmp = b;
                    b = c;
                    c = tmp;
                    tOB = 2;
                    tOC = 1;
                }

                Vec3 normalized = b.subtract(a).cross(c.subtract(a)).normalize();
                builder.vertex(matrix4f, (float) a.x, (float) a.y, (float) a.z)
                        .color(255, 255, 255, 255)
                        .uv(this.uv[i * 2], this.uv[i * 2 + 1])
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(packedLight)
                        .normal((float) normalized.x, (float) normalized.y, (float) normalized.z)
                        .endVertex();
                builder.vertex(matrix4f, (float) b.x, (float) b.y, (float) b.z)
                        .color(255, 255, 255, 255)
                        .uv(this.uv[(i + tOB) * 2], this.uv[(i + tOB) * 2 + 1])
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(packedLight)
                        .normal((float) normalized.x, (float) normalized.y, (float) normalized.z)
                        .endVertex();
                builder.vertex(matrix4f, (float) c.x, (float) c.y, (float) c.z)
                        .color(255, 255, 255, 255)
                        .uv(this.uv[(i + tOC) * 2], this.uv[(i + tOC) * 2 + 1])
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(packedLight)
                        .normal((float) normalized.x, (float) normalized.y, (float) normalized.z)
                        .endVertex();
            }
        }
    }

    public static class Triangle {
        public TexVertex p1, p2, p3;

        public Triangle(Vec3 p1, Vec3 p2, Vec3 p3, float[] uv) {
            this.p1 = new TexVertex(p1, uv[0], uv[1]);
            this.p2 = new TexVertex(p2, uv[2], uv[3]);
            this.p3 = new TexVertex(p3, uv[4], uv[5]);
        }

        public static class TexVertex {
            public Vec3 pos;
            public float u, v;

            public TexVertex(Vec3 pos, float x, float y) {
                this.pos = pos;
                this.u = x;
                this.v = y;
            }
        }
    }
}
