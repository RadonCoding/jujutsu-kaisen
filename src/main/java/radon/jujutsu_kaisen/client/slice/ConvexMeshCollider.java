package radon.jujutsu_kaisen.client.slice;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.lwjgl.opengl.GL11;

// https://github.com/Alcatergit/Hbm-s-Nuclear-Tech-GIT/blob/Custom-1.12.2/src/main/java/com/hbm/physics/ConvexMeshCollider.java
public class ConvexMeshCollider extends Collider {
    public RigidBody.Triangle[] triangles;
    public float[] vertices;
    public int[] indices;
    public AABB localBox;

    private ConvexMeshCollider() {
    }

    public ConvexMeshCollider(int[] indices, float[] vertices, float density) {
        this.fromData(indices, vertices, density);
    }

    private static Vec3 setVal(Vec3 vec, int idx, double val) {
        return switch (idx) {
            case 0 -> new Vec3(val, vec.y, vec.z);
            case 1 -> new Vec3(vec.x, val, vec.z);
            case 2 -> new Vec3(vec.x, vec.y, val);
            default -> throw new RuntimeException("Out of range!");
        };
    }

    private static double val(Vec3 vec, int idx) {
        return switch (idx) {
            case 0 -> vec.x;
            case 1 -> vec.y;
            case 2 -> vec.z;
            default -> throw new RuntimeException("Out of range!");
        };
    }

    // http://melax.github.io/volint.html
    public void fromData(int[] indices, float[] vertices, float density) {
        this.fromData(indices, vertices);

        this.localCentroid = this.computeCenterOfMass();
        this.mass = this.computeVolume() * density;
        this.localInertiaTensor = this.computeInertia(this.localCentroid, this.mass);
    }

