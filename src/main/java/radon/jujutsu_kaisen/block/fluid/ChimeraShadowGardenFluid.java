package radon.jujutsu_kaisen.block.fluid;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import org.jetbrains.annotations.NotNull;

public abstract class ChimeraShadowGardenFluid extends BaseFlowingFluid {
    protected ChimeraShadowGardenFluid(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canSpreadTo(@NotNull BlockGetter pLevel, @NotNull BlockPos pFromPos, @NotNull BlockState pFromBlockState,
                                  @NotNull Direction pDirection, @NotNull BlockPos pToPos, @NotNull BlockState pToBlockState,
                                  @NotNull FluidState pToFluidState, @NotNull Fluid pFluid) {
        return false;
    }

    @Override
    protected void animateTick(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull FluidState pState, RandomSource pRandom) {
        if (pRandom.nextInt(10) == 0) {
            double d0 = (double) pPos.getX() + pRandom.nextDouble();
            double d1 = (double) pPos.getY() + 1.0D;
            double d2 = (double) pPos.getZ() + pRandom.nextDouble();
            pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
        if (pRandom.nextInt(10) == 0) {
            double d0 = (double) pPos.getX() + pRandom.nextDouble();
            double d1 = (double) pPos.getY() + 1.0D;
            double d2 = (double) pPos.getZ() + pRandom.nextDouble();
            pLevel.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    public static class Flowing extends ChimeraShadowGardenFluid {
        public Flowing(Properties properties) {
            super(properties);

            this.registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);

            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(@NotNull FluidState state) {
            return false;
        }
    }

    public static class Source extends ChimeraShadowGardenFluid {
        public Source(Properties properties) {
            super(properties);
        }

        public int getAmount(@NotNull FluidState state) {
            return 8;
        }

        public boolean isSource(@NotNull FluidState state) {
            return true;
        }
    }
}
