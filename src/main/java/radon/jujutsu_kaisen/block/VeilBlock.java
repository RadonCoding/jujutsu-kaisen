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
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;

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

        BlockPos parent = veil.getParent();

        if (parent == null) return resistance;

        if (!(level.getBlockEntity(parent) instanceof VeilRodBlockEntity rod)) return resistance;

        if (rod.ownerUUID == null) return resistance;

        Entity owner = serverLevel.getEntity(rod.ownerUUID);

        if (owner == null) return resistance;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return resistance;

        ISkillData data = cap.getSkillData();

        return resistance + data.getSkill(Skill.BARRIER);
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
            if (!(pLevel.getBlockEntity(pPos) instanceof VeilBlockEntity block)) return shape;

            Entity entity = ctx.getEntity();

            if (entity == null) return shape;

            if (entity instanceof Projectile projectile) {
                Entity owner = projectile.getOwner();

                if (owner != null) {
                    entity = projectile.getOwner();
                }
            }

            BlockPos parent = block.getParent();

            if (parent == null || !(pLevel.getBlockEntity(parent) instanceof VeilRodBlockEntity rod)) return shape;

            return rod.isAllowed(entity) && !pContext.isAbove(Shapes.block(), pPos, true) ? Shapes.empty() : Shapes.block();
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
