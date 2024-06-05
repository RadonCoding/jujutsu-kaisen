package radon.jujutsu_kaisen.ability;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;

public interface IDomainAttack {
    default void performEntity(LivingEntity owner, LivingEntity target, DomainExpansionEntity domain, boolean instant) {}
    default void performBlock(LivingEntity owner, DomainExpansionEntity domain, BlockPos pos, boolean instant) {}
}