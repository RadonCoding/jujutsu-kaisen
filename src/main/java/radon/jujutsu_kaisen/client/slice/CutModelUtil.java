package radon.jujutsu_kaisen.client.slice;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import radon.jujutsu_kaisen.client.FakeEntityRenderer;
import radon.jujutsu_kaisen.client.VertexCapturer;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;

import java.util.*;

public class CutModelUtil {
    private static RigidBody.VertexData compress(RigidBody.Triangle[] triangles) {
        List<Vec3> vertices = new ArrayList<>(triangles.length * 3);
        int[] indices = new int[triangles.length * 3];
        float[] uv = new float[triangles.length * 6];
        int[] color = new int[triangles.length * 3];

        for (int i = 0; i < triangles.length; i++) {
            RigidBody.Triangle triangle = triangles[i];
            double eps = 0.00001D;
            int idx = epsIndexOf(vertices, triangle.p1.pos, eps);

            if (idx != -1) {
                indices[i * 3] = idx;
            } else {
                indices[i * 3] = vertices.size();
                vertices.add(triangle.p1.pos);
            }

            idx = epsIndexOf(vertices, triangle.p2.pos, eps);

            if (idx != -1) {
                indices[i * 3 + 1] = idx;
            } else {
                indices[i * 3 + 1] = vertices.size();
                vertices.add(triangle.p2.pos);
            }

            idx = epsIndexOf(vertices, triangle.p3.pos, eps);

            if (idx != -1) {
                indices[i * 3 + 2] = idx;
            } else {
                indices[i * 3 + 2] = vertices.size();
                vertices.add(triangle.p3.pos);
            }

            uv[i * 6] = triangle.p1.u;
            uv[i * 6 + 1] = triangle.p1.v;
            uv[i * 6 + 2] = triangle.p2.u;
            uv[i * 6 + 3] = triangle.p2.v;
            uv[i * 6 + 4] = triangle.p3.u;
            uv[i * 6 + 5] = triangle.p3.v;

            color[i * 3] = triangle.p1.color;
            color[i * 3 + 1] = triangle.p2.color;
            color[i * 3 + 2] = triangle.p3.color;
        }
        RigidBody.VertexData data = new RigidBody.VertexData();
        data.positions = vertices.toArray(new Vec3[0]);
        data.indices = indices;
        data.uv = uv;
        data.color = color;
        return data;
    }

    private static boolean epsilonEquals(Vec3 a, Vec3 b, double eps) {
        double dx = Math.abs(a.x - b.x);
        double dy = Math.abs(a.y - b.y);
        double dz = Math.abs(a.z - b.z);
        return dx < eps && dy < eps && dz < eps;
    }

    private static int epsIndexOf(List<Vec3> l, Vec3 vec, double eps) {
        for (int i = 0; i < l.size(); i++) {
            if (epsilonEquals(vec, l.get(i), eps)) {
                return i;
            }
        }
        return -1;
    }

    private static double rayPlaneIntercept(Vec3 start, Vec3 ray, float[] plane) {
        double num = -(plane[0] * start.x + plane[1] * start.y + plane[2] * start.z + plane[3]);
        double denom = plane[0] * ray.x + plane[1] * ray.y + plane[2] * ray.z;
        return num / denom;
    }

    private static Matrix3f eulerToMat(float yaw, float pitch, float roll) {
        Matrix3f mY = new Matrix3f();
        mY.rotateY(-yaw);
        Matrix3f mP = new Matrix3f();
        mP.rotateX(pitch);
        Matrix3f mR = new Matrix3f();
        mR.rotateZ(roll);
        mR.mul(mP);
        mR.mul(mY);
        return mR;
    }

    private static Vec3 getEulerAngles(Vec3 vec) {
        double yaw = Math.toDegrees(Math.atan2(vec.x, vec.z));
        double sqrt = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        double pitch = Math.toDegrees(Math.atan2(vec.y, sqrt));
        return new Vec3(yaw, pitch - 90.0F, 0);
    }

    private static Matrix3f normalToMatrix(Vec3 normal, float roll) {
        Vec3 euler = getEulerAngles(normal);
        return eulerToMat((float) Math.toRadians(euler.x), (float) Math.toRadians(euler.y + 90.0F), roll);
    }

