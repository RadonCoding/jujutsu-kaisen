package radon.jujutsu_kaisen.client.slice;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.util.MathUtil;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// https://github.com/Alcatergit/Hbm-s-Nuclear-Tech-GIT/blob/Custom-1.12.2/src/main/java/com/hbm/physics/RigidBody.java
public class RigidBody {
    private static final float SCALE = 3.5F;
    public static final Vec3[] cardinals = new Vec3[]{
            new Vec3(1.0F, 0.0F, 0.0F), new Vec3(0.0F, 1.0F, 0.0F), new Vec3(0.0F, 0.0F, 1.0F),
            new Vec3(-1.0F, 0.0F, 0.0F), new Vec3(0.0F, -1.0F, 0.0F), new Vec3(0.0F, 0.0F, -1.0F)
    };
    public static final RigidBody DUMMY = new RigidBody(null) {
        @Override
        public void addChunk(List<CutModelData> chunk) {
        }

        @Override
        public void solveContacts(float step) {
        }

        @Override
        public void impulse(Vec3 force, Vec3 position) {
        }

        @Override
        public void updateOrientation() {
        }

        @Override
        public void updateGlobalCentroidFromPosition() {
        }

        @Override
        public void updatePositionFromGlobalCentroid() {
        }

        @Override
        public void step(float step) {
        }

        @Override
        public Vec3 globalToLocalPos(Vec3 global) {
            return global;
        }

        @Override
        public Vec3 localToGlobalPos(Vec3 global) {
            return global;
        }

        @Override
        public Vec3 globalToLocalVec(Vec3 global) {
            return global;
        }

        @Override
        public Vec3 localToGlobalVec(Vec3 local) {
            return local;
        }

        @Override
        public void addLinearVelocity(Vec3 velocity) {
        }

        @Override
        public void addAngularVelocity(Vec3 velocity) {
        }

        @Override
        public void addContact(Contact contact) {
        }
    };

    static {
        DUMMY.invRotation = new Matrix3f(DUMMY.rotation);
        DUMMY.localInertiaTensor = new Matrix3f();
        DUMMY.invLocalInertiaTensor = new Matrix3f();
        DUMMY.invGlobalInertiaTensor = new Matrix3f();
        DUMMY.localCentroid = Vec3.ZERO;
        DUMMY.globalCentroid = Vec3.ZERO;
    }

    private static final ResourceLocation BLOOD = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/misc/blood.png");

    private final List<CutModelData> chunk = new ArrayList<>();

    public Level level;
    public AABB bounds;
    public List<Collider> colliders = new ArrayList<>();
    public List<AABB> colliderBoundingBoxes = new ArrayList<>();
    public Vec3 position = Vec3.ZERO;
    public Vec3 globalCentroid = Vec3.ZERO;
    public Matrix3f rotation = new Matrix3f();
    public Matrix3f invRotation;
    public Vec3 prevPosition = Vec3.ZERO;
    public Quaternionf prevRotation = new Quaternionf();
    public Vec3 linearVelocity = Vec3.ZERO;
    public Vec3 angularVelocity = Vec3.ZERO;
    public Vec3 force = Vec3.ZERO;
    public Vec3 torque = Vec3.ZERO;
    public float mass;
    public float invMass;
    public Matrix3f localInertiaTensor;
    public Matrix3f invLocalInertiaTensor;
    public Matrix3f invGlobalInertiaTensor;
    public float friction = 0.5F;
    public float restitution = 0.0F;
    public Vec3 localCentroid;
    public ContactManifold contacts = new ContactManifold();

    public RigidBody(Level level) {
        this.level = level;
    }

    public RigidBody(Level level, double x, double y, double z) {
        this(level);

        this.position = new Vec3(x, y, z);
    }

    public float getScale() {
        return SCALE * (!this.chunk.isEmpty() && this.chunk.getFirst().flip ? -1.0F : 1.0F);
    }

