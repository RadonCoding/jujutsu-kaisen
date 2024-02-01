package radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique.base.ICursedTechnique;

import java.util.Optional;
import java.util.Set;

public class DisasterFlamesTechnique implements ICursedTechnique {
    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.COFFIN_OF_THE_IRON_MOUNTAIN.get();
    }

    @Override
    public Ability getImbuement() {
        return JJKAbilities.DISASTER_FLAMES_IMBUEMENT.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return Set.of(JJKAbilities.EMBER_INSECTS.get(),
                JJKAbilities.EMBER_INSECT_FLIGHT.get(),
                JJKAbilities.VOLCANO.get(),
                JJKAbilities.MAXIMUM_METEOR.get(),
                JJKAbilities.DISASTER_FLAMES.get(),
                JJKAbilities.FLAMETHROWER.get(),
                JJKAbilities.FIREBALL.get(),
                JJKAbilities.FIRE_BEAM.get());
    }
}
