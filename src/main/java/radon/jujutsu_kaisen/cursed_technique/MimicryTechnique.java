package radon.jujutsu_kaisen.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.LinkedHashSet;
import java.util.Set;

public class MimicryTechnique implements ICursedTechnique {
    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.ALL_ENCOMPASSING_UNEQUIVOCAL_LOVE.get();
    }

    @Override
    public Ability getImbuement() {
        return JJKAbilities.MIMICRY_IMBUEMENT.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(Set.of(JJKAbilities.RIKA.get(),
                JJKAbilities.MIMICRY.get(),
                JJKAbilities.REFILL.get(),
                JJKAbilities.COMMAND_PURE_LOVE.get()));
    }
}
