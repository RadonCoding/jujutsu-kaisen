package radon.jujutsu_kaisen.block.domain;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;


public class DomainFloorBlock extends Block implements EntityBlock {
    public DomainFloorBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return JJKBlockEntities.DOMAIN_FLOOR.get().create(pPos, pState);
    }
}
