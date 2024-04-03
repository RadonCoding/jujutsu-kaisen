package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.boogie_woogie.SwapSelf;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportOthers extends Ability {
    private static final double RANGE = 100.0D;
    private static final int EXPIRATION = 5 * 20;

    private static final Map<UUID, AbstractMap.SimpleEntry<UUID, Long>> TARGETS = new HashMap<>();

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable Entity getEntityTarget(LivingEntity owner) {
        return TeleportSelf.getTarget(owner) instanceof EntityHitResult hit ? hit.getEntity() : null;
    }

    private static boolean hasTarget(LivingEntity owner) {
        if (!TARGETS.containsKey(owner.getUUID())) return false;

        AbstractMap.SimpleEntry<UUID, Long> entry = TARGETS.get(owner.getUUID());

        if (owner.level().getGameTime() - entry.getValue() >= EXPIRATION) return false;

        return ((ServerLevel) owner.level()).getEntity(entry.getKey()) != null;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (!(owner.level() instanceof ServerLevel level)) return;

        HitResult first = TeleportSelf.getTarget(owner);

        if (first == null) return;

        if (TARGETS.containsKey(owner.getUUID())) {
            AbstractMap.SimpleEntry<UUID, Long> entry = TARGETS.get(owner.getUUID());

            if (owner.level().getGameTime() - entry.getValue() >= EXPIRATION) {
                if (first instanceof EntityHitResult hit) {
                    TARGETS.put(owner.getUUID(), new AbstractMap.SimpleEntry<>(hit.getEntity().getUUID(), owner.level().getGameTime()));
                }
                return;
            }

            Entity second = level.getEntity(entry.getKey());

            if (second == null) return;

            TeleportSelf.teleport(second, first.getLocation());

            TARGETS.remove(owner.getUUID());
        } else if (first instanceof EntityHitResult hit) {
            TARGETS.put(owner.getUUID(), new AbstractMap.SimpleEntry<>(hit.getEntity().getUUID(), owner.level().getGameTime()));
        }
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            if (hasTarget(owner)) {
                if (TeleportSelf.getTarget(owner) == null) return Status.FAILURE;
            } else {
                Entity target = this.getEntityTarget(owner);

                if (target == null) {
                    return Status.FAILURE;
                }
            }
        }
        return super.isTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 25.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
