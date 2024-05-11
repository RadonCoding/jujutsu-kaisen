package radon.jujutsu_kaisen.block.entity;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SelfEmbodimentOfPerfectionBlockEntity extends DomainBlockEntity {
    public SelfEmbodimentOfPerfectionBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.SELF_EMBODIMENT_OF_PERFECTION.get(), pPos, pBlockState);
    }
}
