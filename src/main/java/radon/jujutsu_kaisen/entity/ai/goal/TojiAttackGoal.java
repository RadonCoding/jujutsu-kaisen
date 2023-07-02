package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.Dash;
import radon.jujutsu_kaisen.ability.JJKAbilities;

public class TojiAttackGoal extends Goal {
    private final PathfinderMob mob;
    private long lastCanUseCheck;

    public TojiAttackGoal(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();

        if (target != null) {
            double distance = this.mob.distanceTo(target);

            if (distance > 1.0D && distance <= Dash.RANGE) {
                AbilityHandler.trigger(this.mob, JJKAbilities.DASH.get());
            }
        }
    }

    @Override
    public boolean canUse() {
        long i = this.mob.level.getGameTime();

        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
            LivingEntity target = this.mob.getTarget();

            if (target == null) {
                return false;
            }
            return target.isAlive();
        }
    }
}
