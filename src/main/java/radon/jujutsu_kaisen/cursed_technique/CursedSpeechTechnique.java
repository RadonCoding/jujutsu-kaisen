package radon.jujutsu_kaisen.cursed_technique;

import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;

import java.util.LinkedHashSet;
import java.util.Set;

public class CursedSpeechTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.DONT_MOVE.get());
        ABILITIES.add(JJKAbilities.GET_CRUSHED.get());
        ABILITIES.add(JJKAbilities.BLAST_AWAY.get());
        ABILITIES.add(JJKAbilities.EXPLODE.get());
        ABILITIES.add(JJKAbilities.DIE.get());
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}
