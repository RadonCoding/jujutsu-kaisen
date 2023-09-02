package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.effect.MaximumRedBeam;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class MaximumRed extends Ability {
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
        MaximumRedBeam beam = new MaximumRedBeam(owner, (float) ((owner.yHeadRot + 90.0F) * Math.PI / 180.0F), (float) (-owner.getXRot() * Math.PI / 180.0F));
        owner.level.addFreshEntity(beam);
    }

    @Override
    public List<Trait> getRequirements() {
        return List.of(Trait.REVERSE_CURSED_TECHNIQUE);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 200.0F;
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
