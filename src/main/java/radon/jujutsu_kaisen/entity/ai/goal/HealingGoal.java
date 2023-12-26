package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.util.HelperMethods;

public class HealingGoal extends Goal {
    private final PathfinderMob mob;
    private long lastCanUseCheck;

    public HealingGoal(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public void tick() {
        this.mob.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            Ability ability = null;

            Ability rct = HelperMethods.getRCTTier(this.mob);

            if (cap.getType() == JujutsuType.CURSE) {
                ability = JJKAbilities.HEAL.get();
            } else if (rct != null) {
                ability = rct;
            }

            if (ability == null) return;

            boolean success = ability.shouldTrigger(this.mob, this.mob.getTarget());

            if (success) {
                if (!cap.isChanneling(ability)) {
                    AbilityHandler.trigger(this.mob, ability);
                }
            } else if (cap.isChanneling(ability)) {
                AbilityHandler.untrigger(this.mob, ability);
            }
        });
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
