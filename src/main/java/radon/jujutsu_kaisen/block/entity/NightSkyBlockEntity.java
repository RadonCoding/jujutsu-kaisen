package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class NightSkyBlockEntity extends DomainBlockEntity {
    public NightSkyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.NIGHT_SKY.get(), pPos, pBlockState);
    }
}
