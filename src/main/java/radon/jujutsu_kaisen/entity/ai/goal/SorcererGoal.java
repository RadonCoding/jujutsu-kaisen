package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;

import java.util.List;

public class SorcererGoal extends Goal {
    private final PathfinderMob mob;
    private long lastCanUseCheck;

    public SorcererGoal(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public void tick() {
        List<Ability> abilities = JJKAbilities.getAbilities(this.mob);

        for (Ability ability : abilities) {
            boolean success = ability.shouldTrigger(this.mob, this.mob.getTarget());

            if (ability.getActivationType(this.mob) == Ability.ActivationType.TOGGLED) {
                if (success) {
                    if (!JJKAbilities.hasToggled(this.mob, ability)) {
                        AbilityHandler.trigger(this.mob, ability);
                    }
                } else if (JJKAbilities.hasToggled(this.mob, ability)) {
                    AbilityHandler.trigger(this.mob, ability);
                }
            } else if (ability.getActivationType(this.mob) == Ability.ActivationType.CHANNELED) {
                if (success) {
                    if (!JJKAbilities.isChanneling(this.mob, ability)) {
                        AbilityHandler.trigger(this.mob, ability);
                    }
                } else if (JJKAbilities.isChanneling(this.mob, ability)) {
                    AbilityHandler.trigger(this.mob, ability);
                }
            } else if (success) {
                AbilityHandler.trigger(this.mob, ability);
            }
        }
    }

    @Override
    public boolean canUse() {
        long i = this.mob.level().getGameTime();

        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
            return true;
        }
    }
}
