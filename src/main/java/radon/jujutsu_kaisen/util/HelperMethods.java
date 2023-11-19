package radon.jujutsu_kaisen.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.config.ConfigHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelperMethods {
    public static final Random RANDOM = new Random();

    public static void convertTo(LivingEntity src, LivingEntity dst, boolean transferInventory, boolean kill) {
        if (!src.isRemoved()) {
            dst.copyPosition(src);

            if (src.hasCustomName()) {
                dst.setCustomName(src.getCustomName());
                dst.setCustomNameVisible(src.isCustomNameVisible());
            }

            dst.setInvulnerable(src.isInvulnerable());

            if (transferInventory) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack stack = src.getItemBySlot(slot);

                    if (!stack.isEmpty()) {
                        dst.setItemSlot(slot, stack.copyAndClear());
                    }
                }
            }

            src.level().addFreshEntity(dst);

            if (src.isPassenger()) {
                Entity vehicle = src.getVehicle();
                src.stopRiding();

                if (vehicle != null) {
                    vehicle.startRiding(vehicle, true);
                }
            }

            if (kill) {
                if (src instanceof Player) {
                    src.kill();
                } else {
                    src.discard();
                }
            }
        }
    }

    private static void sendParticles(ServerLevel pLevel, ServerPlayer pPlayer, boolean pLongDistance, double pPosX, double pPosY, double pPosZ, Packet<?> pPacket) {
        if (pPlayer.level() == pLevel) {
            BlockPos pos = pPlayer.blockPosition();

            if (pos.closerToCenterThan(new Vec3(pPosX, pPosY, pPosZ), pLongDistance ? 512.0D : 32.0D)) {
                pPlayer.connection.send(pPacket);
            }
        }
    }

    public static  <T extends ParticleOptions> void sendParticles(ServerLevel pLevel,T pType, boolean pLongDistance, double pPosX, double pPosY, double pPosZ) {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(pType, pLongDistance, pPosX, pPosY, pPosZ, 0.0F, 0.0F, 0.0F, 0.0F, 0);

        for (int i = 0; i < pLevel.players().size(); i++) {
            ServerPlayer player = pLevel.players().get(i);
            sendParticles(pLevel, player, pLongDistance, pPosX, pPosY, pPosZ,  packet);
        }
    }


    public static SorcererGrade getGrade(float experience) {
        SorcererGrade result = SorcererGrade.GRADE_4;

        for (SorcererGrade grade : SorcererGrade.values()) {
            if (experience < grade.getRequiredExperience()) break;

            result = grade;
        }
        return result;
    }

    public static int strcmp(String str1, String str2) {
        int minLength = Math.min(str1.length(), str2.length());
        int difference = 0;

        for (int i = 0; i < minLength; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                difference = minLength - i;
                break;
            }
        }
        return difference;
    }

    static class Position {
        double x;
        double z;

        public boolean clamp(double minX, double minZ, double maxX, double maxZ) {
            boolean flag = false;

            if (this.x < minX) {
                this.x = minX;
                flag = true;
            } else if (this.x > maxX) {
                this.x = maxX;
                flag = true;
            }

            if (this.z < minZ) {
                this.z = minZ;
                flag = true;
            } else if (this.z > maxZ) {
                this.z = maxZ;
                flag = true;
            }
            return flag;
        }

        public int getSpawnY(BlockGetter level, int y) {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(this.x, y + 1, this.z);
            boolean flag = level.getBlockState(pos).isAir();
            pos.move(Direction.DOWN);

            boolean flag2;

            for (boolean flag1 = level.getBlockState(pos).isAir(); pos.getY() > level.getMinBuildHeight(); flag1 = flag2) {
                pos.move(Direction.DOWN);
                flag2 = level.getBlockState(pos).isAir();

                if (!flag2 && flag1 && flag) {
                    return pos.getY() + 1;
                }
                flag = flag1;
            }
            return y + 1;
        }

        public boolean isSafe(BlockGetter level, int y) {
            BlockPos pos = BlockPos.containing(this.x, this.getSpawnY(level, y) - 1, this.z);
            BlockState state = level.getBlockState(pos);
            return pos.getY() < y && !state.liquid() && !state.is(BlockTags.FIRE);
        }

        public void randomize(RandomSource random, double minX, double minZ, double maxX, double maxZ) {
            this.x = Mth.nextDouble(random, minX, maxX);
            this.z = Mth.nextDouble(random, minZ, maxZ);
        }
    }

    private static void spreadPosition(ServerLevel level, RandomSource random, double minX, double minZ, double maxX, double maxZ, int pMaxHeight, Position pos) {
        boolean flag = true;
        int i = 0;

        while (i < 10000 && flag) {
            flag = pos.clamp(minX, minZ, maxX, maxZ);

            if (!flag && !pos.isSafe(level, pMaxHeight)) {
                pos.randomize(random, minX, minZ, maxX, maxZ);
                flag = true;
            }
            i++;
        }
    }

    public static BlockPos findSafePos(ServerLevel level, LivingEntity owner) {
        RandomSource random = RandomSource.create();

        double d0 = owner.getX() - 10000;
        double d1 = owner.getZ() - 10000;
        double d2 = owner.getX() + 10000;
        double d3 = owner.getZ() + 10000;

        Position pos = new Position();
        pos.randomize(random, d0, d1, d2, d3);

        spreadPosition(level, random, d0, d1, d2, d3, level.dimensionType().height(), pos);

        return BlockPos.containing((double) Mth.floor(pos.x) + 0.5D, pos.getSpawnY(level, level.dimensionType().height()), (double) Mth.floor(pos.z) + 0.5D);
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass) {
        int x = RANDOM.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[x];
    }

    public static boolean isStrongest(float experience) {
        return experience >= ConfigHolder.SERVER.requiredExperienceForStrongest.get().floatValue();
    }

    public static float getPower(float experience) {
        return 1.0F + experience / 1500.0F;
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

    public static float getYRotD(Entity src, Vec3 target) {
        double d0 = target.x() - src.getX();
        double d1 = target.z() - src.getZ();
        return (float) (Mth.atan2(d1, d0) * (double) (180.0F / (float) Math.PI)) - 90.0F;
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
                ((g & 0xFF) << 8) |
                ((b & 0xFF));
    }

    public static int toRGB24(Vector3f color) {
        return (((int) (color.x() * 255.0F) & 0xFF) << 16) |
                (((int) (color.y() * 255.0F) & 0xFF) << 8) |
                (((int) (color.z() * 255.0F) & 0xFF));
    }
}
