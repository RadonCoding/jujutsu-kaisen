package radon.jujutsu_kaisen.cursed_technique;

import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.Set;

public class ShockwaveTechnique implements ICursedTechnique {
    @Override
    public Ability getImbuement() {
        return JJKAbilities.SHOCKWAVE_IMBUEMENT.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return Set.of(JJKAbilities.SHOCKWAVE.get());
    }
}
