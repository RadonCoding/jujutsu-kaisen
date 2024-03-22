package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;

public class VeilActivate extends Ability {
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
        if (!(owner.level() instanceof ServerLevel level)) return;

        BlockPos.betweenClosedStream(AABB.ofSize(owner.position(), 8.0D, 8.0D, 8.0D)).forEach(pos -> {
            if (!(owner.level().getBlockEntity(pos) instanceof VeilRodBlockEntity be)) return;
            if (be.getOwnerUUID() == null || !be.getOwnerUUID().equals(owner.getUUID())) return;

            be.setActive(true);
        });

        for (ServerPlayer player : level.players()) {
            player.sendSystemMessage(Component.translatable(String.format("chat.%s.veil", JujutsuKaisen.MOD_ID), owner.getName().getString()));
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
