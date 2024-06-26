package radon.jujutsu_kaisen.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.client.FakeEntityRenderer;
import radon.jujutsu_kaisen.client.particle.registry.JJKParticles;
import radon.jujutsu_kaisen.client.slice.ConvexMeshCollider;
import radon.jujutsu_kaisen.client.slice.RigidBody;
import radon.jujutsu_kaisen.mixin.client.IAgeableListModelAccessor;
import radon.jujutsu_kaisen.mixin.client.ILivingEntityRendererAccessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// https://github.com/Alcatergit/Hbm-s-Nuclear-Tech-GIT/blob/Custom-1.12.2/src/main/java/com/hbm/render/util/ModelRendererUtil.java
public class SlicedEntityParticle extends TextureSheetParticle {
    private final int entityId;
    private final Vector3f plane;
    private final float distance;

    @Nullable
    private LivingEntity entity;

    @Nullable
    private FakeEntityRenderer renderer;

    @Nullable
    private RigidBody top;
    @Nullable
    private RigidBody bottom;

    protected SlicedEntityParticle(ClientLevel pLevel, double pX, double pY, double pZ, Options options) {
        super(pLevel, pX, pY, pZ);

        this.entityId = options.entityId;
        this.plane = options.plane;
        this.distance = options.distance;
    }

