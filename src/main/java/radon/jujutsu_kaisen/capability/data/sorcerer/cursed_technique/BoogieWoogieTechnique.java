package radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique.base.ICursedTechnique;

import java.util.Optional;
import java.util.Set;

public class BoogieWoogieTechnique implements ICursedTechnique {
    @Override
    public Ability getImbuement() {
        return JJKAbilities.BOOGIE_WOOGIE_IMBUEMENT.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return Set.of(JJKAbilities.SWAP_SELF.get(),
                JJKAbilities.SWAP_OTHERS.get(),
                JJKAbilities.FEINT.get(),
                JJKAbilities.CE_THROW.get(),
                JJKAbilities.ITEM_SWAP.get(),
                JJKAbilities.SHUFFLE.get());
    }
}
