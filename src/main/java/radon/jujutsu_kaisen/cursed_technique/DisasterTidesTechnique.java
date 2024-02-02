package radon.jujutsu_kaisen.cursed_technique;

import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.LinkedHashSet;
import java.util.Set;

public class DisasterTidesTechnique implements ICursedTechnique {
    @Override
    public Ability getDomain() {
        return JJKAbilities.HORIZON_OF_THE_CAPTIVATING_SKANDHA.get();
    }

    @Override
    public Ability getImbuement() {
        return JJKAbilities.DISASTER_TIDES_IMBUEMENT.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(Set.of(JJKAbilities.DISASTER_TIDES.get(),
                JJKAbilities.WATER_SHIELD.get(),
                JJKAbilities.DEATH_SWARM.get(),
                JJKAbilities.FISH_SHIKIGAMI.get(),
                JJKAbilities.WATER_TORRENT.get(),
                JJKAbilities.EEL_GRAPPLE.get()));
    }
}
