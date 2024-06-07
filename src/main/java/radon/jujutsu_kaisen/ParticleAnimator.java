package radon.jujutsu_kaisen;

import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.client.particle.EmittingLightningParticle;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.function.Supplier;

public class ParticleAnimator {
    public static void sphere(Level level, Vec3 center, Supplier<Float> startRadius, Supplier<Float> stopRadius, Supplier<Float> scale, int count,
                              float opacity, boolean glow, boolean fade, int lifetime, Vector3f color) {
        for (int i = 0; i < count; i++) {
            double theta = HelperMethods.RANDOM.nextDouble() * Math.PI * 2;
            double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;

            Vec3 direction = new Vec3(Math.sin(phi) * Math.cos(theta), Math.sin(phi) * Math.sin(theta), Math.cos(phi));

            Vec3 start = center.add(direction.scale(startRadius.get()));
            Vec3 end = center.add(direction.scale(stopRadius.get()));

            level.addParticle(new TravelParticle.Options(end, color, scale.get(), opacity, glow, fade,
                    lifetime), true, start.x, start.y, start.z, 0.0D, 0.0D, 0.0D);
        }
    }

    public static void lightning(Level level, Vec3 center, float radius, Supplier<Float> scale, int count, int lifetime, Vector3f color) {
        for (int i = 0; i < count; i++) {
            double theta = HelperMethods.RANDOM.nextDouble() * Math.PI * 2;
            double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;

            Vec3 direction = new Vec3(Math.sin(phi) * Math.cos(theta), Math.sin(phi) * Math.sin(theta), Math.cos(phi));
            Vec3 offset = center.add(direction.scale(radius));

            level.addParticle(new EmittingLightningParticle.Options(color, direction, scale.get(), lifetime),
                    true, offset.x, offset.y, offset.z, 0.0D, 0.0D, 0.0D);
        }
    }

    public static void ring(Level level, Vec3 center, int count, float radius, float start, float yaw, float pitch, float roll, Vector3f color, float scale) {
        for (int i = 0; i < count; i++) {
            double angle = start + (Math.PI / 2) * ((double) i / count);
            double xOffset = radius * Math.cos(angle);
            double zOffset = radius * Math.sin(angle);
            Vec3 pos = center.add(new Vec3(xOffset, 0.0D, zOffset)
                    .zRot(roll * Mth.DEG_TO_RAD)
                    .xRot(pitch * Mth.DEG_TO_RAD)
                    .yRot(yaw * Mth.DEG_TO_RAD));
            level.addParticle(new TravelParticle.Options(pos, color, scale, 1.0F, true, true, 1),
                    true, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
        }
    }
}
