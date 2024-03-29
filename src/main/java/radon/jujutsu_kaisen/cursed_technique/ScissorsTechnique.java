package radon.jujutsu_kaisen.cursed_technique;

import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.LinkedHashSet;
import java.util.Set;

public class ScissorsTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.SCISSORS.get());
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}