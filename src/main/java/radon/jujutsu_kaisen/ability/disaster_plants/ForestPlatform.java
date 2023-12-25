package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.block.entity.DurationBlockEntity;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.tags.JJKBlockTags;

import java.util.List;

public class ForestPlatform extends Ability implements Ability.IToggled {
    @Override
    public boolean isScalable(LivingEntity owner) {
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

        BlockPos center = owner.blockPosition().below(owner.isShiftKeyDown() ? -1 : 1);

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
