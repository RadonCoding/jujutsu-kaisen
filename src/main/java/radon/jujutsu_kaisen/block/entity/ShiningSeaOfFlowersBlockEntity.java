package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ShiningSeaOfFlowersBlockEntity extends DomainBlockEntity {
    public ShiningSeaOfFlowersBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.SHINING_SEA_OF_FLOWERS.get(), pPos, pBlockState);
    }
}
