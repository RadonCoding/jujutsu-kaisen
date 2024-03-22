package radon.jujutsu_kaisen.ability.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public interface IDomainAttack {
    default void performEntity(LivingEntity owner, LivingEntity target, DomainExpansionEntity domain) {}
    default void performBlock(LivingEntity owner, DomainExpansionEntity domain, BlockPos pos) {}
}