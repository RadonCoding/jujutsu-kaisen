package radon.jujutsu_kaisen.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DomainFloorBlockEntity extends BlockEntity {
    public DomainFloorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DOMAIN_FLOOR.get(), pPos, pBlockState);
    }
}