package radon.jujutsu_kaisen.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;

import java.util.LinkedHashSet;
import java.util.Set;

public class MimicryTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.RIKA.get());
        ABILITIES.add(JJKAbilities.MIMICRY.get());
        ABILITIES.add(JJKAbilities.REFILL.get());
        ABILITIES.add(JJKAbilities.COMMAND_PURE_LOVE.get());
    }

    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.AUTHENTIC_MUTUAL_LOVE.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}
