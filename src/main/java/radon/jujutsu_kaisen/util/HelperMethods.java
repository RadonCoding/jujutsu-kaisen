package radon.jujutsu_kaisen.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.config.ConfigHolder;

import java.util.*;

public class HelperMethods {
    public static final Random RANDOM = new Random();
    private static final String[] WORDS = {"Nah, I'd win.", "Stand proud.", "You can cook.", "Did you pray today?", "You're strong.", "Are you the strongest because?", "Owari da.", "I shall never forget you.", "With this treasure i summon...", "Have you ever trained?"};

    public static boolean applyModifier(LivingEntity owner, Attribute attribute, UUID identifier, String name, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = owner.getAttribute(attribute);
        AttributeModifier modifier = new AttributeModifier(identifier, name, amount, operation);

        if (instance != null) {
            AttributeModifier existing = instance.getModifier(identifier);

            if (existing != null) {
                if (existing.getAmount() != amount) {
                    instance.removeModifier(identifier);
                    instance.addTransientModifier(modifier);
                    return true;
                }
            } else {
                instance.addTransientModifier(modifier);
                return true;
            }
        }
        return false;
    }

    public static void removeModifier(LivingEntity owner, Attribute attribute, UUID identifier) {
        AttributeInstance instance = owner.getAttribute(attribute);

        if (instance != null) {
            instance.removeModifier(identifier);
        }
    }

    public static void giveAdvancement(ServerPlayer player, String name) {
        MinecraftServer server = player.getServer();
        assert server != null;
        Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(JujutsuKaisen.MOD_ID,
                String.format("%s/%s", JujutsuKaisen.MOD_ID, name)));

        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);

            if (!progress.isDone()) {
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criterion);
                }
            }
        }
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

    public static Set<String> getRandomWordCombo(int count) {
        if (count > WORDS.length)
            throw new IllegalArgumentException("Number of words requested exceeds the available word list.");

        Set<String> combo = new HashSet<>();

        while (combo.size() < count) {
            combo.add(WORDS[RANDOM.nextInt(WORDS.length)]);
        }
        return combo;
    }

    public static <E> E getWeightedRandom(Map<E, Double> weights, Random random) {
        E result = null;
        double bestValue = Double.MAX_VALUE;

        for (E element : weights.keySet()) {
            double value = -Math.log(random.nextDouble()) / weights.get(element);

            if (value < bestValue) {
                bestValue = value;
                result = element;
            }
        }

        return result;
    }

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
                        dst.setItemSlot(slot, stack.copy());
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

    public static <T extends ParticleOptions> void sendParticles(ServerLevel pLevel, T pType, boolean pLongDistance, double pPosX, double pPosY, double pPosZ) {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(pType, pLongDistance, pPosX, pPosY, pPosZ, 0.0F, 0.0F, 0.0F, 0.0F, 0);

        for (int i = 0; i < pLevel.players().size(); i++) {
            ServerPlayer player = pLevel.players().get(i);
            sendParticles(pLevel, player, pLongDistance, pPosX, pPosY, pPosZ, packet);
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

    public static int getLevenshteinDistance(String x, String y) {
        int m = x.length();
        int n = y.length();

        int[][] T = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            T[i][0] = i;
        }

        for (int j = 1; j <= n; j++) {
            T[0][j] = j;
        }

        int cost;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                cost = x.charAt(i - 1) == y.charAt(j - 1) ? 0: 1;
                T[i][j] = Integer.min(Integer.min(T[i - 1][j] + 1, T[i][j - 1] + 1),
                        T[i - 1][j - 1] + cost);
            }
        }
        return T[m][n];
    }

    public static float strcmp(String x, String y) {
        float max = Float.max(x.length(), y.length());

        if (max > 0) {
            return 1.0F - ((max - getLevenshteinDistance(x, y)) / max);
        }
        return 0.0F;
    }

    private static class Position {
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

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass, Set<T> blacklist) {
        int x = RANDOM.nextInt(enumClass.getEnumConstants().length);
        T random = enumClass.getEnumConstants()[x];

        for (T blacklisted : blacklist) {
            if (random == blacklisted) {
                x = RANDOM.nextInt(enumClass.getEnumConstants().length);
                random = enumClass.getEnumConstants()[x];
            }
        }
        return random;
    }

    public static boolean isExperienced(float experience) {
        return experience >= ConfigHolder.SERVER.requiredExperienceForExperienced.get().floatValue();
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

        for (Entity entity : level.getEntities(null, AABB.ofSize(bounds.getCenter(), 32.0D, 32.0D, 32.0D))) {
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
}
