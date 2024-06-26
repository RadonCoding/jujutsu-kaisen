package radon.jujutsu_kaisen.client.slice;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;

// https://github.com/Alcatergit/Hbm-s-Nuclear-Tech-GIT/blob/Custom-1.12.2/src/main/java/com/hbm/physics/Collider.java
public abstract class Collider {
    public float mass;
    public Matrix3f localInertiaTensor;
    public Vec3 localCentroid;

    public abstract Vec3 support(Vec3 direction);

    public abstract Collider copy();
}
