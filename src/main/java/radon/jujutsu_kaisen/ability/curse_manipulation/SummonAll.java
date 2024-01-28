package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;

import java.util.List;

public class SummonAll extends Ability {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return target != null && !target.isDeadOrDying() && owner.hasLineOfSight(target) &&
                (cap.getType() == JujutsuType.CURSE || cap.isUnlocked(JJKAbilities.RCT1.get()) ? owner.getHealth() / owner.getMaxHealth() < 0.9F : owner.getHealth() / owner.getMaxHealth() < 0.4F);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        List<AbsorbedCurse> curses = cap.getCurses();

        for (AbsorbedCurse curse : curses) {
            JJKAbilities.summonCurse(owner, curse, false);
        }
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.getCurses().isEmpty()) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        List<AbsorbedCurse> curses = cap.getCurses();

        float cost = 0.0F;

        for (AbsorbedCurse curse : curses) {
            cost += JJKAbilities.getCurseCost(curse);
        }
        return cost;
    }

    @Override
    public float getRealCost(LivingEntity owner) {
        return this.getCost(owner);
    }
}
