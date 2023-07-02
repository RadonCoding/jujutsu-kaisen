package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.sukuna.Cleave;

public class SukunaAttackGoal extends Goal {
    private final PathfinderMob mob;
    private long lastCanUseCheck;

    public SukunaAttackGoal(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();

        if (target != null) {
            double distance = this.mob.distanceTo(target);

            if (JJKAbilities.hasToggledAbility(target, JJKAbilities.INFINITY.get()) || this.mob.getHealth() / this.mob.getMaxHealth() <= 0.75F) {
                AbilityHandler.trigger(this.mob, JJKAbilities.MALEVOLENT_SHRINE.get());
            }
            else if (this.mob.getRandom().nextInt(5) == 0 && distance <= 5.0D) {
                AbilityHandler.trigger(this.mob, JJKAbilities.SMASH.get());
            }
            else if (this.mob.getRandom().nextInt(5) == 0 && distance <= 10.0D) {
                AbilityHandler.trigger(this.mob, JJKAbilities.DISMANTLE.get());
            }
            else if (this.mob.getRandom().nextInt(5) == 0 && distance < Cleave.RANGE) {
                AbilityHandler.trigger(this.mob, JJKAbilities.CLEAVE.get());
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
