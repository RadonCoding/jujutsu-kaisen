package radon.jujutsu_kaisen.block;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.entity.DurationBlockEntity;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;

import java.util.function.Supplier;

public class FakeWaterDurationBlock extends LiquidBlock implements EntityBlock {
    public FakeWaterDurationBlock(FlowingFluid pFluid, Properties pProperties) {
        super(pFluid, pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return JJKBlockEntities.DURATION.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : JJKBlocks.createTickerHelper(pBlockEntityType, JJKBlockEntities.DURATION.get(), DurationBlockEntity::tick);
    }
}
