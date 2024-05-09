package radon.jujutsu_kaisen.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.entity.IBarrier;

import java.util.UUID;

public class VeilBlock extends Block implements EntityBlock {
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
    public static final BooleanProperty TRANSPARENT = BooleanProperty.create("transparent");

    public VeilBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(COLOR, DyeColor.BLACK)
                .setValue(TRANSPARENT, false));
    }

    @Override
    public float getExplosionResistance(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Explosion explosion) {
        float resistance = super.getExplosionResistance(state, level, pos, explosion);

        if (!(level instanceof ServerLevel serverLevel)) return resistance;

        if (!(level.getBlockEntity(pos) instanceof VeilBlockEntity veil)) return resistance;

        UUID identifier = veil.getParentUUID();

        if (identifier == null) return resistance;

        if (!(serverLevel.getEntity(identifier) instanceof IBarrier barrier)) return resistance;

        return resistance * barrier.getStrength();
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos) {
        return true;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return pState.getValue(TRANSPARENT) ? RenderShape.INVISIBLE : super.getRenderShape(pState);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        VoxelShape shape = super.getCollisionShape(pState, pLevel, pPos, pContext);

        if (pContext instanceof EntityCollisionContext ctx) {
            if (!(pLevel.getBlockEntity(pPos) instanceof VeilBlockEntity veil)) return shape;

            Entity entity = ctx.getEntity();

            if (entity == null) return shape;

            if (entity instanceof Projectile projectile) {
                Entity owner = projectile.getOwner();

                if (owner != null) {
                    entity = projectile.getOwner();
                }
            }
            return veil.isAllowed(entity) && !pContext.isAbove(Shapes.block(), pPos, true) ? Shapes.empty() : Shapes.block();
        }
        return shape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return JJKBlockEntities.VEIL.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : JJKBlocks.createTickerHelper(pBlockEntityType, JJKBlockEntities.VEIL.get(), VeilBlockEntity::tick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(COLOR, TRANSPARENT);
    }
}
