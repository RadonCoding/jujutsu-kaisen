package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;

public class HealingGoal extends Goal {
    private final PathfinderMob mob;
    private long lastCanUseCheck;

    public HealingGoal(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public void tick() {
        this.mob.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            Ability ability = cap.getType() == JujutsuType.CURSE ? JJKAbilities.HEAL.get() : JJKAbilities.RCT.get();
            boolean success = ability.shouldTrigger(this.mob, this.mob.getTarget());

            if (success) {
                if (!cap.isChanneling(ability)) {
                    AbilityHandler.trigger(this.mob, ability);
                }
            } else if (cap.isChanneling(ability)) {
                AbilityHandler.trigger(this.mob, ability);
            }
        });
    }

    @Override
    public boolean canUse() {
        long i = this.mob.level.getGameTime();

        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
            return true;
        }
    }
}
