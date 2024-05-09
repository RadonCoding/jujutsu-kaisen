package radon.jujutsu_kaisen.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;

public interface IDomainAttack {
    default void performEntity(LivingEntity owner, LivingEntity target, DomainExpansionEntity domain, boolean instant) {}
    default void performBlock(LivingEntity owner, DomainExpansionEntity domain, BlockPos pos, boolean instant) {}
}