    public void addChunk(List<CutModelData> chunk) {
        this.chunk.addAll(chunk);

        for (CutModelData data : chunk) {
            this.colliders.add(data.collider);
        }

        this.localCentroid = Vec3.ZERO;
        this.mass = 0.0F;

        for (Collider collider : this.colliders) {
            this.mass += collider.mass;
            this.localCentroid = this.localCentroid.add(collider.localCentroid.scale(collider.mass));
        }
        this.invMass = 1.0F / this.mass;
        this.localCentroid = this.localCentroid.scale(this.invMass);

        this.localInertiaTensor = new Matrix3f();

        for (Collider collider : this.colliders) {
            // https://en.wikipedia.org/wiki/Parallel_axis_theorem
            Vec3 colliderToLocal = this.localCentroid.subtract(collider.localCentroid);
            double dot = colliderToLocal.dot(colliderToLocal);
            Matrix3f outer = MathUtil.outer(colliderToLocal, colliderToLocal);

            Matrix3f colliderToLocalMat = new Matrix3f();
            colliderToLocalMat.identity();
            colliderToLocalMat.scale((float) dot);
            colliderToLocalMat.sub(outer);
            colliderToLocalMat.scale(collider.mass);
            Matrix3f cLocalIT = new Matrix3f(collider.localInertiaTensor);
            cLocalIT.add(colliderToLocalMat);
            this.localInertiaTensor.add(cLocalIT);
        }

        this.invLocalInertiaTensor = new Matrix3f(this.localInertiaTensor).invert();

        this.invGlobalInertiaTensor = new Matrix3f();
        this.updateOrientation();
        this.updateGlobalCentroidFromPosition();
        this.prevPosition = this.position;
        this.updateAABBs();
    }

    public void tick() {
        // Clear contacts because if for example blocks have been destroyed
        for (int i = 0; i < this.contacts.contactCount; i++) {
            this.contacts.removeContact(i);
        }

        this.setPrevData();

        int time = 8;
        float step = 0.05F / (float) time;

        for (int i = 0; i < time; i++) {
            this.step(step);
        }
    }

    public void step(float step) {
        this.contacts.update();

        for (VoxelShape shape : this.level.getCollisions(null, this.bounds)) {
            for (int i = 0; i < this.colliders.size(); i++) {
                Collider a = this.colliders.get(i);

                if (!this.colliderBoundingBoxes.get(i).intersects(shape.bounds())) {
                    continue;
                }

                Collider b = new AABBCollider(shape.bounds());
                GJK.GJKInfo info = GJK.colliding(this, null, a, b);

                if (info.result == GJK.Result.COLLIDING) {
                    this.contacts.addContact(new Contact(this, null, a, b, info));
                }
            }
        }

        this.solveContacts(step);
        this.integrateVelocityAndPosition(step);
    }

    public void integrateVelocityAndPosition(float step) {
        // Integrate velocity
        this.linearVelocity = this.linearVelocity.add(this.force.scale(this.invMass * step));
        this.angularVelocity = this.angularVelocity.add(MathUtil.transform(this.torque.scale(step), this.invGlobalInertiaTensor));

        this.force = Vec3.ZERO;
        this.torque = Vec3.ZERO;

        // Integrate position
        this.globalCentroid = this.globalCentroid.add(this.linearVelocity.scale(step));

        if (this.angularVelocity.lengthSqr() > 0.0D) {
            Vec3 axis = this.angularVelocity.normalize();
            double angle = this.angularVelocity.length() * step;
            Matrix3f turn = new Matrix3f();
            turn.set(new AxisAngle4f((float) angle, axis.toVector3f()));
            turn.mul(this.rotation);
            this.rotation = turn;
            this.updateOrientation();
        }
        this.updatePositionFromGlobalCentroid();
        this.updateAABBs();
        this.addLinearVelocity(new Vec3(0.0D, -9.81F * step, 0.0D));
    }

    public void setPrevData() {
        this.prevPosition = this.position;
        MathUtil.quaternionfFromMatrix3f(this.prevRotation, this.rotation);
    }

    public void addContact(Contact contact) {
        this.contacts.addContact(contact);
    }

