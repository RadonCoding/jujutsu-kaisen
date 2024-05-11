package radon.jujutsu_kaisen.block.entity;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AuthenticMutualLoveBlockEntity extends DomainBlockEntity {
    public AuthenticMutualLoveBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.AUTHENTIC_MUTUAL_LOVE.get(), pPos, pBlockState);
    }
}
