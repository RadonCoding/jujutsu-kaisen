package radon.jujutsu_kaisen.cursed_technique;

import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.LinkedHashSet;
import java.util.Set;

public class CurseManipulationTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.CURSE_ABSORPTION.get());
        ABILITIES.add(JJKAbilities.RELEASE_CURSE.get());
        ABILITIES.add(JJKAbilities.RELEASE_CURSES.get());
        ABILITIES.add(JJKAbilities.SUMMON_ALL.get());
        ABILITIES.add(JJKAbilities.ENHANCE_CURSE.get());
        ABILITIES.add(JJKAbilities.MAXIMUM_UZUMAKI.get());
        ABILITIES.add(JJKAbilities.MINI_UZUMAKI.get());
        ABILITIES.add(JJKAbilities.WORM_CURSE_GRAB.get());
        ABILITIES.add(JJKAbilities.FISH_SWARM.get());
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}