    public void fromData(int[] indices, float[] vertices) {
        this.indices = indices;
         this.vertices = vertices;
        this.triangles = new RigidBody.Triangle[indices.length / 3];

        for (int i = 0; i < indices.length; i += 3) {
            Vec3 p1 = new Vec3(vertices[indices[i] * 3], vertices[indices[i] * 3 + 1], vertices[indices[i] * 3 + 2]);
            Vec3 p2 = new Vec3(vertices[indices[i + 1] * 3], vertices[indices[i + 1] * 3 + 1], vertices[indices[i + 1] * 3 + 2]);
            Vec3 p3 = new Vec3(vertices[indices[i + 2] * 3], vertices[indices[i + 2] * 3 + 1], vertices[indices[i + 2] * 3 + 2]);
            this.triangles[i / 3] = new RigidBody.Triangle(p1, p2, p3);
        }

        double maxX = this.support(RigidBody.cardinals[0]).x;
        double maxY = this.support(RigidBody.cardinals[1]).y;
        double maxZ = this.support(RigidBody.cardinals[2]).z;
        double minX = this.support(RigidBody.cardinals[3]).x;
        double minY = this.support(RigidBody.cardinals[4]).y;
        double minZ = this.support(RigidBody.cardinals[5]).z;
        this.localBox = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private float computeVolume() {
        float volume = 0.0F;

        for (RigidBody.Triangle triangle : this.triangles) {
            Matrix3f mat = new Matrix3f((float) triangle.p1.pos.x, (float) triangle.p1.pos.y, (float) triangle.p1.pos.z,
                    (float) triangle.p2.pos.x, (float) triangle.p2.pos.y, (float) triangle.p2.pos.z,
                    (float) triangle.p3.pos.x, (float) triangle.p3.pos.y, (float) triangle.p3.pos.z);
            float vol = mat.m00 * (mat.m11 * mat.m22 - mat.m12 * mat.m21) + mat.m01 *
                    (mat.m12 * mat.m20 - mat.m10 * mat.m22) + mat.m02 * (mat.m10 * mat.m21 - mat.m11 * mat.m20);
            volume += vol;
        }
        return volume / 6.0F;
    }

    private Vec3 computeCenterOfMass() {
        Vec3 center = Vec3.ZERO;
        float volume = 0.0F;

        for (RigidBody.Triangle triangle : this.triangles) {
            Matrix3f mat = new Matrix3f((float) triangle.p1.pos.x, (float) triangle.p1.pos.y, (float) triangle.p1.pos.z,
                    (float) triangle.p2.pos.x, (float) triangle.p2.pos.y, (float) triangle.p2.pos.z,
                    (float) triangle.p3.pos.x, (float) triangle.p3.pos.y, (float) triangle.p3.pos.z);
            float vol = mat.m00 * (mat.m11 * mat.m22 - mat.m12 * mat.m21) + mat.m01 *
                    (mat.m12 * mat.m20 - mat.m10 * mat.m22) + mat.m02 * (mat.m10 * mat.m21 - mat.m11 * mat.m20);
            center = center.add(vol * (mat.m00 + mat.m10 + mat.m20),
                    vol * (mat.m01 + mat.m11 + mat.m21),
                    vol * (mat.m02 + mat.m12 + mat.m22));
            volume += vol;
        }

        if (volume == 0.0F) return Vec3.ZERO;

        return new Vec3(center.x / (volume * 4.0F), center.y / (volume * 4.0F), center.z / (volume * 4.0F));
    }

    private Matrix3f computeInertia(Vec3 com, float mass) {
        float volume = 0.0F;
        Vec3 diag = Vec3.ZERO;
        Vec3 offd = Vec3.ZERO;

        for (RigidBody.Triangle t : this.triangles) {
            Matrix3f mat = new Matrix3f((float) (t.p1.pos.x - com.x), (float) (t.p1.pos.y - com.y), (float) (t.p1.pos.z - com.z),
                    (float) (t.p2.pos.x - com.x), (float) (t.p2.pos.y - com.y), (float) (t.p2.pos.z - com.z),
                    (float) (t.p3.pos.x - com.x), (float) (t.p3.pos.y - com.y), (float) (t.p3.pos.z - com.z));
            float vol = mat.m00 * (mat.m11 * mat.m22 - mat.m12 * mat.m21) + mat.m01 *
                    (mat.m12 * mat.m20 - mat.m10 * mat.m22) + mat.m02 * (mat.m10 * mat.m21 - mat.m11 * mat.m20);
            volume += vol;

            for (int j = 0; j < 3; j++) {
                int j1 = (j + 1) % 3;
                int j2 = (j + 2) % 3;
                diag = setVal(diag, j, val(diag, j) +
                        (mat.get(0, j) * mat.get(1, j) + mat.get(1, j) * mat.get(2, j) + mat.get(2, j) * mat.get(0, j) +
                                mat.get(0, j) * mat.get(0, j) + mat.get(1, j) * mat.get(1, j) + mat.get(2, j) * mat.get(2, j)) * vol);
                offd = setVal(offd, j, val(offd, j) +
                        (mat.get(0, j1) * mat.get(1, j2) + mat.get(1, j1) * mat.get(2, j2) + mat.get(2, j1) * mat.get(0, j2) +
                                mat.get(0, j1) * mat.get(2, j2) + mat.get(1, j1) * mat.get(0, j2) + mat.get(2, j1) * mat.get(1, j2) +
                                mat.get(0, j1) * mat.get(0, j2) + mat.get(1, j1) * mat.get(1, j2) + mat.get(2, j1) * mat.get(2, j2)) * vol);
            }
        }

        if (volume > 0.0F) {
            float volume2 = volume * (60.0F / 6.0F);
            diag = new Vec3(diag.x / volume2, diag.y / volume2, diag.z / volume2);
            volume2 = volume * (120.0F / 6.0F);
            offd = new Vec3(offd.x / volume2, offd.y / volume2, offd.z / volume2);
            diag = diag.scale(mass);
            offd = offd.scale(mass);
        }
        return new Matrix3f((float) (diag.y + diag.z), (float) -offd.z, (float) -offd.y,
                (float) -offd.z, (float) (diag.x + diag.z), (float) -offd.x,
                (float) -offd.y, (float) -offd.x, (float) (diag.x + diag.y));
    }

    @Override
    public Vec3 support(Vec3 dir) {
        double dot = -Float.MAX_VALUE;
        int index = 0;

        for (int i = 0; i < this.vertices.length; i += 3) {
            double newDot = dir.x * this.vertices[i] + dir.y * this.vertices[i + 1] + dir.z * this.vertices[i + 2];

            if (newDot > dot) {
                dot = newDot;
                index = i;
            }
        }
        return new Vec3(this.vertices[index], this.vertices[index + 1], this.vertices[index + 2]);
    }

    @Override
    public Collider copy() {
        ConvexMeshCollider collider = new ConvexMeshCollider();
        collider.vertices = this.vertices;
        collider.indices = this.indices;
        collider.triangles = this.triangles;
        collider.localBox = this.localBox;
        collider.localCentroid = this.localCentroid;
        collider.localInertiaTensor = this.localInertiaTensor;
        collider.mass = this.mass;
        return collider;
    }
}
