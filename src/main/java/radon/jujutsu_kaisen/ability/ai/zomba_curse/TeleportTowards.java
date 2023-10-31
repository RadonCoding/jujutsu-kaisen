package radon.jujutsu_kaisen.ability.ai.zomba_curse;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.util.HelperMethods;

public class TeleportTowards extends Ability {
    @Override
    public boolean isChantable() {
        return false;
    }

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

        while (pos.getY() > owner.level().getMinBuildHeight() && !owner.level().getBlockState(pos).blocksMotion()) {
            pos.move(Direction.DOWN);
        }
        BlockState blockstate = owner.level().getBlockState(pos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);

        if (flag && !flag1) {
            Vec3 current = owner.position();
            boolean success = owner.randomTeleport(pX, pY, pZ, true);

            if (success) {
                owner.level().gameEvent(GameEvent.TELEPORT, current, GameEvent.Context.of(owner));

                if (!owner.isSilent()) {
                    owner.level().playSound(null, owner.xo, owner.yo, owner.zo, SoundEvents.ENDERMAN_TELEPORT, owner.getSoundSource(), 1.0F, 1.0F);
                    owner.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public void run(LivingEntity owner) {
        LivingEntity target;

        if (!(owner instanceof Mob mob) || (target = mob.getTarget()) == null) return;

        Vec3 pos = new Vec3(owner.getX() - target.getX(), owner.getY(0.5D) - target.getEyeY(), owner.getZ() - target.getZ()).normalize();
        double d0 = 16.0D;
        double d1 = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 8.0D - pos.x * d0;
        double d2 = owner.getY() + (double) (HelperMethods.RANDOM.nextInt(16) - 8) - pos.y * d0;
        double d3 = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 8.0D - pos.z * d0;
        teleport(owner, d1, d2, d3);
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
