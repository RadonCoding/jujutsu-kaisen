package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.gojo.Teleport;

public class GojoAttackGoal extends Goal {
    private final PathfinderMob mob;
    private long lastCanUseCheck;

    public GojoAttackGoal(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public void tick() {
        if (!JJKAbilities.hasToggledAbility(this.mob, JJKAbilities.INFINITY.get())) {
            AbilityHandler.trigger(this.mob, JJKAbilities.INFINITY.get());
        }
        if (!JJKAbilities.hasToggledAbility(this.mob, JJKAbilities.RCT.get())) {
            AbilityHandler.trigger(this.mob, JJKAbilities.RCT.get());
        }

        LivingEntity target = this.mob.getTarget();

        if (target != null) {
            double distance = this.mob.distanceTo(target);

            if (this.mob.getHealth() / this.mob.getMaxHealth() <= 0.5F) {
                AbilityHandler.trigger(this.mob, JJKAbilities.UNLIMITED_VOID.get());
            }
            if (this.mob.getRandom().nextInt(5) == 0 && distance <= 5.0D) {
                AbilityHandler.trigger(this.mob, JJKAbilities.SMASH.get());
            }
            if (this.mob.getRandom().nextInt(5) == 0 && distance <= 10.0D) {
                AbilityHandler.trigger(this.mob, JJKAbilities.RED.get());
            }
            if (this.mob.getRandom().nextInt(5) == 0 && distance <= 5.0D) {
                AbilityHandler.trigger(this.mob, JJKAbilities.BLUE.get());
            }
            if (this.mob.getRandom().nextInt(10) == 0 && distance <= 50.0D) {
                AbilityHandler.trigger(this.mob, JJKAbilities.HOLLOW_PURPLE.get());
            }
            if (distance > 10.0D && distance <= Teleport.RANGE) {
                AbilityHandler.trigger(this.mob, JJKAbilities.TELEPORT.get());
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
