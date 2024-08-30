package radon.jujutsu_kaisen.ability;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.domain.DomainExpansionEntity;

public interface IDomainAttack {
    default void performEntity(LivingEntity owner, LivingEntity target, DomainExpansionEntity domain, boolean instant) {
    }

    default void performBlock(Level level, LivingEntity owner, DomainExpansionEntity domain, BlockPos pos, boolean instant) {
    }
}