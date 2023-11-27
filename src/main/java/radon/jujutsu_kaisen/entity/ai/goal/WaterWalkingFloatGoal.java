package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;

import java.util.EnumSet;

public class WaterWalkingFloatGoal extends Goal {
    private final PathfinderMob mob;

    public WaterWalkingFloatGoal(PathfinderMob pMob) {
        this.mob = pMob;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP));
        pMob.getNavigation().setCanFloat(true);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        Ability ability = JJKAbilities.WATER_WALKING.get();

        boolean success = ability.shouldTrigger(this.mob, this.mob.getTarget());

        if (this.mob.isInWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getFluidJumpThreshold() || this.mob.isInLava() || this.mob.isInFluidType((fluidType, height) -> this.mob.canSwimInFluidType(fluidType) && height > this.mob.getFluidJumpThreshold())) {
            if (this.mob.getRandom().nextFloat() < 0.8F) {
                this.mob.getJumpControl().jump();
            }
        } else if (success) {
            if (!JJKAbilities.hasToggled(this.mob, ability)) {
                AbilityHandler.trigger(this.mob, ability);
            }
        } else if (JJKAbilities.hasToggled(this.mob, ability)) {
            AbilityHandler.trigger(this.mob, ability);
        }
    }
}
