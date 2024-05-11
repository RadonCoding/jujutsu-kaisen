package radon.jujutsu_kaisen.ability.misc;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.*;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.entity.VeilEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class VeilDeactivate extends Ability {
    public static final double RANGE = 64.0D;

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

        if (!(RotationUtil.getLookAtHit(owner, RANGE) instanceof BlockHitResult hit)) return;

        BlockPos pos = hit.getBlockPos();

        if (!(owner.level().getBlockEntity(pos) instanceof VeilBlockEntity be)) return;
        if (be.getParentUUID() == null || !(level.getEntity(be.getParentUUID()) instanceof VeilEntity veil)) return;
        if (veil.getOwner() != owner) return;

        veil.discard();
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
