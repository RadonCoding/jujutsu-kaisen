package radon.jujutsu_kaisen.cursed_technique;

import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.LinkedHashSet;
import java.util.Set;

public class BoogieWoogieTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.SWAP_SELF.get());
        ABILITIES.add(JJKAbilities.SWAP_OTHERS.get());
        ABILITIES.add(JJKAbilities.FEINT.get());
        ABILITIES.add(JJKAbilities.CE_THROW.get());
        ABILITIES.add(JJKAbilities.ITEM_SWAP.get());
        ABILITIES.add(JJKAbilities.SHUFFLE.get());
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}