    public void solveContacts(float step) {
        for (int i = 0; i < this.contacts.contactCount; i++) {
            this.contacts.contacts[i].init(step);
        }

        int velocityIterations = 4;

        for (int i = 0; i < velocityIterations; i++) {
            for (int j = 0; j < this.contacts.contactCount; j++) {
                this.contacts.contacts[j].solve();
            }
        }
    }

    public Vec3 localToGlobalPos(Vec3 global) {
        return MathUtil.transform(global, this.rotation).add(this.position);
    }

    public Vec3 globalToLocalPos(Vec3 global) {
        return MathUtil.transform(global.subtract(this.position), this.invRotation);
    }

    public Vec3 localToGlobalVec(Vec3 local) {
        return MathUtil.transform(local, this.rotation);
    }

    public Vec3 globalToLocalVec(Vec3 global) {
        return MathUtil.transform(global, this.invRotation);
    }

    public void addLinearVelocity(Vec3 velocity) {
        this.linearVelocity = this.linearVelocity.add(velocity);
    }

    public void addAngularVelocity(Vec3 velocity) {
        this.angularVelocity = this.angularVelocity.add(velocity);
    }

    public void impulse(Vec3 force, Vec3 position) {
        this.force = this.force.add(force);
        this.torque = this.torque.add(position.subtract(this.globalCentroid).cross(force));
    }

    public void impulseVelocity(Vec3 force, Vec3 position) {
        this.linearVelocity = this.linearVelocity.add(force.scale(this.invMass));
        this.angularVelocity = this.angularVelocity.add(MathUtil.transform(position.subtract(this.globalCentroid).cross(force), this.invGlobalInertiaTensor));
    }

    public void impulseVelocityDirect(Vec3 force, Vec3 position) {
        this.linearVelocity = this.linearVelocity.add(force);
        this.angularVelocity = this.angularVelocity.add(position.subtract(this.globalCentroid).cross(force));
    }

    public void updateOrientation() {
        Quaternionf quaternionf = new Quaternionf();
        MathUtil.quaternionfFromMatrix3f(quaternionf, this.rotation);
        quaternionf.normalize();
        MathUtil.matrix3fFromQuaterionf(this.rotation, quaternionf);

        this.invRotation = new Matrix3f(this.rotation).transpose();

        this.invGlobalInertiaTensor.set(this.invRotation);
        this.invGlobalInertiaTensor.mul(this.invLocalInertiaTensor);
        this.invGlobalInertiaTensor.mul(this.rotation);
    }

    public void updatePositionFromGlobalCentroid() {
        this.position = this.globalCentroid.add(MathUtil.transform(this.localCentroid.reverse(), this.rotation));
    }

    public void updateGlobalCentroidFromPosition() {
        this.globalCentroid = MathUtil.transform(this.localCentroid, this.rotation).add(this.position);
    }

    public void updateAABBs() {
        this.colliderBoundingBoxes.clear();
        double tMaxX, tMaxY, tMaxZ, tMinX, tMinY, tMinZ;
        tMaxX = tMaxY = tMaxZ = -Double.MAX_VALUE;
        tMinX = tMinY = tMinZ = Double.MAX_VALUE;

        for (Collider collider : this.colliders) {
            double maxX = GJK.localSupport(this, collider, cardinals[0]).x;
            double maxY = GJK.localSupport(this, collider, cardinals[1]).y;
            double maxZ = GJK.localSupport(this, collider, cardinals[2]).z;
            double minX = GJK.localSupport(this, collider, cardinals[3]).x;
            double minY = GJK.localSupport(this, collider, cardinals[4]).y;
            double minZ = GJK.localSupport(this, collider, cardinals[5]).z;
            this.colliderBoundingBoxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
            tMaxX = Math.max(tMaxX, maxX);
            tMaxY = Math.max(tMaxY, maxY);
            tMaxZ = Math.max(tMaxZ, maxZ);
            tMinX = Math.min(tMinX, minX);
            tMinY = Math.min(tMinY, minY);
            tMinZ = Math.min(tMinZ, minZ);
        }
        this.bounds = new AABB(tMinX, tMinY, tMinZ, tMaxX, tMaxY, tMaxZ);
    }

