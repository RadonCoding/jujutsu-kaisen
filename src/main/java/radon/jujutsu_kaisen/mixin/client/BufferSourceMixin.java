package radon.jujutsu_kaisen.mixin.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.client.VertexCapturer;
import radon.jujutsu_kaisen.client.slice.RigidBody;

import java.util.ArrayList;
import java.util.List;

@Mixin(MultiBufferSource.BufferSource.class)
public abstract class BufferSourceMixin {
    @Shadow protected abstract BufferBuilder getBuilderRaw(RenderType pRenderType);

    @Shadow @Final protected BufferBuilder builder;

    @Inject(method = "endBatch(Lnet/minecraft/client/renderer/RenderType;)V", at = @At(value = "TAIL", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch(Lnet/minecraft/client/renderer/RenderType;)V"))
    public void endBatch(RenderType type, CallbackInfo ci) {
        if (!VertexCapturer.capture) return;

        BufferBuilder builder = this.getBuilderRaw(type);

        if (builder.format != DefaultVertexFormat.NEW_ENTITY) return;

        List<RigidBody.Triangle.TexVertex> vertices = new ArrayList<>();
        List<RigidBody.Triangle[]> triangles = new ArrayList<>();

        for (int nextElementByte = builder.nextElementByte; nextElementByte > 0;) {
            nextElementByte -= DefaultVertexFormat.ELEMENT_PADDING.getByteSize();
            nextElementByte -= DefaultVertexFormat.ELEMENT_NORMAL.getByteSize();
            nextElementByte -= DefaultVertexFormat.ELEMENT_UV2.getByteSize();
            nextElementByte -= DefaultVertexFormat.ELEMENT_UV1.getByteSize();
            nextElementByte -= DefaultVertexFormat.ELEMENT_UV0.getByteSize();

            float u = builder.buffer.getFloat(nextElementByte);
            float v = builder.buffer.getFloat(nextElementByte + 4);

            nextElementByte -= DefaultVertexFormat.ELEMENT_COLOR.getByteSize();

            int r = builder.buffer.get(nextElementByte) & 0xFF;
            int g = builder.buffer.get(nextElementByte + 1) & 0xFF;
            int b = builder.buffer.get(nextElementByte + 2) & 0xFF;
            int a = builder.buffer.get(nextElementByte + 3) & 0xFF;

            nextElementByte -= DefaultVertexFormat.ELEMENT_POSITION.getByteSize();

            float x = builder.buffer.getFloat(nextElementByte);
            float y = builder.buffer.getFloat(nextElementByte + 4);
            float z = builder.buffer.getFloat(nextElementByte + 8);

            vertices.add(new RigidBody.Triangle.TexVertex(new Vec3(x, y, z), u, v, FastColor.ARGB32.color(a, r, g, b)));

            // Six polygons, four vertices each
            if (vertices.size() == 6 * 4) {
                RigidBody.Triangle[] current = new RigidBody.Triangle[12];

                for (int i = 0; i < vertices.size(); i += 4) {
                    RigidBody.Triangle.TexVertex v0 = vertices.get(i);
                    RigidBody.Triangle.TexVertex v1 = vertices.get(i + 1);
                    RigidBody.Triangle.TexVertex v2 = vertices.get(i + 2);
                    RigidBody.Triangle.TexVertex v3 = vertices.get(i + 3);

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
                    current[i / 2] = new RigidBody.Triangle(v0.pos, v1.pos, v2.pos, uv, color);
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
                    current[i / 2 + 1] = new RigidBody.Triangle(v2.pos, v3.pos, v0.pos, uv, color);
                }

                triangles.add(current);

                vertices.clear();
            }
        }

        VertexCapturer.captured.add(new VertexCapturer.Capture(RenderSystem.getShaderTexture(0), type,
                ImmutableList.copyOf(triangles)));

        triangles.clear();
    }
}
