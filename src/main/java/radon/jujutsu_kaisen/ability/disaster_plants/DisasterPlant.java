package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.entity.effect.DisasterPlantEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class DisasterPlant extends Ability {
    public static final double RANGE = 32.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && this.getTarget(owner) == target;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (RotationUtil.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            return target;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        LivingEntity target = this.getTarget(owner);

        if (target == null) return;

        DisasterPlantEntity plant = new DisasterPlantEntity(owner, this.getPower(owner), target);
        owner.level().addFreshEntity(plant);
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            LivingEntity target = this.getTarget(owner);

            if (target == null) {
                return Status.FAILURE;
            }
        }
        return super.isTriggerable(owner);
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 300.0F;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Override
    public Classification getClassification() {
        return Classification.PLANTS;
    }
}
