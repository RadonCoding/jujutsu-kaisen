package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChantGoal<T extends PathfinderMob & ISorcerer> extends Goal {
    private final T mob;

    public ChantGoal(T pMob) {
        this.mob = pMob;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private static Set<String> getRandomChantCombo(int count) {
        List<? extends String> chants = ConfigHolder.SERVER.chants.get();

        Set<String> combo = new HashSet<>();

        while (combo.size() < Math.min(chants.size(), count)) {
            combo.add(chants.get(HelperMethods.RANDOM.nextInt(chants.size())));
        }
        return combo;
    }

    @Override
    public void tick() {
        if (!this.mob.canChant()) return;

        IJujutsuCapability cap = this.mob.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        for (Ability ability : JJKAbilities.getAbilities(this.mob)) {
            if (!ability.isScalable(this.mob) || data.hasChants(ability)) continue;

            Set<String> chants = getRandomChantCombo(5);

            while (!data.isChantsAvailable(chants)) chants = getRandomChantCombo(5);

            data.addChants(ability, chants);
        }
    }
}
