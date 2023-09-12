package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.util.HelperMethods;

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
            this.mob.setXRot(HelperMethods.getXRotD(this.mob, target.getEyePosition()));
            this.mob.xRotO = this.mob.getXRot();

            this.mob.setYRot(HelperMethods.getYRotD(this.mob, target.getEyePosition()));
            this.mob.yRotO = this.mob.getYRot();

            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yHeadRotO = this.mob.yHeadRot;

            this.mob.yBodyRot = this.mob.getYRot();
            this.mob.yBodyRotO = this.mob.yBodyRot;
        }
    }
}