    private static Optional<Vec3> getNext(List<Vec3[]> edges, Vec3 first) {
        Iterator<Vec3[]> iter = edges.iterator();

        while (iter.hasNext()) {
            Vec3[] v = iter.next();
            double eps = 0.00001D;

            if (epsilonEquals(v[0], first, eps)) {
                iter.remove();
                return Optional.of(v[1]);
            } else if (epsilonEquals(v[1], first, eps)) {
                iter.remove();
                return Optional.of(v[0]);
            }
        }
        return Optional.empty();
    }

    private static RigidBody.VertexData[] cutAndCapConvex(RigidBody.Triangle[] triangles, float[] plane) {
        RigidBody.VertexData[] result = new RigidBody.VertexData[] { null, null, new RigidBody.VertexData() };
        List<RigidBody.Triangle> side1 = new ArrayList<>();
        List<RigidBody.Triangle> side2 = new ArrayList<>();
        List<Vec3[]> clippedEdges = new ArrayList<>();

        for (RigidBody.Triangle triangle : triangles) {
            boolean p1 = triangle.p1.pos.x * plane[0] + triangle.p1.pos.y * plane[1] + triangle.p1.pos.z * plane[2] + plane[3] > 0;
            boolean p2 = triangle.p2.pos.x * plane[0] + triangle.p2.pos.y * plane[1] + triangle.p2.pos.z * plane[2] + plane[3] > 0;
            boolean p3 = triangle.p3.pos.x * plane[0] + triangle.p3.pos.y * plane[1] + triangle.p3.pos.z * plane[2] + plane[3] > 0;

            if (p1 && p2 && p3) { // If all points on positive side, add to side 1
                side1.add(triangle);
            } else if (!p1 && !p2 && !p3) { // Else if all on negative side, add to size 2
                side2.add(triangle);
            } else if (p1 ^ p2 ^ p3) { // Else if only one is positive, clip and add 1 triangle to side 1, 2 to side 2
                RigidBody.Triangle.TexVertex a, b, c;

                if (p1) {
                    a = triangle.p1;
                    b = triangle.p2;
                    c = triangle.p3;
                } else if (p2) {
                    a = triangle.p2;
                    b = triangle.p3;
                    c = triangle.p1;
                } else {
                    a = triangle.p3;
                    b = triangle.p1;
                    c = triangle.p2;
                }
                Vec3 rAB = b.pos.subtract(a.pos);
                Vec3 rAC = c.pos.subtract(a.pos);
                float interceptAB = (float) rayPlaneIntercept(a.pos, rAB, plane);
                float interceptAC = (float) rayPlaneIntercept(a.pos, rAC, plane);
                Vec3 d = a.pos.add(rAB.scale(interceptAB));
                Vec3 e = a.pos.add(rAC.scale(interceptAC));
                float[] deUv = new float[4];
                deUv[0] = a.u + (b.u - a.u) * interceptAB;
                deUv[1] = a.v + (b.v - a.v) * interceptAB;
                deUv[2] = a.u + (c.u - a.u) * interceptAC;
                deUv[3] = a.v + (c.v - a.v) * interceptAC;
                int[] deColor = new int[4];
                deColor[0] = (int) (a.color + (b.color - a.color) * interceptAB);
                deColor[1] = (int) (a.color + (c.color - a.color) * interceptAC);
                side2.add(new RigidBody.Triangle(d, b.pos, e, new float[] { deUv[0], deUv[1], b.u, b.v, deUv[2], deUv[3] },
                        new int[] { deColor[0], b.color, deColor[1] }));
                side2.add(new RigidBody.Triangle(b.pos, c.pos, e, new float[] { b.u, b.v, c.u, c.v, deUv[2], deUv[3] },
                        new int[] { b.color, c.color, deColor[1] }));
                side1.add(new RigidBody.Triangle(a.pos, d, e, new float[] { a.u, a.v, deUv[0], deUv[1], deUv[2], deUv[3] },
                        new int[] { a.color, deColor[0], deColor[1] }));
                clippedEdges.add(new Vec3[] { d, e } );
            } else { // Else one is negative, clip and add 2 triangles to side 1, 1 to side 2.
                RigidBody.Triangle.TexVertex a, b, c;

                if (!p1) {
                    a = triangle.p1;
                    b = triangle.p2;
                    c = triangle.p3;
                } else if (!p2) {
                    a = triangle.p2;
                    b = triangle.p3;
                    c = triangle.p1;
                } else {
                    a = triangle.p3;
                    b = triangle.p1;
                    c = triangle.p2;
                }
                Vec3 rAB = b.pos.subtract(a.pos);
                Vec3 rAC = c.pos.subtract(a.pos);
                float interceptAB = (float) rayPlaneIntercept(a.pos, rAB, plane);
                float interceptAC = (float) rayPlaneIntercept(a.pos, rAC, plane);
                Vec3 d = a.pos.add(rAB.scale(interceptAB));
                Vec3 e = a.pos.add(rAC.scale(interceptAC));
                float[] deUv = new float[4];
                deUv[0] = a.u + (b.u - a.u) * interceptAB;
                deUv[1] = a.v + (b.v - a.v) * interceptAB;
                deUv[2] = a.u + (c.u - a.u) * interceptAC;
                deUv[3] = a.v + (c.v - a.v) * interceptAC;
                int[] deColor = new int[4];
                deColor[0] = (int) (a.color + (b.color - a.color) * interceptAB);
                deColor[1] = (int) (a.color + (c.color - a.color) * interceptAC);
                side1.add(new RigidBody.Triangle(d, b.pos, e, new float[] { deUv[0], deUv[1], b.u, b.v, deUv[2], deUv[3] },
                        new int[] { deColor[0], b.color, deColor[1] }));
                side1.add(new RigidBody.Triangle(b.pos, c.pos, e, new float[] { b.u, b.v, c.u, c.v, deUv[2], deUv[3] },
                        new int[] { b.color, c.color, deColor[1] }));
                side2.add(new RigidBody.Triangle(a.pos, d, e, new float[] { a.u, a.v, deUv[0], deUv[1], deUv[2], deUv[3] },
                        new int[] { a.color, deColor[0], deColor[1] }));
                clippedEdges.add(new Vec3[] { e, d });
            }
        }

        if (!clippedEdges.isEmpty()) {
            Matrix3f matrix3f = normalToMatrix(new Vec3(plane[0], plane[1], plane[2]), 0.0F);
            List<Vec3> orderedClipVertices = new ArrayList<>();
            orderedClipVertices.add(clippedEdges.getFirst()[0]);

            while (!clippedEdges.isEmpty()) {
                Optional<Vec3> next = getNext(clippedEdges, orderedClipVertices.getLast());

                if (next.isEmpty()) break;

                orderedClipVertices.add(next.get());
            }

            Vector3f uv1 = new Vector3f((float) orderedClipVertices.getFirst().x, (float) orderedClipVertices.getFirst().y, (float) orderedClipVertices.getFirst().z);
            matrix3f.transform(uv1);
            RigidBody.Triangle[] cap = new RigidBody.Triangle[orderedClipVertices.size() - 2];

            for (int i = 0; i < cap.length; i++) {
                Vector3f uv2 = new Vector3f((float) orderedClipVertices.get(i + 2).x, (float) orderedClipVertices.get(i + 2).y, (float) orderedClipVertices.get(i + 2).z);
                matrix3f.transform(uv2);
                Vector3f uv3 = new Vector3f((float) orderedClipVertices.get(i + 1).x, (float) orderedClipVertices.get(i + 1).y, (float) orderedClipVertices.get(i + 1).z);
                matrix3f.transform(uv3);
                cap[i] = new RigidBody.Triangle(orderedClipVertices.getFirst(), orderedClipVertices.get(i + 2), orderedClipVertices.get(i + 1),
                        new float[] { uv1.x, uv1.y, uv2.x, uv2.y, uv3.x, uv3.y },
                        new int[] { 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF });
                side1.add(new RigidBody.Triangle(orderedClipVertices.getFirst(), orderedClipVertices.get(i + 2), orderedClipVertices.get(i + 1), new float[6], new int[3]));
                side2.add(new RigidBody.Triangle(orderedClipVertices.getFirst(), orderedClipVertices.get(i + 1), orderedClipVertices.get(i + 2), new float[6], new int[3]));
            }
            result[2] = compress(cap);
        }
        result[0] = compress(side1.toArray(new RigidBody.Triangle[0]));
        result[1] = compress(side2.toArray(new RigidBody.Triangle[0]));
        return result;
    }

