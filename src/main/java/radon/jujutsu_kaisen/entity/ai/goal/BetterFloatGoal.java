package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.ability.JJKAbilities;

import java.util.EnumSet;

public class BetterFloatGoal extends Goal {
    private final Mob mob;

    public BetterFloatGoal(Mob pMob) {
        this.mob = pMob;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP));
        pMob.getNavigation().setCanFloat(true);
    }

    @Override
    public boolean canUse() {
        if (JJKAbilities.hasToggled(this.mob, JJKAbilities.WATER_WALKING.get())) return false;
        return this.mob.isInWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getFluidJumpThreshold() || this.mob.isInLava() || this.mob.isInFluidType((fluidType, height) -> this.mob.canSwimInFluidType(fluidType) && height > this.mob.getFluidJumpThreshold());
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.mob.getRandom().nextFloat() < 0.8F) {
            this.mob.getJumpControl().jump();
        }
    }
}
