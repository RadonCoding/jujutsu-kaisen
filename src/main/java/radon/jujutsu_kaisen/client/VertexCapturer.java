package radon.jujutsu_kaisen.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.client.slice.RigidBody;

import java.util.ArrayList;
import java.util.List;

public class VertexCapturer {
    public static boolean capture;

    public static List<Capture> captured = new ArrayList<>();

    public record Capture(RenderType type, List<RigidBody.Triangle[]> triangles) {}
}
