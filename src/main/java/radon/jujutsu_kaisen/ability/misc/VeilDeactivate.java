package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.entity.VeilEntity;

public class VeilDeactivate extends Ability {
    private static final double RANGE = 16.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        BlockPos.betweenClosedStream(AABB.ofSize(owner.position(), RANGE, RANGE, RANGE)).forEach(pos -> {
            if (!(owner.level().getBlockEntity(pos) instanceof VeilRodBlockEntity be)) return;
            if (be.getOwnerUUID() == null || !be.getOwnerUUID().equals(owner.getUUID())) return;

            be.setActive(false);
        });

        for (VeilEntity veil : owner.level().getEntitiesOfClass(VeilEntity.class, AABB.ofSize(owner.position(), RANGE, RANGE, RANGE))) {
            if (veil.getOwner() != owner) continue;

            veil.discard();
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.DOMAIN;
    }
}
