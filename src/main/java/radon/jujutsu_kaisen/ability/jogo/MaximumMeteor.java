package radon.jujutsu_kaisen.ability.jogo;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.entity.MeteorEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MaximumMeteor extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(10) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        MeteorEntity meteor = new MeteorEntity(owner);
        owner.level.addFreshEntity(meteor);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1000.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
