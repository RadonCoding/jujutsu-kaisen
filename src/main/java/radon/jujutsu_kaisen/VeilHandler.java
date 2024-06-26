package radon.jujutsu_kaisen;


import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.data.DataProvider;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.entity.IBarrier;
import radon.jujutsu_kaisen.entity.IDomain;
import radon.jujutsu_kaisen.entity.IVeil;

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

        Optional<IDomainData> data = DataProvider.getDataIfPresent(level, JJKAttachmentTypes.DOMAIN);

        if (data.isPresent()) {
            ResourceKey<Level> key = data.get().getOriginal();

            if (key != null) {
                ServerLevel original = level.getServer().getLevel(key);

                if (original != null) {
                    result.addAll(getBarriers(original, BlockPos.containing(target.getCenter().scale(original.dimensionType().coordinateScale() /
                            level.dimensionType().coordinateScale()))));
                }
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

        if (owner instanceof IVeil veil && veil.isAllowed(attacker) && veil.isAllowed(victim))
            return veil.canDamage(victim);

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

        if (barriers.containsKey(level.dimension())) {
            for (UUID identifier : barriers.get(level.dimension())) {
                if (!(level.getEntity(identifier) instanceof IBarrier current)) continue;
                if (!current.isBarrier(target)) continue;

                if (strongest == null) {
                    strongest = current;
                    continue;
                }

                if (current.getStrength() > strongest.getStrength()) strongest = current;
            }
        }
        return strongest;
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
