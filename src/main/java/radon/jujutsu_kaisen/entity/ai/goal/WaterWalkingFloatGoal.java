package radon.jujutsu_kaisen.entity.ai.goal;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;

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
        IJujutsuCapability cap = this.mob.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        Ability ability = JJKAbilities.CURSED_ENERGY_FLOW.get();

        boolean success = ability.shouldTrigger(this.mob, this.mob.getTarget());

        if (this.mob.isInWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getFluidJumpThreshold() || this.mob.isInLava() || this.mob.isInFluidType((fluidType, height) -> this.mob.canSwimInFluidType(fluidType) && height > this.mob.getFluidJumpThreshold())) {
            if (this.mob.getRandom().nextFloat() < 0.8F) {
                this.mob.getJumpControl().jump();
            }
        } else if (success) {
            if (!data.hasToggled(ability)) {
                AbilityHandler.trigger(this.mob, ability);
            }
        } else if (data.hasToggled(ability)) {
            AbilityHandler.trigger(this.mob, ability);
        }
    }
}
