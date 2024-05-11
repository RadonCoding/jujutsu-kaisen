package radon.jujutsu_kaisen.block.entity;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class UnlimitedVoidBlockEntity extends DomainBlockEntity {
    public UnlimitedVoidBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.UNLIMITED_VOID.get(), pPos, pBlockState);
    }
}