    public static RigidBody.VertexData compress(RigidBody.Triangle[] triangles) {
        List<Vec3> vertices = new ArrayList<>(triangles.length * 3);
        int[] indices = new int[triangles.length * 3];
        float[] uv = new float[triangles.length * 6];

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
        }
        RigidBody.VertexData data = new RigidBody.VertexData();
        data.positions = vertices.toArray(new Vec3[0]);
        data.indices = indices;
        data.uv = uv;
        return data;
    }

    public static boolean epsilonEquals(Vec3 a, Vec3 b, double eps) {
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

    public static double rayPlaneIntercept(Vec3 start, Vec3 ray, float[] plane) {
        double num = -(plane[0] * start.x + plane[1] * start.y + plane[2] * start.z + plane[3]);
        double denom = plane[0] * ray.x + plane[1] * ray.y + plane[2] * ray.z;
        return num / denom;
    }

    public static RigidBody.Triangle[] triangulate(PoseStack poseStack, ModelPart part, ModelPart.Cube cube) {
        RigidBody.Triangle[] triangles = new RigidBody.Triangle[12];

        int i = 0;

        poseStack.pushPose();
        part.translateAndRotate(poseStack);

        Matrix4f matrix4f = poseStack.last().pose();

        for (ModelPart.Polygon polygon : cube.polygons) {
            Vector3f tmp = new Vector3f();
            Vec3 v0 = new Vec3(matrix4f.transformPosition(tmp.set(polygon.vertices[0].pos).div(16.0F), tmp));
            Vec3 v1 = new Vec3(matrix4f.transformPosition(tmp.set(polygon.vertices[1].pos).div(16.0F), tmp));
            Vec3 v2 = new Vec3(matrix4f.transformPosition(tmp.set(polygon.vertices[2].pos).div(16.0F), tmp));
            Vec3 v3 = new Vec3(matrix4f.transformPosition(tmp.set(polygon.vertices[3].pos).div(16.0F), tmp));
            float[] uv = new float[6];
            uv[0] = polygon.vertices[0].u;
            uv[1] = polygon.vertices[0].v;
            uv[2] = polygon.vertices[1].u;
            uv[3] = polygon.vertices[1].v;
            uv[4] = polygon.vertices[2].u;
            uv[5] = polygon.vertices[2].v;
            triangles[i++] = new RigidBody.Triangle(v0, v1, v2, uv);
            uv = new float[6];
            uv[0] = polygon.vertices[2].u;
            uv[1] = polygon.vertices[2].v;
            uv[2] = polygon.vertices[3].u;
            uv[3] = polygon.vertices[3].v;
            uv[4] = polygon.vertices[0].u;
            uv[5] = polygon.vertices[0].v;
            triangles[i++] = new RigidBody.Triangle(v2, v3, v0, uv);
        }
        poseStack.popPose();

        return triangles;
    }

    public static RigidBody.VertexData[] cutAndCapModelBox(PoseStack poseStack, ModelPart part, ModelPart.Cube cube, float[] plane) {
        return cutAndCapConvex(triangulate(poseStack, part, cube), plane);
    }

    public static Matrix3f eulerToMat(float yaw, float pitch, float roll) {
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

    public static Vec3 getEulerAngles(Vec3 vec) {
        double yaw = Math.toDegrees(Math.atan2(vec.x, vec.z));
        double sqrt = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        double pitch = Math.toDegrees(Math.atan2(vec.y, sqrt));
        return new Vec3(yaw, pitch - 90.0F, 0);
    }

    public static Matrix3f normalToMatrix(Vec3 normal, float roll) {
        Vec3 euler = getEulerAngles(normal);
        return eulerToMat((float) Math.toRadians(euler.x), (float) Math.toRadians(euler.y + 90.0F), roll);
    }

    private static Vec3 getNext(List<Vec3[]> edges, Vec3 first) {
        Iterator<Vec3[]> iter = edges.iterator();

        while (iter.hasNext()) {
            Vec3[] v = iter.next();
            double eps = 0.00001D;

            if (epsilonEquals(v[0], first, eps)) {
                iter.remove();
                return v[1];
            } else if (epsilonEquals(v[1], first, eps)) {
                iter.remove();
                return v[0];
            }
        }
        throw new RuntimeException("Didn't find next in loop!");
    }

    public static RigidBody.VertexData[] cutAndCapConvex(RigidBody.Triangle[] triangles, float[] plane) {
        RigidBody.VertexData[] result = new RigidBody.VertexData[]{null, null, new RigidBody.VertexData()};
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
                float[] deTex = new float[4];
                deTex[0] = a.u + (b.u - a.u) * interceptAB;
                deTex[1] = a.v + (b.v - a.v) * interceptAB;
                deTex[2] = a.u + (c.u - a.u) * interceptAC;
                deTex[3] = a.v + (c.v - a.v) * interceptAC;

                side2.add(new RigidBody.Triangle(d, b.pos, e, new float[]{deTex[0], deTex[1], b.u, b.v, deTex[2], deTex[3]}));
                side2.add(new RigidBody.Triangle(b.pos, c.pos, e, new float[]{b.u, b.v, c.u, c.v, deTex[2], deTex[3]}));
                side1.add(new RigidBody.Triangle(a.pos, d, e, new float[]{a.u, a.v, deTex[0], deTex[1], deTex[2], deTex[3]}));
                clippedEdges.add(new Vec3[]{d, e});
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
                float[] deTex = new float[4];
                deTex[0] = a.u + (b.u - a.u) * interceptAB;
                deTex[1] = a.v + (b.v - a.v) * interceptAB;
                deTex[2] = a.u + (c.u - a.u) * interceptAC;
                deTex[3] = a.v + (c.v - a.v) * interceptAC;

                side1.add(new RigidBody.Triangle(d, b.pos, e, new float[] { deTex[0], deTex[1], b.u, b.v, deTex[2], deTex[3] }));
                side1.add(new RigidBody.Triangle(b.pos, c.pos, e, new float[] { b.u, b.v, c.u, c.v, deTex[2], deTex[3] }));
                side2.add(new RigidBody.Triangle(a.pos, d, e, new float[] { a.u, a.v, deTex[0], deTex[1], deTex[2], deTex[3] }));

                clippedEdges.add(new Vec3[]{e, d});
            }
        }

        if (!clippedEdges.isEmpty()) {
            Matrix3f matrix3f = normalToMatrix(new Vec3(plane[0], plane[1], plane[2]), 0.0F);
            List<Vec3> orderedClipVertices = new ArrayList<>();
            orderedClipVertices.add(clippedEdges.getFirst()[0]);

            while (!clippedEdges.isEmpty()) {
                orderedClipVertices.add(getNext(clippedEdges, orderedClipVertices.getLast()));
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
                        new float[]{uv1.x, uv1.y, uv2.x, uv2.y, uv3.x, uv3.y});
                side1.add(new RigidBody.Triangle(orderedClipVertices.getFirst(), orderedClipVertices.get(i + 2), orderedClipVertices.get(i + 1), new float[6]));
                side2.add(new RigidBody.Triangle(orderedClipVertices.getFirst(), orderedClipVertices.get(i + 1), orderedClipVertices.get(i + 2), new float[6]));
            }
            result[2] = compress(cap);
        }
        result[0] = compress(side1.toArray(new RigidBody.Triangle[0]));
        result[1] = compress(side2.toArray(new RigidBody.Triangle[0]));
        return result;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(float partialTicks) {
        return AABB.INFINITE;
    }

    @Override
    public void tick() {
        if (this.entity == null) {
            if (this.level.getEntity(this.entityId) instanceof LivingEntity living) {
                this.entity = living;

                this.renderer = new FakeEntityRenderer(this.entity);
            }
        }

        if (this.top != null) this.top.tick();
        if (this.bottom != null) this.bottom.tick();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        if (this.entity == null || this.renderer == null) return;

        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

        if (!(dispatcher.getRenderer(this.entity) instanceof LivingEntityRenderer living)) return;

        if (this.top == null || this.bottom == null) {
            this.renderer.setup(() -> {
                EntityModel<? super LivingEntity> model = living.getModel();

                PoseStack poseStack = new PoseStack();

                model.attackTime = ((ILivingEntityRendererAccessor) living).invokeGetAttackAnim(this.entity, pPartialTicks);
                boolean shouldSit = this.entity.isPassenger() && (this.entity.getVehicle() != null && this.entity.getVehicle().shouldRiderSit());
                model.riding = shouldSit;
                model.young = this.entity.isBaby();
                float f = Mth.rotLerp(pPartialTicks, this.entity.yBodyRotO, this.entity.yBodyRot);
                float f1 = Mth.rotLerp(pPartialTicks, this.entity.yHeadRotO, this.entity.yHeadRot);
                float f2 = f1 - f;

                if (shouldSit && this.entity.getVehicle() instanceof LivingEntity livingentity) {
                    f = Mth.rotLerp(pPartialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
                    f2 = f1 - f;
                    float f7 = Mth.wrapDegrees(f2);

                    if (f7 < -85.0F) {
                        f7 = -85.0F;
                    }

                    if (f7 >= 85.0F) {
                        f7 = 85.0F;
                    }

                    f = f1 - f7;

                    if (f7 * f7 > 2500.0F) {
                        f += f7 * 0.2F;
                    }

                    f2 = f1 - f;
                }

                float f6 = Mth.lerp(pPartialTicks, this.entity.xRotO, this.entity.getXRot());

                if (LivingEntityRenderer.isEntityUpsideDown(this.entity)) {
                    f6 *= -1.0F;
                    f2 *= -1.0F;
                }

                f2 = Mth.wrapDegrees(f2);

                if (this.entity.hasPose(Pose.SLEEPING)) {
                    Direction direction = this.entity.getBedOrientation();
                    if (direction != null) {
                        float f3 = this.entity.getEyeHeight(Pose.STANDING) - 0.1F;
                        poseStack.translate((float) (-direction.getStepX()) * f3, 0.0F, (float) (-direction.getStepZ()) * f3);
                    }
                }

                float f8 = this.entity.getScale();
                poseStack.scale(f8, f8, f8);
                float f9 = ((ILivingEntityRendererAccessor) living).invokeGetBob(this.entity, pPartialTicks);
                ((ILivingEntityRendererAccessor) living).invokeSetupRotations(this.entity, poseStack, f9, f, pPartialTicks, f8);
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                ((ILivingEntityRendererAccessor) living).invokeScale(this.entity, poseStack, pPartialTicks);
                poseStack.translate(0.0F, -1.501F, 0.0F);

                model.prepareMobModel(this.entity, 0.0F, 0.0F, pPartialTicks);
                model.setupAnim(this.entity, 0.0F, 0.0F, f9, f2, f6);

                List<ModelPart> boxes = new ArrayList<>();

                if (model instanceof HierarchicalModel<?> hierarchical) {
                    boxes.addAll(hierarchical.root().getAllParts().toList());
                } else if (model instanceof AgeableListModel<?> ageable) {
                    for (ModelPart part : ((IAgeableListModelAccessor) ageable).invokeHeadParts()) boxes.add(part);
                    for (ModelPart part : ((IAgeableListModelAccessor) ageable).invokeBodyParts()) boxes.add(part);
                }

                double d0 = Mth.lerp(pPartialTicks, this.xo, this.x);
                double d1 = Mth.lerp(pPartialTicks, this.yo, this.y);
                double d2 = Mth.lerp(pPartialTicks, this.zo, this.z);

                this.top = new RigidBody(this.level, d0, d1, d2);
                this.bottom = new RigidBody(this.level, d0, d1, d2);

                List<RigidBody.CutModelData> top = new ArrayList<>();
                List<RigidBody.CutModelData> bottom = new ArrayList<>();

                for (ModelPart part : boxes) {
                    for (ModelPart.Cube cube : part.cubes) {
                        RigidBody.VertexData[] data = cutAndCapModelBox(poseStack, part, cube, new float[] { this.plane.x, this.plane.y, this.plane.z, -this.distance });
                        RigidBody.CutModelData tp = null;
                        RigidBody.CutModelData bt = null;

                        if (data[0].indices != null && data[0].indices.length > 0) {
                            tp = new RigidBody.CutModelData(data[0], null, false,
                                    new ConvexMeshCollider(data[0].indices, data[0].vertices(), 1.0F));
                            top.add(tp);
                        }
                        if (data[1].indices != null && data[1].indices.length > 0) {
                            bt = new RigidBody.CutModelData(data[1], null, true,
                                    new ConvexMeshCollider(data[1].indices, data[1].vertices(), 1.0F));
                            bottom.add(bt);
                        }
                        if (data[2].indices != null && data[2].indices.length > 0) {
                            tp.cap = data[2];
                            bt.cap = data[2];
                        }
                    }
                }

                this.top.addChunk(top);
                this.top.impulseVelocityDirect(new Vec3(this.plane.x * this.top.getScale(),
                        this.plane.y * this.top.getScale(),
                        this.plane.z * this.top.getScale()), this.top.globalCentroid);

                this.bottom.addChunk(bottom);
                this.bottom.impulseVelocityDirect(new Vec3(this.plane.x * this.bottom.getScale(),
                        this.plane.y * this.bottom.getScale(),
                        this.plane.z * this.bottom.getScale()), this.bottom.globalCentroid);
            });
        }

        if (this.top == null || this.bottom == null) return;

        ResourceLocation texture = living.getTextureLocation(this.entity);
        int packedLight = dispatcher.getPackedLightCoords(this.entity, pPartialTicks);

        this.top.render(texture, packedLight, pPartialTicks);
        this.bottom.render(texture, packedLight, pPartialTicks);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public record Options(int entityId, Vector3f plane, float distance) implements ParticleOptions {
        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Codec.INT.fieldOf("entityId").forGetter(options -> options.entityId),
                                ExtraCodecs.VECTOR3F.fieldOf("plane").forGetter(options -> options.plane),
                                Codec.FLOAT.fieldOf("distance").forGetter(options -> options.distance)
                        )
                        .apply(builder, Options::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                Options::entityId,
                ByteBufCodecs.VECTOR3F,
                Options::plane,
                ByteBufCodecs.FLOAT,
                Options::distance,
                Options::new
        );

        @Override
        public @NotNull ParticleType<?> getType() {
            return JJKParticles.SLICE.get();
        }
    }

    public static class Provider implements ParticleProvider<Options> {
        public Provider(SpriteSet ignored) {
        }

        public Particle createParticle(@NotNull Options pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            return new SlicedEntityParticle(pLevel, pX, pY, pZ, pType);
        }
    }
}

