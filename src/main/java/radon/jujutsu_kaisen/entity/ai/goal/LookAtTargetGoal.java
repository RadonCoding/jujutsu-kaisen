package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LookAtTargetGoal extends Goal {
    private final Mob mob;

    public LookAtTargetGoal(Mob mob) {
        this.mob = mob;

        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();

        if (target != null) {
            this.mob.getLookControl().setLookAt(target, (float) this.mob.getMaxHeadYRot(), (float) this.mob.getMaxHeadXRot());
        }
    }
}
