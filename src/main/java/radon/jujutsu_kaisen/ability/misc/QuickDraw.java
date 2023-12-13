package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.BindingVow;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuickDraw extends Ability implements Ability.IToggled {
    private static final Map<UUID, Vec3> POSITIONS = new HashMap<>();

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (!POSITIONS.containsKey(owner.getUUID())) return;

        SimpleDomainEntity domain = cap.getSummonByClass(level, SimpleDomainEntity.class);

        if (domain == null) return;

        for (Entity entity : owner.level().getEntities(owner, domain.getBoundingBox())) {
            if (entity == domain) continue;

            if (entity instanceof AbstractArrow || entity instanceof ThrowableItemProjectile) {
                entity.discard();
            } else {
                owner.swing(InteractionHand.MAIN_HAND, true);

                if (owner instanceof Player player) {
                    player.attack(entity);
                } else {
                    owner.doHurtTarget(entity);
                }
            }
        }
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        if (POSITIONS.containsKey(owner.getUUID())) {
            if (owner.position() != POSITIONS.get(owner.getUUID())) {
                return Status.FAILURE;
            }
        }
        return super.checkStatus(owner);
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            POSITIONS.put(owner.getUUID(), owner.position());
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            POSITIONS.remove(owner.getUUID());
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return JJKAbilities.hasToggled(owner, JJKAbilities.SIMPLE_DOMAIN.get()) && super.isValid(owner);
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.SIMPLE_DOMAIN.get();
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.quickDrawCost.get();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(4.0F, 4.0F);
    }

    @Override
    public boolean isTechnique() {
        return false;
    }
}
