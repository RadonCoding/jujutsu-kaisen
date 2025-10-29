package radon.jujutsu_kaisen;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.entity.IBarrier;
import radon.jujutsu_kaisen.entity.IDomain;
import radon.jujutsu_kaisen.entity.IVeil;
import radon.jujutsu_kaisen.entity.domain.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.OpenDomainExpansionEntity;

import javax.annotation.Nullable;
import java.util.*;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class VeilHandler {
    private static final Map<ResourceKey<Level>, Set<UUID>> barriers = new HashMap<>();

    public static void barrier(ResourceKey<Level> dimension, UUID identifier) {
        if (!barriers.containsKey(dimension)) {
            barriers.put(dimension, new HashSet<>());
        }
        barriers.get(dimension).add(identifier);
    }

    public static Set<IBarrier> getBarriers(ServerLevel level, BlockPos target) {
        Set<IBarrier> result = new HashSet<>();

        if (barriers.containsKey(level.dimension())) {
            for (UUID identifier : barriers.get(level.dimension())) {
                if (!(level.getEntity(identifier) instanceof IBarrier barrier) || !barrier.isInsideBarrier(target))
                    continue;
                result.add(barrier);
            }
        }
        return result;
    }

    public static Set<IBarrier> getBarriers(ServerLevel level, AABB bounds) {
        Set<IBarrier> result = new HashSet<>();

        if (barriers.containsKey(level.dimension())) {
            for (UUID identifier : barriers.get(level.dimension())) {
                if (!(level.getEntity(identifier) instanceof IBarrier barrier) || !bounds.intersects(barrier.getBounds()))
                    continue;
                result.add(barrier);
            }
        }
        return result;
    }

    public static boolean canSpawn(ServerLevel level, Mob mob, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);

        IBarrier owner = getOwner(level, target);

        if (owner instanceof IVeil veil) return veil.isAllowed(mob);

        return true;
    }

    public static boolean canDamage(Entity attacker, LivingEntity victim, ServerLevel level, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);

        IBarrier owner = getOwner(level, target);

        if (owner instanceof IVeil veil && veil.isAllowed(attacker) && veil.isAllowed(victim)) return veil.canDamage(victim);

        return true;
    }

    public static boolean canDestroy(@Nullable Entity entity, ServerLevel level, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);

        if (level.getBlockState(target).is(JJKBlocks.VEIL_ROD)) return true;

        IBarrier owner = getOwner(level, target);

        if (owner instanceof IVeil veil) return veil.canDestroy(entity, target);

        return true;
    }

    @Nullable
    private static IBarrier getOwner(ServerLevel level, BlockPos target) {
        IBarrier strongest = null;
        boolean tie = false;

        if (barriers.containsKey(level.dimension())) {
            for (UUID identifier : barriers.get(level.dimension())) {
                if (!(level.getEntity(identifier) instanceof IBarrier current)) continue;
                if (!current.isBarrierOrInside(target)) continue;

                if (strongest == null) {
                    strongest = current;
                    continue;
                }

                if (strongest instanceof OpenDomainExpansionEntity && current instanceof ClosedDomainExpansionEntity closed) {
                    // Open domain already strongest, closed domain is skipped if the block is on its outer shell
                    if (closed.isBarrier(target)) continue;
                } else if (strongest instanceof ClosedDomainExpansionEntity closed && current instanceof OpenDomainExpansionEntity open) {
                    // Closed domain strongest, open domain overrides if block is on outer shell
                    if (closed.isBarrier(target) && open.isInsideBarrier(target)) {
                        strongest = current;
                        tie = false;
                        continue;
                    }
                }

                float strengthA = current.getStrength();
                float strengthB = strongest.getStrength();

                if (strengthA > strengthB) {
                    strongest = current;
                    tie = false;
                } else if (strengthA == strengthB) {
                    tie = true;
                }
            }
        }

        return tie ? null : strongest;
    }


    public static boolean isOwnedBy(ServerLevel level, BlockPos target, IBarrier barrier) {
        return getOwner(level, target) == barrier;
    }

    public static boolean isOwnedNonDomain(ServerLevel level, BlockPos target) {
        return !(getOwner(level, target) instanceof IDomain);
    }

    public static boolean isInsideBarrier(ServerLevel level, BlockPos target) {
        return getOwner(level, target) != null;
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Level level = event.getLevel();
        Entity entity = event.getEntity();

        if (barriers.containsKey(level.dimension())) {
            Set<UUID> current = barriers.get(level.dimension());

            if (!current.contains(entity.getUUID())) return;

            current.remove(entity.getUUID());

            if (current.isEmpty()) {
                barriers.remove(level.dimension());
            }
        }
    }
}