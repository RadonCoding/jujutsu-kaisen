package radon.jujutsu_kaisen.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.config.ConfigHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelperMethods {
    public static final Random RANDOM = new Random();

    private static BlockPos getTopNonCollidingPos(LevelReader level, EntityType<?> type, int x, int z) {
        int i = level.getHeight(SpawnPlacements.getHeightmapType(type), x, z);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, i, z);

        if (level.dimensionType().hasCeiling()) {
            do {
                pos.move(Direction.DOWN);
            } while(!level.getBlockState(pos).isAir());

            do {
                pos.move(Direction.DOWN);
            } while (level.getBlockState(pos).isAir() && pos.getY() > level.getMinBuildHeight());
        }

        if (SpawnPlacements.getPlacementType(type) == SpawnPlacements.Type.ON_GROUND) {
            BlockPos below = pos.below();

            if (level.getBlockState(below).isPathfindable(level, below, PathComputationType.LAND)) {
                return below;
            }
        }
        return pos.immutable();
    }

    public static BlockPos findSafePos(ServerLevel level, Player player) {
        BlockPos.MutableBlockPos pos = player.blockPosition().mutable();

        level.getPoiManager().ensureLoadedAndValid(level, pos, 16);

        double minX = level.getWorldBorder().getMinX();
        double maxX = level.getWorldBorder().getMaxX();
        double minZ = level.getWorldBorder().getMinZ();
        double maxZ = level.getWorldBorder().getMaxZ();

        while (!NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType(player.getType()), level, pos, player.getType())) {
            pos.set(minX + RANDOM.nextDouble() * (maxX - minX), 0, minZ + RANDOM.nextDouble() * (maxZ - minZ));

            level.getPoiManager().ensureLoadedAndValid(level, pos, 16);

            pos.set(getTopNonCollidingPos(level, player.getType(), pos.getX(), pos.getZ()));
        }
        return pos.immutable();
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass) {
        int x = RANDOM.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[x];
    }

    public static boolean isStrongest(float experience) {
        return experience >= ConfigHolder.SERVER.requiredExperienceForStrongest.get().floatValue();
    }

    public static float getPower(float experience) {
        return 1.0F + experience / 1000.0F;
    }

    public static float getYaw(Vec3 vec) {
        return (float) (-Mth.atan2(vec.x(), vec.z()) * (180.0D / Math.PI));
    }

    public static float getXRotD(Entity src, Vec3 target) {
        double d0 = target.x() - src.getX();
        double d1 = target.y() - src.getEyeY();
        double d2 = target.z() - src.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        return (float) (-(Mth.atan2(d1, d3) * (double) (180.0F / (float) Math.PI)));
    }

    public static float getYRotD(Entity src,Vec3 target) {
        double d0 = target.x() - src.getX();
        double d1 = target.z() - src.getZ();
        return (float) (Mth.atan2(d1, d0) * (double) (180.0F / (float) Math.PI)) - 90.0F;
    }

    public static Vec3 getLookAngle(LivingEntity entity) {
        float pitch = entity.xRotO + Mth.wrapDegrees(entity.getXRot() - entity.xRotO);
        float yaw = (entity.yHeadRotO + Mth.wrapDegrees(entity.yHeadRot - entity.yHeadRotO));

        float f = pitch * ((float) Math.PI / 180.0F);
        float f1 = -yaw * ((float) Math.PI / 180.0F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    public static Vec3 getLookAngle(Entity entity) {
        float pitch = entity.xRotO + Mth.wrapDegrees(entity.getXRot() - entity.xRotO);
        float yaw = (entity.yRotO + Mth.wrapDegrees(entity.getYRot() - entity.yRotO));

        float f = pitch * ((float) Math.PI / 180.0F);
        float f1 = -yaw * ((float) Math.PI / 180.0F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    public static HitResult getHitResult(Entity entity, Vec3 start, Vec3 end) {
        return getHitResult(entity, start, end, true);
    }

    public static HitResult getHitResult(Entity entity, Vec3 start, Vec3 end, boolean hasToBePickable) {
        Level level = entity.level();

        HitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));

        if (blockHit.getType() != HitResult.Type.MISS) {
            end = blockHit.getLocation();
        }

        HitResult entityHit = ProjectileUtil.getEntityHitResult(level, entity, start, end, entity.getBoundingBox()
                .expandTowards(end.subtract(start)).inflate(1.0D), target -> !target.isSpectator() && (!hasToBePickable || target.isPickable()));

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

    public static HitResult getLookAtHitAny(Entity entity, double range) {
        Vec3 start = entity.getEyePosition();
        Vec3 look = entity.getLookAngle();
        Vec3 end = start.add(look.scale(range));
        return getHitResult(entity, start, end, false);
    }

    public static List<Entity> getEntityCollisions(Level level, AABB bounds) {
        List<Entity> collisions = new ArrayList<>();

        for (Entity entity : level.getEntities(null, AABB.ofSize(bounds.getCenter(), 64.0D, 64.0D, 64.0D))) {
            if (bounds.intersects(entity.getBoundingBox())) {
                collisions.add(entity);
            }
        }
        return collisions;
    }

    public static <T extends Entity> List<T> getEntityCollisionsOfClass(Class<T> clazz, Level level, AABB bounds) {
        List<Entity> collisions = getEntityCollisions(level, bounds);

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

    public static int toRGB24(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF));
    }

    public static int toRGB24(Vector3f color) {
        return (((int) (color.x() * 255.0F) & 0xFF) << 16) |
                (((int) (color.y() * 255.0F) & 0xFF) << 8)  |
                (((int) (color.z() * 255.0F) & 0xFF));
    }
}
