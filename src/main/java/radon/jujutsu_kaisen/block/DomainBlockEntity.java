package radon.jujutsu_kaisen.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DomainBlockEntity extends BlockEntity {
    public DomainBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JujutsuBlockEntities.DOMAIN_BLOCK_ENTITY.get(), pPos, pBlockState);
        System.out.println(this.level.getBlockState(pPos));
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DomainBlockEntity pEntity) {
    }
}
