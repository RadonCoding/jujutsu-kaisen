package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.chant.IChantData;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.util.HelperMethods;

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

    private static Set<String> getRandomChantCombo() {
        List<? extends String> chants = ConfigHolder.SERVER.chants.get();

        Set<String> combo = new HashSet<>();

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

            Set<String> chants = getRandomChantCombo();

            while (!data.isChantsAvailable(chants)) chants = getRandomChantCombo();

            data.addChants(ability, chants);
        }
    }
}
