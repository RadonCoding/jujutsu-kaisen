package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DaySkyBlockEntity extends DomainBlockEntity {
    public DaySkyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DAY_SKY.get(), pPos, pBlockState);
    }
}
