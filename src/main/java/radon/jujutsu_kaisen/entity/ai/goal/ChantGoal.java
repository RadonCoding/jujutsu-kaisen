package radon.jujutsu_kaisen.entity.ai.goal;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.chant.IChantData;
import radon.jujutsu_kaisen.entity.ISorcerer;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.LinkedHashSet;
import java.util.List;

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

    private static LinkedHashSet<String> getRandomChantCombo() {
        List<? extends String> chants = ConfigHolder.SERVER.chants.get();

        LinkedHashSet<String> combo = new LinkedHashSet<>();

        while (combo.size() < Math.min(chants.size(), ConfigHolder.SERVER.maximumChantCount.get())) {
            combo.add(chants.get(HelperMethods.RANDOM.nextInt(chants.size())));
        }
        return combo;
    }

    @Override
    public void tick() {
        if (!this.mob.canChant()) return;

        IJujutsuCapability cap = this.mob.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IChantData data = cap.getChantData();

        for (Ability ability : JJKAbilities.getAbilities(this.mob)) {
            if (!ability.isScalable(this.mob) || data.hasChants(ability)) continue;

            LinkedHashSet<String> chants = getRandomChantCombo();

            while (!data.isChantsAvailable(chants)) chants = getRandomChantCombo();

            data.addChants(ability, chants);
        }
    }
}
