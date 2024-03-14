package radon.jujutsu_kaisen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
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

    public static Set<DomainExpansionEntity> getDomains(ServerLevel level) {
        if (!domains.containsKey(level.dimension())) return Set.of();

        Set<UUID> current = domains.get(level.dimension());

        if (current.isEmpty()) return Set.of();

        Set<DomainExpansionEntity> result = new HashSet<>();

        for (UUID identifier : current) {
            if (!(level.getEntity(identifier) instanceof DomainExpansionEntity domain)) continue;
            result.add(domain);
        }
        return result;
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

                if (!isProtected(level, target)) continue;

                if (!be.isAllowed(mob)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean canDamage(@Nullable Entity entity, ServerLevel level, double x, double y, double z) {
        BlockPos target = BlockPos.containing(x, y, z);

        for (Map.Entry<ResourceKey<Level>, Set<BlockPos>> entry : veils.entrySet()) {
            ResourceKey<Level> dimension1 = entry.getKey();

            for (BlockPos pos : entry.getValue()) {
                // So that veil rods can still be broken
                if (target.equals(pos)) continue;

                if (level.dimension() != dimension1 || !(level.getBlockEntity(pos) instanceof VeilRodBlockEntity be))
                    continue;

                if (entity != null && entity.getUUID() == be.ownerUUID) continue;

                if (!isProtected(level, target)) continue;

                for (Modifier modifier : be.modifiers) {
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

                if (level.dimension() != dimension1 || !(level.getBlockEntity(pos) instanceof VeilRodBlockEntity be))
                    continue;

                if (entity != null && entity.getUUID() == be.ownerUUID) continue;

                if (!isProtected(level, target)) continue;

                for (Modifier modifier : be.modifiers) {
                    if (modifier.getAction() == Modifier.Action.DENY && modifier.getType() == Modifier.Type.GRIEFING) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isProtectedBy(ServerLevel level, ResourceKey<Level> dimension, BlockPos veil, BlockPos target) {
        if (level.dimension() != dimension || !(level.getBlockEntity(veil) instanceof VeilRodBlockEntity be))
            return false;

        int radius = be.getSize();
        BlockPos relative = target.subtract(veil);

        if (relative.distSqr(Vec3i.ZERO) >= radius * radius) return false;

        boolean valid = true;

        // If there's a domain that is at least 50% the strength of the veil's then it will win
        for (Map.Entry<ResourceKey<Level>, Set<UUID>> domainEntry : domains.entrySet()) {
            if (!valid) break;

            if (domainEntry.getKey() != dimension) continue;

            for (UUID identifier : domainEntry.getValue()) {
                if (!(level.getEntity(identifier) instanceof DomainExpansionEntity domain)) continue;
                if (!domain.isInsideBarrier(target)) continue;

                if (be.ownerUUID == null || !(level.getEntity(be.ownerUUID) instanceof LivingEntity veilOwner)) {
                    valid = false;
                    break;
                }

                IJujutsuCapability veilOwnerCap = veilOwner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (veilOwnerCap == null) {
                    valid = false;
                    break;
                }

                ISkillData veilOwnerData = veilOwnerCap.getSkillData();

                LivingEntity domainOwner = domain.getOwner();

                if (domainOwner == null) continue;

                IJujutsuCapability domainOwnerCap = domainOwner.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (domainOwnerCap == null) continue;

                ISkillData domainOwnerData = domainOwnerCap.getSkillData();

                if (Math.round(veilOwnerData.getSkill(Skill.BARRIER) * (1.0F / ConfigHolder.SERVER.domainStrength.get())) < domainOwnerData.getSkill(Skill.BARRIER)) {
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }

    public static boolean isProtected(ServerLevel level, BlockPos target) {
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
