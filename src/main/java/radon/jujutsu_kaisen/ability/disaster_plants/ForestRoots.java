package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.effect.ForestRootsEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class ForestRoots extends Ability {
    public static final double RANGE = 15.0D;



    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(20) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        for (LivingEntity entity : owner.level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(owner.position(), RANGE, RANGE, RANGE), entity -> entity != owner)) {
            owner.level().addFreshEntity(new ForestRootsEntity(owner, this.getOutput(owner), entity));
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }


}
