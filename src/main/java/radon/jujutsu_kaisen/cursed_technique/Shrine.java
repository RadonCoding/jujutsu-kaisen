package radon.jujutsu_kaisen.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;

import java.util.LinkedHashSet;
import java.util.Set;

public class Shrine implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.DISMANTLE.get());
        ABILITIES.add(JJKAbilities.BIG_DISMANTLE.get());
        ABILITIES.add(JJKAbilities.DISMANTLE_NET.get());
        ABILITIES.add(JJKAbilities.DISMANTLE_SKATING.get());
        ABILITIES.add(JJKAbilities.CLEAVE.get());
        ABILITIES.add(JJKAbilities.SPIDERWEB.get());
        ABILITIES.add(JJKAbilities.FURNACE_OPEN.get());
    }

    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.MALEVOLENT_SHRINE.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}
