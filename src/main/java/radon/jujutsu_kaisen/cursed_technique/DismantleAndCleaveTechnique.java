package radon.jujutsu_kaisen.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.Set;

public class DismantleAndCleaveTechnique implements ICursedTechnique {
    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.MALEVOLENT_SHRINE.get();
    }

    @Override
    public Ability getImbuement() {
        return JJKAbilities.DISMANTLE_AND_CLEAVE_IMBUEMENT.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return Set.of(JJKAbilities.DISMANTLE.get(),
                JJKAbilities.DISMANTLE_NET.get(),
                JJKAbilities.DISMANTLE_SKATING.get(),
                JJKAbilities.CLEAVE.get(),
                JJKAbilities.SPIDERWEB.get(),
                JJKAbilities.FIRE_ARROW.get());
    }
}
