package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.entity.projectile.RedProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class Red extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        return HelperMethods.RANDOM.nextInt(40) == 0 && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        RedProjectile red = new RedProjectile(owner, this.getOutput(owner), ChantHandler.getOutput(owner, this) >= 1.5F);
        owner.level().addFreshEntity(red);
    }

    @Override
    public List<Ability> getRequirements() {
        return List.of(JJKAbilities.RCT1.get());
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 200.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }
}
