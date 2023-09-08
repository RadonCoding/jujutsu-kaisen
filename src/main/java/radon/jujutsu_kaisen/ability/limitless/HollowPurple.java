package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.projectile.HollowPurpleProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class HollowPurple extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(10) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        HollowPurpleProjectile purple = new HollowPurpleProjectile(owner);
        owner.level.addFreshEntity(purple);
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        if (JJKAbilities.MAXIMUM_HOLLOW_PURPLE.get().getStatus(owner, false, false, false, false) == Status.COOLDOWN) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public List<Trait> getRequirements() {
        return List.of(Trait.REVERSE_CURSED_TECHNIQUE);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 500.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public Classification getClassification() {
        return Classification.LIMITLESS;
    }
}
