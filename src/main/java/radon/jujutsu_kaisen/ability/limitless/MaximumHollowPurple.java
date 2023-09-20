package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.projectile.MaximumHollowPurpleProjectile;

import java.util.List;

public class MaximumHollowPurple extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getType() == JujutsuType.CURSE || cap.hasTrait(Trait.REVERSE_CURSED_TECHNIQUE) ? owner.getHealth() / owner.getMaxHealth() < 0.8F :
                owner.getHealth() / owner.getMaxHealth() < 0.7F || target.getHealth() > owner.getHealth() * 2;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        MaximumHollowPurpleProjectile purple = new MaximumHollowPurpleProjectile(owner);
        owner.level.addFreshEntity(purple);
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        if (JJKAbilities.HOLLOW_PURPLE.get().getStatus(owner, false, false, false, false) == Status.COOLDOWN) {
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
