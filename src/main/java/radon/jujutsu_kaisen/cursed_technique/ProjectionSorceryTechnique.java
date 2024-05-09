package radon.jujutsu_kaisen.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProjectionSorceryTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.PROJECTION_SORCERY.get());
        ABILITIES.add(JJKAbilities.TWENTY_FOUR_FRAME_RULE.get());
        ABILITIES.add(JJKAbilities.AIR_FRAME.get());
    }

    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.TIME_CELL_MOON_PALACE.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}
