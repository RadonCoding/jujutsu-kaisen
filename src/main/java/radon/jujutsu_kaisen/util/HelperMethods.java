package radon.jujutsu_kaisen.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.mixin.common.ILevelAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelperMethods {
    public static final Random RANDOM = new Random();

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass) {
        int x = RANDOM.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[x];
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

    public static List<Entity> getEntityCollisions(Level pLevel, AABB pCollisionBox) {
        List<Entity> collisions = new ArrayList<>();

        for (Entity entity : ((ILevelAccessor) pLevel).getEntitiesInvoker().getAll()) {
            if (entity.getBoundingBox().intersects(pCollisionBox)) {
                collisions.add(entity);
            }
        }
        return collisions;
    }

    public static <T extends Entity> List<T> getEntityCollisionsOfClass(Class<T> clazz, Level pLevel, AABB pCollisionBox) {
        List<Entity> collisions = getEntityCollisions(pLevel, pCollisionBox);

        List<T> result = new ArrayList<>();

        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        for (Entity collision : collisions) {
            T casted = test.tryCast(collision);

            if (casted != null) {
                result.add(casted);
            }
        }
        return result;
    }
}
