package radon.jujutsu_kaisen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VeilHandler {
    private static final Map<ResourceKey<Level>, BlockPos> veils = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<UUID>> domains = new HashMap<>();

    public static void veil(ResourceKey<Level> dimension, BlockPos pos) {
        veils.put(dimension, pos);
    }

    public static void domain(ResourceKey<Level> dimension, UUID identifier) {
        if (!domains.containsKey(dimension)) {
            domains.put(dimension, new HashSet<>());
        }
        domains.get(dimension).add(identifier);
    }

    public static Set<DomainExpansionEntity> getDomains(ServerLevel level) {
        Set<DomainExpansionEntity> result = new HashSet<>();

        for (UUID identifier : domains.getOrDefault(level.dimension(), Set.of())) {
            if (!(level.getEntity(identifier) instanceof DomainExpansionEntity domain)) continue;
            result.add(domain);
        }
        return result;
    }

    public static Set<DomainExpansionEntity> getDomains(ServerLevel level, BlockPos pos) {
        Set<DomainExpansionEntity> result = new HashSet<>();

        for (UUID identifier : domains.getOrDefault(level.dimension(), Set.of())) {
            if (!(level.getEntity(identifier) instanceof DomainExpansionEntity domain) || !domain.isInsideBarrier(pos)) continue;
            result.add(domain);
        }
        return result;
    }

    public static Set<DomainExpansionEntity> getDomains(ServerLevel level, AABB bounds) {
        Set<DomainExpansionEntity> result = new HashSet<>();

        for (UUID identifier : domains.getOrDefault(level.dimension(), Set.of())) {
            if (!(level.getEntity(identifier) instanceof DomainExpansionEntity domain) || !bounds.intersects(domain.getBounds())) continue;
            result.add(domain);
        }
        return result;
    }

    public static boolean canSpawn(Mob mob, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);

        for (Map.Entry<ResourceKey<Level>, BlockPos> entry : veils.entrySet()) {
            ResourceKey<Level> dimension = entry.getKey();
            BlockPos pos = entry.getValue();

            if (mob.level().dimension() != dimension || !(mob.level().getBlockEntity(pos) instanceof VeilRodBlockEntity be))
                continue;

            int radius = be.getSize();
            BlockPos relative = target.subtract(pos);

            if (relative.distSqr(Vec3i.ZERO) < radius * radius) {
                return false; //VeilBlockEntity.isAllowed(pos, mob);
            }
        }
        return true;
    }

    public static boolean isProtected(Level accessor, BlockPos target) {
        for (Map.Entry<ResourceKey<Level>, BlockPos> entry : veils.entrySet()) {
            ResourceKey<Level> dimension = entry.getKey();
            BlockPos pos = entry.getValue();

            if (accessor.dimension() != dimension || !(accessor.getBlockEntity(pos) instanceof VeilRodBlockEntity be))
                continue;

            int radius = be.getSize();
            BlockPos relative = target.subtract(pos);

            if (relative.distSqr(Vec3i.ZERO) < radius * radius) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.side == LogicalSide.CLIENT || event.type != TickEvent.Type.LEVEL || event.phase == TickEvent.Phase.START || event.level.isClientSide)
            return;

        veils.entrySet().removeIf(entry ->
                event.level.dimension() == entry.getKey() && !(event.level.getBlockEntity(entry.getValue()) instanceof VeilRodBlockEntity));

        domains.entrySet().removeIf(entry ->
                event.level.dimension() == entry.getKey() && entry.getValue().isEmpty());


        if (domains.containsKey(event.level.dimension())) {
            domains.get(event.level.dimension()).removeIf(identifier ->
                    !(((ServerLevel) event.level).getEntity(identifier) instanceof DomainExpansionEntity));
        }
    }
}
