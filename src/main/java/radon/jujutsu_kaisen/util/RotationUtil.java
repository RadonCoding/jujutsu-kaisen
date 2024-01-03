package radon.jujutsu_kaisen.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class RotationUtil {
    public static Vec2 getTargetAdjustedRotation(Entity entity) {
        if (entity instanceof Targeting targeting) {
            LivingEntity target = targeting.getTarget();

            if (target != null) {
                Vec3 start = entity.getEyePosition();
                Vec3 end = target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D);
                double d0 = end.x - start.x;
                double d1 = end.y - start.y;
                double d2 = end.z - start.z;
                double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                float yaw = Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * (double) (180.0F / (float) Math.PI)) - 90.0F);
                float pitch = Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * (double) (180.0F / (float) Math.PI))));
                return new Vec2(pitch, yaw);
            }
        }
        return new Vec2(entity.getXRot(), entity.getYRot());
    }

    public static float getTargetAdjustedYRot(Entity entity) {
        Vec2 rot = getTargetAdjustedRotation(entity);
        return rot.y;
    }

    public static float getTargetAdjustedXRot(Entity entity) {
        Vec2 rot = getTargetAdjustedRotation(entity);
        return rot.x;
    }

    public static Vec3 getTargetAdjustedLookAngle(Entity entity) {
        Vec2 rot = getTargetAdjustedRotation(entity);
        return calculateViewVector(rot.y, rot.x);
    }

    public static Vec3 calculateViewVector(float yaw, float pitch) {
        float f = pitch * ((float) Math.PI / 180.0F);
        float f1 = -yaw * ((float) Math.PI / 180.0F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    public static float getYaw(Vec3 vec) {
        return (float) (-Mth.atan2(vec.x, vec.z) * (180.0D / Math.PI));
    }

    public static HitResult getHitResult(Entity entity, Vec3 start, Vec3 end) {
        return getHitResult(entity, start, end, target -> !target.isSpectator() && target.isPickable());
    }

    public static HitResult getHitResult(Entity entity, Vec3 start, Vec3 end, Predicate<Entity> filter) {
        Level level = entity.level();

        HitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));

        if (blockHit.getType() != HitResult.Type.MISS) {
            end = blockHit.getLocation();
        }

        HitResult entityHit = ProjectileUtil.getEntityHitResult(level, entity, start, end, entity.getBoundingBox()
                .expandTowards(end.subtract(start)).inflate(2.0D), filter);

        if (entityHit != null) {
            return entityHit;
        }
        return blockHit;
    }

    public static HitResult getLookAtHit(Entity entity, double range, Predicate<Entity> filter) {
        Vec3 start = entity.getEyePosition();
        Vec3 look = getTargetAdjustedLookAngle(entity);
        Vec3 end = start.add(look.scale(range));
        return getHitResult(entity, start, end, filter);
    }

    public static HitResult getLookAtHit(Entity entity, double range) {
        return getLookAtHit(entity, range, target -> !target.isSpectator() && target.isPickable());
    }
}
