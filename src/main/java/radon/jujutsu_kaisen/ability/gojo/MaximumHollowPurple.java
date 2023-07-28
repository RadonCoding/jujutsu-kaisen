package radon.jujutsu_kaisen.ability.gojo;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.projectile.MaximumHollowPurpleProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MaximumHollowPurple extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(10) == 0 && target != null && target.getMaxHealth() > Player.MAX_HEALTH && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        MaximumHollowPurpleProjectile purple = new MaximumHollowPurpleProjectile(owner);
        owner.level.addFreshEntity(purple);
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return JJKAbilities.hasTrait(owner, Trait.REVERSE_CURSED_TECHNIQUE);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1000.0F;
    }

    @Override
    public int getCooldown() {
        return 60 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
