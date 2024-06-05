package radon.jujutsu_kaisen.block.domain;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.IBarrier;

import java.util.UUID;


public class DomainBlock extends Block implements EntityBlock {
    public DomainBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        VoxelShape shape = super.getCollisionShape(pState, pLevel, pPos, pContext);

        if (pContext instanceof EntityCollisionContext ctx) {
            Entity entity = ctx.getEntity();

            if (entity == null) return shape;

            IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return shape;

            ISorcererData data = cap.getSorcererData();

            if (data.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY) && !pContext.isAbove(Shapes.block(), pPos, true)) {
                return Shapes.empty();
            }
        }
        return shape;
    }

    @Override
    public float getExplosionResistance(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, Explosion explosion) {
        Entity direct = explosion.getDirectSourceEntity();
        LivingEntity indirect = explosion.getIndirectSourceEntity();

        Entity exploder = indirect == null ? direct : indirect;

        if (exploder != null && level instanceof ServerLevel && level.getBlockEntity(pos) instanceof DomainBlockEntity be) {
            UUID identifier = be.getIdentifier();

            if (identifier != null && ((ServerLevel) level).getEntity(identifier) instanceof DomainExpansionEntity domain
                    && domain.isInsideBarrier(exploder.blockPosition())) return 3600000.8F;
        }

        float resistance = super.getExplosionResistance(state, level, pos, explosion);

        if (!(level instanceof ServerLevel serverLevel)) return resistance;

        if (!(level.getBlockEntity(pos) instanceof DomainBlockEntity be)) return resistance;

        UUID identifier = be.getIdentifier();

        if (identifier == null) return resistance;

        if (!(serverLevel.getEntity(identifier) instanceof IBarrier barrier)) return resistance;

        return resistance * barrier.getStrength();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return JJKBlockEntities.DOMAIN.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : JJKBlocks.createTickerHelper(pBlockEntityType, JJKBlockEntities.DOMAIN.get(), DomainBlockEntity::tick);
    }
}
