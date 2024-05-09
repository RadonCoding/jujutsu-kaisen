package radon.jujutsu_kaisen.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;

import java.util.LinkedHashSet;
import java.util.Set;

public class DisasterPlantsTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.FOREST_PLATFORM.get());
        ABILITIES.add(JJKAbilities.FOREST_SPIKES.get());
        ABILITIES.add(JJKAbilities.WOOD_SHIELD.get());
        ABILITIES.add(JJKAbilities.CURSED_BUD.get());
        ABILITIES.add(JJKAbilities.FOREST_WAVE.get());
        ABILITIES.add(JJKAbilities.FOREST_ROOTS.get());
        ABILITIES.add(JJKAbilities.FOREST_DASH.get());
        ABILITIES.add(JJKAbilities.DISASTER_PLANT.get());
    }

    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.SHINING_SEA_OF_FLOWERS.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return ABILITIES;
    }
}