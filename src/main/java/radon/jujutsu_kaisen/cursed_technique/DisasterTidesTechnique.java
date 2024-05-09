package radon.jujutsu_kaisen.cursed_technique;

import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;

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
