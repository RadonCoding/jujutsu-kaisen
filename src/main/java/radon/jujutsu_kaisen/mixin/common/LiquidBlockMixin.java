package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    public void getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> cir) {
        if (pContext instanceof EntityCollisionContext ctx && ctx.getEntity() instanceof LivingEntity owner && ctx.isAbove(LiquidBlock.STABLE_SHAPE, pPos, true) &&
                pLevel.getBlockState(pPos.above()).isAir()) {
            if ((owner instanceof TamableAnimal tamable && tamable.isTame() && pLevel.getBlockEntity(pPos) instanceof DomainBlockEntity be &&
                    pLevel instanceof ServerLevel level && level.getEntity(be.getIdentifier()) instanceof DomainExpansionEntity domain &&
                    domain.getOwner() == tamable.getOwner()) || JJKAbilities.hasToggled(owner, JJKAbilities.WATER_WALKING.get())) {
                cir.setReturnValue(Shapes.block());
            }
        }
    }
}
