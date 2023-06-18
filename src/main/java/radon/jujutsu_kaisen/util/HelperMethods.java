package radon.jujutsu_kaisen.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class HelperMethods {
    public static final Random RANDOM = new Random();

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass) {
        int x = RANDOM.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[x];
    }

    public static Quaternionf getQuaternion(float x, float y, float z, float w) {
        w *= (float) Math.PI / 180.0D;
        float f = (float) Math.sin(w / 2.0F);
        float i = x * f;
        float j = y * f;
        float k = z * f;
        float r = (float) Math.cos(w / 2.0F);
        return new Quaternionf(i, j, k, r);
    }

    public static void rotateQ(float w, float x, float y, float z, PoseStack matrix) {
        matrix.mulPose(getQuaternion(x, y, z, w));
    }

    public static HitResult getHitResult(Level level, Entity entity, Vec3 start, Vec3 end) {
        double d0 = Double.MAX_VALUE;
        Entity entityHit = null;

        for (Entity target : level.getEntities(entity, entity.getBoundingBox().expandTowards(end).inflate(1.0D))) {
            AABB box = target.getBoundingBox();
            Optional<Vec3> optional = box.clip(start, end);

            if (optional.isPresent()) {
                double d1 = start.distanceToSqr(optional.get());

                if (d1 < d0) {
                    entityHit = target;
                    d0 = d1;
                }
            }
        }

        if (entityHit != null) {
            return new EntityHitResult(entityHit);
        }

        BlockHitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, entity));

        if (blockHit.getType() == HitResult.Type.BLOCK) {
            return blockHit;
        }
        return null;
    }
}
