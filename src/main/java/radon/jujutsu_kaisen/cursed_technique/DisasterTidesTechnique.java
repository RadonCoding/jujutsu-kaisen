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

public class DisasterTidesTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.DISASTER_TIDES.get());
        ABILITIES.add(JJKAbilities.WATER_SHIELD.get());
        ABILITIES.add(JJKAbilities.DEATH_SWARM.get());
        ABILITIES.add(JJKAbilities.FISH_SHIKIGAMI.get());
        ABILITIES.add(JJKAbilities.WATER_TORRENT.get());
        ABILITIES.add(JJKAbilities.EEL_GRAPPLE.get());
    }

    @Override
    public Ability getDomain() {
        return JJKAbilities.HORIZON_OF_THE_CAPTIVATING_SKANDHA.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}
