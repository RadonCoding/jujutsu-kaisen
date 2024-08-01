package radon.jujutsu_kaisen.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.client.VertexCapturer;
import radon.jujutsu_kaisen.client.slice.RigidBody;

import java.nio.ByteBuffer;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin {
    @Shadow private VertexFormat format;

    @Shadow private ByteBuffer buffer;

    @Shadow private int nextElementByte;

    @Inject(method = "endVertex", at = @At("HEAD"))
    public void endVertex(CallbackInfo ci) {
        if (!VertexCapturer.capture) return;

        if (this.format != DefaultVertexFormat.NEW_ENTITY) return;

        int nextElementByte = this.nextElementByte;

        nextElementByte -= DefaultVertexFormat.ELEMENT_PADDING.getByteSize();
        nextElementByte -= DefaultVertexFormat.ELEMENT_NORMAL.getByteSize();
        nextElementByte -= DefaultVertexFormat.ELEMENT_UV2.getByteSize();
        nextElementByte -= DefaultVertexFormat.ELEMENT_UV1.getByteSize();
        nextElementByte -= DefaultVertexFormat.ELEMENT_UV0.getByteSize();

        float u = this.buffer.getFloat(nextElementByte);
        float v = this.buffer.getFloat(nextElementByte + 4);

        nextElementByte -= DefaultVertexFormat.ELEMENT_COLOR.getByteSize();

        int r = this.buffer.get(nextElementByte) & 0xFF;
        int g = this.buffer.get(nextElementByte + 1) & 0xFF;
        int b = this.buffer.get(nextElementByte + 2) & 0xFF;
        int a = this.buffer.get(nextElementByte + 3) & 0xFF;

        nextElementByte -= DefaultVertexFormat.ELEMENT_POSITION.getByteSize();

        float x = this.buffer.getFloat(nextElementByte);
        float y = this.buffer.getFloat(nextElementByte + 4);
        float z = this.buffer.getFloat(nextElementByte + 8);

        VertexCapturer.currentVertices.add(new RigidBody.Triangle.TexVertex(new Vec3(x, y, z), u, v, FastColor.ARGB32.color(a, r, g, b)));

        // Six polygons, four vertices each
        if (VertexCapturer.currentVertices.size() == 6 * 4) {
            RigidBody.Triangle[] triangles = new RigidBody.Triangle[12];

            for (int i = 0; i < VertexCapturer.currentVertices.size(); i += 4) {
                RigidBody.Triangle.TexVertex v0 = VertexCapturer.currentVertices.get(i);
                RigidBody.Triangle.TexVertex v1 = VertexCapturer.currentVertices.get(i + 1);
                RigidBody.Triangle.TexVertex v2 = VertexCapturer.currentVertices.get(i + 2);
                RigidBody.Triangle.TexVertex v3 = VertexCapturer.currentVertices.get(i + 3);

                float[] uv = new float[6];
                uv[0] = v0.u;
                uv[1] = v0.v;
                uv[2] = v1.u;
                uv[3] = v1.v;
                uv[4] = v2.u;
                uv[5] = v2.v;
                int[] color = new int[6];
                color[0] = v0.color;
                color[1] = v1.color;
                color[2] = v2.color;
                triangles[i / 2] = new RigidBody.Triangle(v0.pos, v1.pos, v2.pos, uv, color);
                uv = new float[6];
                uv[0] = v2.u;
                uv[1] = v2.v;
                uv[2] = v3.u;
                uv[3] = v3.v;
                uv[4] = v0.u;
                uv[5] = v0.v;
                color = new int[6];
                color[0] = v2.color;
                color[1] = v3.color;
                color[2] = v0.color;
                triangles[i / 2 + 1] = new RigidBody.Triangle(v2.pos, v3.pos, v0.pos, uv, color);
            }

            VertexCapturer.currentTriangles.add(triangles);

            VertexCapturer.currentVertices.clear();
        }
    }
}
