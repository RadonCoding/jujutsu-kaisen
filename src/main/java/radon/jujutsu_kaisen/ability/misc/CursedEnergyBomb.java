package radon.jujutsu_kaisen.ability.misc;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.*;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.entity.effect.CursedEnergyBombEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class CursedEnergyBomb extends Ability {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.getTechnique() == null && HelperMethods.RANDOM.nextInt(5) == 0 && owner.hasLineOfSight(target) && owner.distanceTo(target) <= CursedEnergyBombEntity.RANGE;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        CursedEnergyBombEntity bomb = new CursedEnergyBombEntity(owner, this.getOutput(owner));
        owner.level().addFreshEntity(bomb);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.getType() == JujutsuType.CURSE && data.getExtraEnergy() > 0.0F && super.isValid(owner);
    }
}
