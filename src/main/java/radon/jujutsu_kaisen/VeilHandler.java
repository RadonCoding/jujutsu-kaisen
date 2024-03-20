package radon.jujutsu_kaisen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VeilHandler {
    private static final Map<ResourceKey<Level>, Set<BlockPos>> veils = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<UUID>> domains = new HashMap<>();

    public static void veil(ResourceKey<Level> dimension, BlockPos pos) {
        if (!veils.containsKey(dimension)) {
            veils.put(dimension, new HashSet<>());
        }
        veils.get(dimension).add(pos);
    }

    public static void domain(ResourceKey<Level> dimension, UUID identifier) {
        if (!domains.containsKey(dimension)) {
            domains.put(dimension, new HashSet<>());
        }
        domains.get(dimension).add(identifier);
    }

    public static Set<DomainExpansionEntity> getDomains(ServerLevel level, BlockPos pos) {
        if (!domains.containsKey(level.dimension())) return Set.of();

        Set<UUID> current = domains.get(level.dimension());

        if (current.isEmpty()) return Set.of();

        Set<DomainExpansionEntity> result = new HashSet<>();

        for (UUID identifier : current) {
            if (!(level.getEntity(identifier) instanceof DomainExpansionEntity domain) || !domain.isInsideBarrier(pos)) continue;
            result.add(domain);
        }
        return result;
    }

    public static Set<DomainExpansionEntity> getDomains(ServerLevel level, AABB bounds) {
        if (!domains.containsKey(level.dimension())) return Set.of();

        Set<UUID> current = domains.get(level.dimension());

        if (current.isEmpty()) return Set.of();

        Set<DomainExpansionEntity> result = new HashSet<>();

        for (UUID identifier : current) {
            if (!(level.getEntity(identifier) instanceof DomainExpansionEntity domain) || !bounds.intersects(domain.getBounds())) continue;
            result.add(domain);
        }
        return result;
    }

    public static boolean canSpawn(ServerLevel level, Mob mob, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);

        for (Map.Entry<ResourceKey<Level>, Set<BlockPos>> entry : veils.entrySet()) {
            ResourceKey<Level> dimension = entry.getKey();

            for (BlockPos pos : entry.getValue()) {
                if (mob.level().dimension() != dimension || !(mob.level().getBlockEntity(pos) instanceof VeilRodBlockEntity be))
                    continue;

                if (!isProtectedByVeil(level, target)) continue;

                if (!be.isAllowed(mob)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean canDamage(Entity attacker, LivingEntity victim, ServerLevel level, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);

        for (Map.Entry<ResourceKey<Level>, Set<BlockPos>> entry : veils.entrySet()) {
            ResourceKey<Level> dimension1 = entry.getKey();

            for (BlockPos pos : entry.getValue()) {
                if (level.dimension() != dimension1) continue;

                if (!(level.getBlockEntity(pos) instanceof VeilRodBlockEntity rod)) continue;

                if (attacker.getUUID().equals(rod.ownerUUID)) continue;

                if (!isProtectedByVeil(level, target)) continue;

                if (!rod.isAllowed(attacker) || !rod.isAllowed(victim)) continue;

                for (Modifier modifier : rod.modifiers) {
                    if (modifier.getAction() == Modifier.Action.DENY && modifier.getType() == Modifier.Type.VIOLENCE) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean canDestroy(@Nullable Entity entity, ServerLevel level, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);

        for (Map.Entry<ResourceKey<Level>, Set<BlockPos>> veilEntry : veils.entrySet()) {
            ResourceKey<Level> dimension1 = veilEntry.getKey();

            for (BlockPos pos : veilEntry.getValue()) {
                // So that veil rods can still be broken
                if (target.equals(pos)) continue;

                // So that veils can still be destroyed
                if (level.getBlockEntity(target) instanceof VeilBlockEntity veil) {
                    if (veil.getParent() == pos) continue;
                }

                if (level.dimension() != dimension1 || !(level.getBlockEntity(pos) instanceof VeilRodBlockEntity be))
                    continue;

                if (entity != null && entity.getUUID().equals(be.ownerUUID)) continue;

                if (!isProtectedByVeil(level, target)) continue;

                for (Modifier modifier : be.modifiers) {
                    if (modifier.getAction() == Modifier.Action.DENY && modifier.getType() == Modifier.Type.GRIEFING) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static int getBarrierSkill(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        ISkillData data = cap.getSkillData();

        return data.getSkill(Skill.BARRIER);
    }

    public static boolean isProtectedByDomain(ServerLevel level, ResourceKey<Level> dimension, BlockPos veil, BlockPos target) {
        if (level.dimension() != dimension || !(level.getBlockEntity(veil) instanceof VeilRodBlockEntity be)) return false;

        // If there's a domain that is at least 50% the strength of the veil's then it will win
        for (Map.Entry<ResourceKey<Level>, Set<UUID>> domainEntry : domains.entrySet()) {
            if (domainEntry.getKey() != dimension) continue;

            for (UUID identifier : domainEntry.getValue()) {
                if (!(level.getEntity(identifier) instanceof DomainExpansionEntity domain)) continue;
                if (!domain.isInsideBarrier(target)) continue;

                if (be.ownerUUID == null || !(level.getEntity(be.ownerUUID) instanceof LivingEntity veilOwner)) {
                    return true;
                }

                LivingEntity domainOwner = domain.getOwner();

                if (domainOwner == null) continue;

                boolean outside = veilOwner.distanceToSqr(veil.getCenter()) >= be.getSize() * be.getSize();

                if (Math.round(getBarrierSkill(veilOwner) * (1.0F / ConfigHolder.SERVER.domainStrength.get()) * (outside ? 1.5F : 1.0F)) < getBarrierSkill(domainOwner)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isProtectedBy(ServerLevel level, ResourceKey<Level> dimension, BlockPos veil, BlockPos target) {
        if (level.dimension() != dimension || !(level.getBlockEntity(veil) instanceof VeilRodBlockEntity firstVeil))
            return false;

        if (target.subtract(veil).distSqr(Vec3i.ZERO) >= firstVeil.getSize() * firstVeil.getSize()) return false;

        if (isProtectedByDomain(level, dimension, veil, target)) return false;

        BlockPos protector = null;

        for (Map.Entry<ResourceKey<Level>, Set<BlockPos>> entry : veils.entrySet()) {
            for (BlockPos pos : entry.getValue()) {
                if (pos == veil) continue;

                if (!(level.getBlockEntity(pos) instanceof VeilRodBlockEntity secondVeil)) {
                    continue;
                }

                if (target.subtract(pos).distSqr(Vec3i.ZERO) >= secondVeil.getSize() * secondVeil.getSize()) continue;

                if (isProtectedByDomain(level, dimension, veil, target)) continue;

                if (firstVeil.ownerUUID == null || !(level.getEntity(firstVeil.ownerUUID) instanceof LivingEntity firstOwner)) {
                    protector = pos;
                    continue;
                }

                if (protector == null) {
                    protector = pos;
                    continue;
                }

                if (secondVeil.ownerUUID == null || !(level.getEntity(secondVeil.ownerUUID) instanceof LivingEntity secondOwner)) {
                    continue;
                }

                int first = Math.round(getBarrierSkill(firstOwner) * (firstOwner.distanceToSqr(protector.getCenter()) >=
                        firstVeil.getSize() * firstVeil.getSize() ? 1.5F : 1.0F));
                int second = Math.round(getBarrierSkill(secondOwner) * (secondOwner.distanceToSqr(pos.getCenter()) >=
                        secondVeil.getSize() * secondVeil.getSize() ? 1.5F : 1.0F));

                // Check if the current one is stronger than the current strongest
                if (second > first) {
                    protector = pos;
                    continue;
                }

                // If they're equal neither wins
                if (first == second) {
                    protector = null;
                }
            }
        }
        return protector == null;
    }

    public static boolean isProtectedByVeil(ServerLevel level, BlockPos target) {
        for (Map.Entry<ResourceKey<Level>, Set<BlockPos>> entry : veils.entrySet()) {
            ResourceKey<Level> dimension = entry.getKey();

            for (BlockPos pos : entry.getValue()) {
                if (isProtectedBy(level, dimension, pos, target)) return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Level level = event.getLevel();
        Entity entity = event.getEntity();

        if (domains.containsKey(level.dimension())) {
            Set<UUID> current = domains.get(level.dimension());
            current.remove(entity.getUUID());

            if (current.isEmpty()) {
                domains.remove(level.dimension());
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.side == LogicalSide.CLIENT || event.type != TickEvent.Type.LEVEL || event.phase == TickEvent.Phase.START || event.level.isClientSide)
            return;

        if (veils.containsKey(event.level.dimension())) {
            Set<BlockPos> current = veils.get(event.level.dimension());
            current.removeIf(pos -> (!(event.level.getBlockEntity(pos) instanceof VeilRodBlockEntity be) || !be.isValid()));
        }
    }
}
