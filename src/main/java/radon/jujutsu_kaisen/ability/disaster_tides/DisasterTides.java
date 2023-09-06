package radon.jujutsu_kaisen.ability.disaster_tides;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.RemovableBlockEntity;

public class DisasterTides extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        Vec3 look = owner.getLookAngle();
        BlockState block = JJKBlocks.REMOVABLE_WATER.get().defaultBlockState();

        double startX = owner.getX() + look.x * 2;
        double startY = owner.getY() + look.y * 2;
        double startZ = owner.getZ() + look.z * 2;

        int waveLength = 32;
        int waveWidth = 16;

        for (int i = 0; i < waveLength; i++) {
            for (int j = -waveWidth / 2; j < waveWidth / 2; j++) {
                double posX = startX + look.x * i;
                double posY = startY + j;
                double posZ = startZ + look.z * i;

                BlockPos pos = BlockPos.containing(posX, posY, posZ);
                BlockState state = owner.level.getBlockState(pos);

                if (!state.isAir() || state.canOcclude()) continue;

                BlockState original = null;

                if (owner.level.getBlockEntity(pos) instanceof RemovableBlockEntity be) {
                    original = be.getOriginal();
                }
                owner.level.setBlockAndUpdate(pos, block);

                if (owner.level.getBlockEntity(pos) instanceof RemovableBlockEntity be) {
                    be.create(5, original == null ? state : original);
                }
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }
}
