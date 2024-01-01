package radon.jujutsu_kaisen.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.misc.RCT1;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.awt.*;
import java.util.*;
import java.util.function.Predicate;

public class HelperMethods {
    public static final RandomSource RANDOM = RandomSource.createThreadSafe();
    private static final String[] WORDS = {"Nah, I'd win.", "Stand proud.", "You can cook.", "Did you pray today?", "You're strong.", "Are you the strongest because?", "Owari da.", "I shall never forget you.", "With this treasure i summon...", "Have you ever trained?"};

    public static boolean isMelee(DamageSource source) {
        return !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK) || source.is(JJKDamageSources.SPLIT_SOUL_KATANA)) ||
                source instanceof JJKDamageSources.JujutsuDamageSource jujutsu && jujutsu.getAbility() != null && jujutsu.getAbility().isMelee();
    }

    public static int getRGB24(Vector3f rgb) {
        return FastColor.ARGB32.color(255, Math.round(rgb.x * 255.0F), Math.round(rgb.y * 255.0F), Math.round(rgb.z * 255.0F));
    }

    public static Vec3 getLookAngle(Entity entity) {
        if (entity instanceof Targeting targeting) {
            LivingEntity target = targeting.getTarget();

            if (target != null) {
                Vec3 start = entity.getEyePosition();
                Vec3 end = target.getEyePosition();
                double d0 = end.x - start.x;
                double d1 = end.y - start.y;
                double d2 = end.z - start.z;
                double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                float yaw = Mth.wrapDegrees((float) (Mth.atan2(d2, d0) * (double) (180.0F / (float) Math.PI)) - 90.0F);
                float pitch = Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * (double) (180.0F / (float) Math.PI))));
                return calculateViewVector(yaw, pitch);
            }
        }
        return entity.getLookAngle();
    }

    public static boolean isDestroyable(BlockGetter getter, @Nullable LivingEntity source, BlockPos pos) {
        if (source != null && !(source instanceof Player) && !source.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return false;

        BlockState state = getter.getBlockState(pos);
        boolean destroyable = !state.isAir() && state.getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE;

        if (!destroyable && source != null && source.level() instanceof ServerLevel level && getter.getBlockEntity(pos) instanceof DomainBlockEntity be) {
            UUID identifier = be.getIdentifier();
            destroyable = identifier == null || !(level.getEntity(identifier) instanceof DomainExpansionEntity domain) ||
                    !domain.isInsideBarrier(source.blockPosition());
        }
        return destroyable;
    }

    @Nullable
    public static RCT1 getRCTTier(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.isUnlocked(JJKAbilities.RCT3.get())) return JJKAbilities.RCT3.get();
        if (cap.isUnlocked(JJKAbilities.RCT2.get())) return JJKAbilities.RCT2.get();
        if (cap.isUnlocked(JJKAbilities.RCT1.get())) return JJKAbilities.RCT1.get();

        return null;
    }

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

    public static <E> E getWeightedRandom(Map<E, Double> weights, RandomSource random) {
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

    public static <T extends ParticleOptions> void sendParticle(ServerPlayer player, T pType, boolean pLongDistance, double pPosX, double pPosY, double pPosZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(pType, pLongDistance, pPosX, pPosY, pPosZ, (float) pXSpeed, (float) pYSpeed, (float) pZSpeed, 1.0F, 0);
        sendParticles(player.serverLevel(), player, pLongDistance, pPosX, pPosY, pPosZ, packet);
    }

    public static <T extends ParticleOptions> void sendParticles(ServerLevel pLevel, T pType, boolean pLongDistance, double pPosX, double pPosY, double pPosZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(pType, pLongDistance, pPosX, pPosY, pPosZ, (float) pXSpeed, (float) pYSpeed, (float) pZSpeed, 1.0F, 0);

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

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        return clazz.getEnumConstants()[RANDOM.nextInt(clazz.getEnumConstants().length)];
    }

    public static <T extends Enum<T>> T randomEnum(Class<T> clazz, Set<T> excluded) {
        if (!excluded.isEmpty()) {
            EnumSet<T> available = EnumSet.complementOf(EnumSet.copyOf(excluded));

            if (!available.isEmpty()) {
                return (T) available.toArray()[RANDOM.nextInt(available.size())];
            }
        }
        return clazz.getEnumConstants()[RANDOM.nextInt(clazz.getEnumConstants().length)];
    }

    public static boolean isExperienced(float experience) {
        return experience >= ConfigHolder.SERVER.requiredExperienceForExperienced.get().floatValue();
    }

    public static float getPower(float experience) {
        return 1.0F + experience / 1500.0F;
    }

    public static float getYaw(Vec3 vec) {
        return (float) (-Mth.atan2(vec.x, vec.z) * (180.0D / Math.PI));
    }

    public static float getXRotD(Entity src, Vec3 target) {
        double d0 = target.x - src.getX();
        double d1 = target.y - src.getEyeY();
        double d2 = target.z - src.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        return (float) (-(Mth.atan2(d1, d3) * (double) (180.0F / (float) Math.PI)));
    }

    public static float getYRotD(Entity src, Vec3 target) {
        double d0 = target.x - src.getX();
        double d1 = target.z - src.getZ();
        return (float) (Mth.atan2(d1, d0) * (double) (180.0F / (float) Math.PI)) - 90.0F;
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
        Vec3 look = HelperMethods.getLookAngle(entity);
        Vec3 end = start.add(look.scale(range));
        return getHitResult(entity, start, end, filter);
    }

    public static HitResult getLookAtHit(Entity entity, double range) {
        return getLookAtHit(entity, range, target -> !target.isSpectator() && target.isPickable());
    }

    public static int toRGB24(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF));
    }
}
