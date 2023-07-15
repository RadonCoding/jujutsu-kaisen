package radon.jujutsu_kaisen.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

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

    public static HitResult getHitResult(Entity entity, Vec3 start, Vec3 end) {
        Level level = entity.level;

        HitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));

        if (blockHit.getType() != HitResult.Type.MISS) {
            end = blockHit.getLocation();
        }

        HitResult entityHit = ProjectileUtil.getEntityHitResult(level, entity, start, end, entity.getBoundingBox()
                .expandTowards(end.subtract(start)).inflate(1.0D), target -> !target.isSpectator() && target.isPickable());

        if (entityHit != null) {
            return entityHit;
        }
        return blockHit;
    }

    public static HitResult getLookAtHit(Entity entity, double range) {
        Vec3 start = entity.getEyePosition();
        Vec3 look = entity.getLookAngle();
        Vec3 end = start.add(look.scale(range));
        return getHitResult(entity, start, end);
    }
}
