package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DurationBlockEntity;

public class ForestPlatform extends Ability implements Ability.IToggled {
    @Override
    public boolean isChantable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return owner.fallDistance > 0.0F;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        BlockState replace = JJKBlocks.FAKE_WOOD.get().defaultBlockState();

        BlockPos pos = owner.blockPosition();

        int i = 4;

        for (BlockPos between : BlockPos.betweenClosed(pos.offset(-i, -1, -i), pos.offset(i, -1, i))) {
            if (between.closerToCenterThan(owner.position(), i)) {
                BlockState state = owner.level().getBlockState(between);

                if (state.isAir() || !state.canOcclude()) {
                    owner.level().setBlockAndUpdate(between, replace);

                    if (owner.level().getBlockEntity(between) instanceof DurationBlockEntity be) {
                        be.create(2, state);
                    }
                } else if (state.is(JJKBlocks.FAKE_WOOD.get())) {
                    if (owner.level().getBlockEntity(between) instanceof DurationBlockEntity be) {
                        be.setDuration(2);
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
    public boolean isTechnique() {
        return true;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }
}
