package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SkyBlockEntity extends DomainBlockEntity {
    public SkyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.SKY.get(), pPos, pBlockState);
    }
}
