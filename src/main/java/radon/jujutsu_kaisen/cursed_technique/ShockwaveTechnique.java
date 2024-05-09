package radon.jujutsu_kaisen.cursed_technique;

import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;

import java.util.LinkedHashSet;
import java.util.Set;

public class ShockwaveTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.SHOCKWAVE.get());
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}