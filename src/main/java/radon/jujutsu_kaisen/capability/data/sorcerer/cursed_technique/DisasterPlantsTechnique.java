package radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique.base.ICursedTechnique;

import java.util.Set;

public class DisasterPlantsTechnique implements ICursedTechnique {
    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.SHINING_SEA_OF_FLOWERS.get();
    }

    @Override
    public Ability getImbuement() {
        return JJKAbilities.DISASTER_PLANTS_IMBUEMENT.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return Set.of(JJKAbilities.FOREST_PLATFORM.get(),
                JJKAbilities.FOREST_SPIKES.get(),
                JJKAbilities.WOOD_SHIELD.get(),
                JJKAbilities.CURSED_BUD.get(),
                JJKAbilities.FOREST_WAVE.get(),
                JJKAbilities.FOREST_ROOTS.get(),
                JJKAbilities.FOREST_DASH.get(),
                JJKAbilities.DISASTER_PLANT.get());
    }
}
