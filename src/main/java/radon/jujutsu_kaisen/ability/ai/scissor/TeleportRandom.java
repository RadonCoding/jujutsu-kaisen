package radon.jujutsu_kaisen.ability.ai.scissor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.util.HelperMethods;

public class TeleportRandom extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private static void teleport(LivingEntity owner, double pX, double pY, double pZ) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(pX, pY, pZ);

        while (pos.getY() > owner.level.getMinBuildHeight() && !owner.level.getBlockState(pos).getMaterial().blocksMotion()) {
            pos.move(Direction.DOWN);
        }
        BlockState blockstate = owner.level.getBlockState(pos);
        boolean flag = blockstate.getMaterial().blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);

        if (flag && !flag1) {
            Vec3 current = owner.position();
            boolean success = owner.randomTeleport(pX, pY, pZ, true);

            if (success) {
                owner.level.gameEvent(GameEvent.TELEPORT, current, GameEvent.Context.of(owner));

                if (!owner.isSilent()) {
                    owner.level.playSound(null, owner.xo, owner.yo, owner.zo, SoundEvents.ENDERMAN_TELEPORT, owner.getSoundSource(), 1.0F, 1.0F);
                    owner.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public void run(LivingEntity owner) {
        if (!owner.level.isClientSide) {
            double d0 = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 64.0D;
            double d1 = owner.getY() + (double)(HelperMethods.RANDOM.nextInt(64) - 32);
            double d2 = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 64.0D;
            teleport(owner, d0, d1, d2);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }
}
