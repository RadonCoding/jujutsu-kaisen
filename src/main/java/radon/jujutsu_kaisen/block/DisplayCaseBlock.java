package radon.jujutsu_kaisen.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.entity.DisplayCaseBlockEntity;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.item.base.CursedObjectItem;

import java.util.stream.Stream;

public class DisplayCaseBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Stream.of(Shapes.join(Block.box(6.875, 14.024999999999988, 8.0, 9.125, 15.524999999999988, 8.0), Shapes.join(Block.box(6.5, 13.424999999999994, 6.5, 9.5, 14.17499999999999, 9.5), Shapes.join(Block.box(6.5625, 12.587499999999991, 6.5625, 9.4375, 13.212499999999988, 9.4375), Block.box(8.0, 14.024999999999988, 6.875, 8.0, 15.524999999999988, 9.125), BooleanOp.AND), BooleanOp.AND), BooleanOp.AND), Block.box(1.875, 1.2249999999999992, 1.875, 14.125, 13.47499999999999, 14.125), Block.box(2.0, 4.163336342344337E-17, 2.0, 14.0, 1.5, 14.0)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public DisplayCaseBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (pHand == InteractionHand.MAIN_HAND && pPlayer instanceof ServerPlayer player) {
            if (pLevel.getBlockEntity(pPos) instanceof DisplayCaseBlockEntity be) {
                ItemStack stack = player.getItemInHand(pHand);

                if (stack.getItem() instanceof CursedObjectItem) {
                    if (be.getItem().isEmpty()) {
                        ItemStack copy = stack.copy();

                        be.setItem(copy);

                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                        return InteractionResult.CONSUME;
                    }
                } else if (!be.getItem().isEmpty()) {
                    if (!player.getAbilities().instabuild && player.addItem(be.getItem())) {
                        be.setItem(ItemStack.EMPTY);
                    }
                }
            }
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new DisplayCaseBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return JJKBlocks.createTickerHelper(pBlockEntityType, JJKBlockEntities.DISPLAY_CASE.get(), DisplayCaseBlockEntity::tick);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }
}
