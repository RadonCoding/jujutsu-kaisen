package radon.jujutsu_kaisen.ability.boogie_woogie;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.effect.CursedEnergyImbuedItem;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SwapOthers extends Ability {
    public static final double RANGE = 30.0D;
    private static final int EXPIRATION = 5 * 20;

    private static final Map<UUID, AbstractMap.SimpleEntry<UUID, Long>> TARGETS = new HashMap<>();

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable Entity getTarget(LivingEntity owner) {
        if (RotationUtil.getLookAtHit(owner, RANGE, target -> !target.isSpectator()) instanceof EntityHitResult hit) {
            Entity target = hit.getEntity();
            return SwapSelf.canSwap(target) ? target : null;
        }
        return null;
    }

    public static void setTarget(LivingEntity owner, Entity target) {
        TARGETS.put(owner.getUUID(), new AbstractMap.SimpleEntry<>(target.getUUID(), owner.level().getGameTime()));
    }
    
    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (!(owner.level() instanceof ServerLevel level)) return;

        Entity first = this.getTarget(owner);

        if (first != null) {
            if (TARGETS.containsKey(owner.getUUID())) {
                AbstractMap.SimpleEntry<UUID, Long> entry = TARGETS.get(owner.getUUID());

                if (owner.level().getGameTime() - entry.getValue() >= EXPIRATION) {
                    TARGETS.put(owner.getUUID(), new AbstractMap.SimpleEntry<>(first.getUUID(), owner.level().getGameTime()));
                    return;
                }

                Entity second = level.getEntity(entry.getKey());

                if (second == null) return;

                owner.level().playSound(null, second.getX(), second.getY(), second.getZ(), JJKSounds.CLAP.get(), SoundSource.MASTER, 2.0F, 1.0F);
                owner.level().playSound(null, first.getX(), first.getY(), first.getZ(), JJKSounds.CLAP.get(), SoundSource.MASTER, 1.0F, 1.0F);

                Vec3 pos = second.position();

                Vec2 firstRot = first.getRotationVector();
                Vec2 secondRot = second.getRotationVector();

                second.teleportTo(first.getX(), first.getY(), first.getZ());
                first.teleportTo(pos.x, pos.y, pos.z);

                second.setYRot(firstRot.y);
                second.setXRot(firstRot.x);

                first.setYRot(secondRot.y);
                first.setXRot(secondRot.x);

                TARGETS.remove(owner.getUUID());
            } else {
                TARGETS.put(owner.getUUID(), new AbstractMap.SimpleEntry<>(first.getUUID(), owner.level().getGameTime()));
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.MELEE;
    }
}