    public void render(int packedLight, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        PoseStack poseStack = new PoseStack();

        double d0 = Mth.lerp(partialTicks, this.prevPosition.x, this.position.x);
        double d1 = Mth.lerp(partialTicks, this.prevPosition.y, this.position.y);
        double d2 = Mth.lerp(partialTicks, this.prevPosition.z, this.position.z);

        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        poseStack.translate(d0 - cam.x, d1 - cam.y, d2 - cam.z);

        Quaternionf quaternionf = new Quaternionf();
        MathUtil.quaternionfFromMatrix3f(quaternionf, this.rotation);
        quaternionf.nlerp(this.prevRotation, 1.0F - partialTicks);
        quaternionf.normalize();

        poseStack.mulPose(quaternionf);

        Matrix4f matrix4f = poseStack.last().pose();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        Map<RenderType, List<CutModelData>> grouped = new HashMap<>();

        for (RigidBody.CutModelData data : this.chunk) {
            grouped.computeIfAbsent(data.type, ignored -> new ArrayList<>()).add(data);
        }

        for (Map.Entry<RenderType, List<RigidBody.CutModelData>> entry : grouped.entrySet()) {
            RenderType type = entry.getKey();
            List<RigidBody.CutModelData> dataList = entry.getValue();

            builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);

            for (RigidBody.CutModelData data : dataList) {
                data.data.tessellate(builder, matrix4f, packedLight);
            }

            type.end(builder, RenderSystem.getVertexSorting());
        }

        for (RigidBody.CutModelData data : this.chunk) {
            if (data.cap == null) continue;

            RenderType type = RenderType.entityCutoutNoCull(BLOOD);

            builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);

            data.cap.tessellate(builder, matrix4f, packedLight);

            type.end(builder, RenderSystem.getVertexSorting());
        }
    }

    public static class CutModelData {
        public RenderType type;
        public VertexData data;
        public VertexData cap;
        public boolean flip;
        public final ConvexMeshCollider collider;

        public CutModelData(RenderType type, VertexData data, VertexData cap, boolean flip, ConvexMeshCollider collider) {
            this.type = type;
            this.data = data;
            this.cap = cap;
            this.flip = flip;
            this.collider = collider;
        }
    }

    public static class VertexData {
        public Vec3[] positions;
        public int @Nullable[] indices;
        public float[] uv;

        public void tessellate(BufferBuilder builder, Matrix4f matrix4f, int packedLight) {
            this.tessellate(builder, matrix4f, false, packedLight);
        }

        // TODO: Use correct color
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

        public float[] vertices() {
            float[] vertices = new float[this.positions.length * 3];

            for (int i = 0; i < this.positions.length; i++) {
                Vec3 pos = this.positions[i];
                vertices[i * 3] = (float) pos.x;
                vertices[i * 3 + 1] = (float) pos.y;
                vertices[i * 3 + 2] = (float) pos.z;
            }
            return vertices;
        }
    }

    public static class Triangle {
        public TexVertex p1, p2, p3;

        public Triangle(Vec3 p1, Vec3 p2, Vec3 p3) {
            this.p1 = new TexVertex(p1);
            this.p2 = new TexVertex(p2);
            this.p3 = new TexVertex(p3);
        }

        public Triangle(Vec3 p1, Vec3 p2, Vec3 p3, float[] uv) {
            this.p1 = new TexVertex(p1, uv[0], uv[1]);
            this.p2 = new TexVertex(p2, uv[2], uv[3]);
            this.p3 = new TexVertex(p3, uv[4], uv[5]);
        }

        public static class TexVertex {
            public Vec3 pos;
            public float u, v;

            public TexVertex(Vec3 pos) {
                this.pos = pos;
            }

            public TexVertex(Vec3 pos, float u, float v) {
                this.pos = pos;
                this.u = u;
                this.v = v;
            }
        }
    }
}
