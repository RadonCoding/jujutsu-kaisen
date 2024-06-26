package radon.jujutsu_kaisen.client.slice;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;

// https://github.com/Alcatergit/Hbm-s-Nuclear-Tech-GIT/blob/Custom-1.12.2/src/main/java/com/hbm/physics/AABBCollider.java
public class AABBCollider extends Collider {
    public AABB box;
    public float density = -1;

    // Only use if this is a static collider.
    public AABBCollider(AABB box) {
        this.box = box;
        this.localCentroid = box.getCenter();
    }

    public AABBCollider(AABB box, float density) {
        this.box = box;
        float w = (float) (box.maxX - box.minX);
        float h = (float) (box.maxY - box.minY);
        float d = (float) (box.maxZ - box.minZ);
        float vol = w * h * d;
        this.mass = density * vol;
        this.localCentroid = box.getCenter();
        // https://en.wikipedia.org/wiki/List_of_moments_of_inertia
        float iMass = this.mass / 12.0F;
        this.localInertiaTensor = new Matrix3f(
                iMass * (h * h + d * d), 0.0F, 0.0F,
                0.0F, iMass * (w * w + d * d), 0.0F,
                0.0F, 0.0F, iMass * (w * w + h * h));
    }

    @Override
    public Vec3 support(Vec3 direction) {
        return new Vec3(
                direction.x > 0.0D ? this.box.maxX : this.box.minX,
                direction.y > 0.0D ? this.box.maxY : this.box.minY,
                direction.z > 0.0D ? this.box.maxZ : this.box.minZ);
    }

    @Override
    public Collider copy() {
        if (this.density == -1) {
            return new AABBCollider(this.box);
        } else {
            return new AABBCollider(this.box, this.density);
        }
    }
}
