package radon.jujutsu_kaisen.block.domain;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;

import java.util.function.Supplier;

public class ChimeraShadowGardenBlock extends LiquidBlock implements EntityBlock {
    public ChimeraShadowGardenBlock(FlowingFluid pFluid, Properties pProperties) {
        super(pFluid, pProperties);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        if (pContext instanceof EntityCollisionContext ctx) {
            if (!(ctx.getEntity() instanceof LivingEntity entity))
                return super.getCollisionShape(pState, pLevel, pPos, pContext);
            if (!(pLevel.getBlockEntity(pPos) instanceof DomainBlockEntity be))
                return super.getCollisionShape(pState, pLevel, pPos, pContext);
            if (!(pLevel instanceof ServerLevel level && level.getEntity(be.getIdentifier()) instanceof DomainExpansionEntity domain))
                return super.getCollisionShape(pState, pLevel, pPos, pContext);
            if (entity instanceof TamableAnimal tamable && tamable.isTame() && tamable.getOwner() == domain.getOwner())
                return Shapes.block();
            if (domain.getOwner() == entity) return super.getCollisionShape(pState, pLevel, pPos, pContext);
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2, 0, false, false, false));
        }
        return super.getCollisionShape(pState, pLevel, pPos, pContext);
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
