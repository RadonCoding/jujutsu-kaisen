package radon.jujutsu_kaisen.cursed_technique;

import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.LinkedHashSet;
import java.util.Set;

public class BoogieWoogieTechnique implements ICursedTechnique {
    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(Set.of(JJKAbilities.SWAP_SELF.get(),
                JJKAbilities.SWAP_OTHERS.get(),
                JJKAbilities.FEINT.get(),
                JJKAbilities.CE_THROW.get(),
                JJKAbilities.ITEM_SWAP.get(),
                JJKAbilities.SHUFFLE.get()));
    }
}
