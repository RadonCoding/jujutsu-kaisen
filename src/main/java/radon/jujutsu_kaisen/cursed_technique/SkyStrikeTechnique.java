package radon.jujutsu_kaisen.cursed_technique;

import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.LinkedHashSet;
import java.util.Set;

public class SkyStrikeTechnique implements ICursedTechnique {
    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(Set.of(JJKAbilities.SKY_STRIKE.get()));
    }
}
