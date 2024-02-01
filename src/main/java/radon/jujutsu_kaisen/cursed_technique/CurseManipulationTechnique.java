package radon.jujutsu_kaisen.cursed_technique;

import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.Set;

public class CurseManipulationTechnique implements ICursedTechnique {
    @Override
    public Ability getImbuement() {
        return JJKAbilities.CURSE_MANIPULATION_IMBUEMENT.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return Set.of(JJKAbilities.CURSE_ABSORPTION.get(),
                JJKAbilities.RELEASE_CURSE.get(),
                JJKAbilities.RELEASE_CURSES.get(),
                JJKAbilities.SUMMON_ALL.get(),
                JJKAbilities.ENHANCE_CURSE.get(),
                JJKAbilities.MAXIMUM_UZUMAKI.get(),
                JJKAbilities.MINI_UZUMAKI.get(),
                JJKAbilities.WORM_CURSE_GRAB.get());
    }
}