    private static int[] getTextureDimensions(int textureID) {
        int[] dimensions = new int[2];

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        dimensions[0] = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        dimensions[1] = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        return dimensions;
    }

    private static float[] readTextureData(int textureID) {
        int[] dimensions = getTextureDimensions(textureID);
        int width = dimensions[0];
        int height = dimensions[1];

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid texture dimensions.");
        }

        float[] buffer = new float[width * height * 4];
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_RGBA, GL11.GL_FLOAT, buffer);
        return buffer;
    }

    private static float getAlphaValue(float[] buffer, int[] dimensions, int x, int y) {
        int width = dimensions[0];
        int index = (y * width + x) * 4;
        return buffer[index + 3];
    }

    public static void collect(FakeEntityRenderer renderer, Vector3f plane, float distance, float partialTicks, List<RigidBody.CutModelData> top, List<RigidBody.CutModelData> bottom) {
        Minecraft mc = Minecraft.getInstance();

        VertexCapturer.capture = true;

        mc.getMainRenderTarget().unbindWrite();

        renderer.render(new PoseStack(), partialTicks);

        mc.getMainRenderTarget().bindWrite(false);

        VertexCapturer.capture = false;

        Map<RenderType, TextureData> textures = new HashMap<>();

        for (VertexCapturer.Capture capture : VertexCapturer.captured) {
            if (textures.containsKey(capture.type())) continue;

            int previous = RenderSystem.getShaderTexture(0);

            capture.type().setupRenderState();
            int current = RenderSystem.getShaderTexture(0);
            capture.type().clearRenderState();

            if (previous == current) continue;

            float[] buffer = readTextureData(current);
            int[] dimensions = getTextureDimensions(current);
            textures.put(capture.type(), new TextureData(buffer, dimensions));
        }

        for (int i = 0; i < VertexCapturer.captured.size(); i++) {
            VertexCapturer.Capture capture = VertexCapturer.captured.get(i);

            for (RigidBody.Triangle[] triangles : capture.triangles()) {
                if (textures.containsKey(capture.type())) {
                    boolean visible = false;

                    TextureData data = textures.get(capture.type());

                    // Each triangles array contains six polygons and each of them contains four quads
                    for (int j = 0; j < triangles.length; j += 2) {
                        RigidBody.Triangle t1 = triangles[j];
                        RigidBody.Triangle t2 = triangles[j + 1];

                        float[] uv = new float[8];
                        uv[0] = t1.p1.u;
                        uv[1] = t1.p1.v;
                        uv[2] = t1.p2.u;
                        uv[3] = t1.p2.v;
                        uv[4] = t1.p3.u;
                        uv[5] = t1.p3.v;
                        uv[6] = t2.p2.u;
                        uv[7] = t2.p2.v;

                        int width = data.dimensions[0];
                        int height = data.dimensions[1];

                        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
                        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;

                        for (int k = 0; k < uv.length; k += 2) {
                            float u = uv[k];
                            float v = uv[k + 1];

                            if (u < minX) minX = u;
                            if (u > maxX) maxX = u;
                            if (v < minY) minY = v;
                            if (v > maxY) maxY = v;
                        }

                        int startX = Math.max(0, Math.round(minX * (width - 1)));
                        int endX = Math.min(width - 1, Math.round(maxX * (width - 1)));
                        int startY = Math.max(0, Math.round(minY * (height - 1)));
                        int endY = Math.min(height - 1, Math.round(maxY * (height - 1)));

                        for (int x = startX; x < endX; x++) {
                            if (visible) break;

                            for (int y = startY; y < endY; y++) {
                                if (getAlphaValue(data.buffer, data.dimensions, x, y) == 0.0F) continue;

                                visible = true;
                                break;
                            }
                        }
                    }

                    if (!visible) continue;
                }

                RigidBody.VertexData[] data = cutAndCapConvex(triangles, new float[] { plane.x, plane.y, plane.z, -distance });
                RigidBody.CutModelData tp = null;
                RigidBody.CutModelData bt = null;

                if (data[0].indices != null && data[0].indices.length > 0) {
                    tp = new RigidBody.CutModelData(capture.type(), data[0], null, false,
                            new ConvexMeshCollider(data[0].indices, data[0].vertices(), 1.0F));
                    top.add(tp);
                }
                if (data[1].indices != null && data[1].indices.length > 0) {
                    bt = new RigidBody.CutModelData(capture.type(), data[1], null, true,
                            new ConvexMeshCollider(data[1].indices, data[1].vertices(), 1.0F));
                    bottom.add(bt);
                }
                if (data[2].indices != null && data[2].indices.length > 0) {
                    tp.cap = data[2];
                    bt.cap = data[2];
                }
            }
        }

        VertexCapturer.captured.clear();
    }

    private record TextureData(float[] buffer, int[] dimensions) {}
}
