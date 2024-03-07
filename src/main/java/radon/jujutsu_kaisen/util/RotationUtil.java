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
    private static Vec2 getTargetAdjustedRotation(Vec3 start, Entity entity) {
        if (entity instanceof Targeting targeting) {
            LivingEntity target = targeting.getTarget();

            if (target != null) {
                double inaccuracy = (float) (14 - entity.level().getDifficulty().getId() * 4);
                Vec3 end = target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D);
                double d0 = end.x - start.x;
                double d1 = end.y - start.y;
                double d2 = end.z - start.z;

                Vec3 offset = new Vec3(d0, d1, d2).normalize()
                        .add(
                                HelperMethods.RANDOM.triangle(0.0D, 0.0172275D * inaccuracy),
                                HelperMethods.RANDOM.triangle(0.0D, 0.0172275D * inaccuracy),
                                HelperMethods.RANDOM.triangle(0.0D, 0.0172275D * inaccuracy)
                        );

                double d3 = Math.sqrt(offset.x * offset.x + offset.z * offset.z);

                float yaw = Mth.wrapDegrees((float) (Mth.atan2(offset.z,offset.x) * (double) (180.0F / Mth.PI)) - 90.0F);
                float pitch = Mth.wrapDegrees((float) (-(Mth.atan2(offset.y, d3) * (double) (180.0F / Mth.PI))));

                entity.setYRot(yaw);
                entity.yRotO = yaw;

                entity.setXRot(pitch);
                entity.xRotO = pitch;

                if (entity instanceof LivingEntity living) {
                    living.yHeadRot = yaw;
                    living.yHeadRotO = yaw;

                    living.yBodyRot = yaw;
                    living.yBodyRotO = yaw;
                }
                return new Vec2(pitch, yaw);
            }
        }
        return new Vec2(entity.getXRot(), entity.getYRot());
    }

    public static float getTargetAdjustedYRot(Vec3 start, Entity entity) {
        Vec2 rot = getTargetAdjustedRotation(start, entity);
        return rot.y;
    }

    public static float getTargetAdjustedXRot(Vec3 start, Entity entity) {
        Vec2 rot = getTargetAdjustedRotation(start, entity);
        return rot.x;
    }

    public static Vec3 getTargetAdjustedLookAngle(Vec3 start, Entity entity) {
        Vec2 rot = getTargetAdjustedRotation(start, entity);
        return calculateViewVector(rot.x, rot.y);
    }

    public static float getTargetAdjustedYRot(Entity entity) {
        return getTargetAdjustedYRot(entity.getEyePosition(), entity);
    }

    public static float getTargetAdjustedXRot(Entity entity) {
        return getTargetAdjustedXRot(entity.getEyePosition(), entity);
    }

    public static Vec3 getTargetAdjustedLookAngle(Entity entity) {
        return getTargetAdjustedLookAngle(entity.getEyePosition(), entity);
    }

    public static boolean hasLineOfSight(Vec3 start, Entity entity, Entity target) {
        if (target.level() != entity.level()) {
            return false;
        } else {
            Vec3 end = new Vec3(target.getX(), target.getEyeY(), target.getZ());

            if (end.distanceTo(start) > 128.0D) {
                return false;
            } else {
                return entity.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS;
            }
        }
    }

    public static boolean hasLineOfSight(Entity entity, Entity target) {
        return hasLineOfSight(entity.getEyePosition(), entity, target);
    }

    public static Vec3 calculateViewVector(float pitch, float yaw) {
        float f = pitch * (Mth.PI / 180.0F);
        float f1 = -yaw * (Mth.PI / 180.0F);
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
