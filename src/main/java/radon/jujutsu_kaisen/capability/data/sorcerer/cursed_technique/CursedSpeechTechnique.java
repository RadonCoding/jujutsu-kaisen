package radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique;

import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique.base.ICursedTechnique;

import java.util.Optional;
import java.util.Set;

public class CursedSpeechTechnique implements ICursedTechnique {
    @Override
    public Ability getImbuement() {
        return JJKAbilities.CURSED_SPEECH_IMBUEMENT.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return Set.of(JJKAbilities.DONT_MOVE.get(),
                JJKAbilities.GET_CRUSHED.get(),
                JJKAbilities.BLAST_AWAY.get(),
                JJKAbilities.EXPLODE.get(),
                JJKAbilities.DIE.get());
    }
}
