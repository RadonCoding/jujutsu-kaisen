package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DurationBlockEntity;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.util.HelperMethods;

public class ForestPlatform extends Ability implements Ability.IToggled {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (data.hasToggled(this)) {
            return owner.getFeetBlockState().getCollisionShape(owner.level(), owner.blockPosition()).isEmpty() && HelperMethods.RANDOM.nextInt(5) != 0;
        }
        return owner.fallDistance > 2.0F && !owner.isInFluidType();
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        BlockState replace = JJKBlocks.FAKE_WOOD.get().defaultBlockState();

        BlockPos center = owner.blockPosition().below(owner.isShiftKeyDown() ? 2 : 1);

        int i = 4;

        for (int x = -i; x <= i; x++) {
            for (int y = -i; y <= 0; y++) {
                for (int z = -i; z <= i; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance <= i) {
                        BlockPos pos = center.offset(x, y, z);
                        BlockState state = owner.level().getBlockState(pos);

                        if (state.isAir() || !state.canOcclude()) {
                            owner.level().setBlockAndUpdate(pos, replace);

                            if (owner.level().getBlockEntity(pos) instanceof DurationBlockEntity be) {
                                be.create(2, state);
                            }
                        } else if (state.is(JJKBlocks.FAKE_WOOD.get())) {
                            if (owner.level().getBlockEntity(pos) instanceof DurationBlockEntity be) {
                                be.setDuration(2);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }



    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }
}
