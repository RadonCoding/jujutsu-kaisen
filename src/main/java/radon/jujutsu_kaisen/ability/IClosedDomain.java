package radon.jujutsu_kaisen.ability;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;

import java.util.List;

public interface IClosedDomain {
    List<Block> getBlocks();
}
