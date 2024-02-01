package radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.JJKEntities;

import java.util.Optional;
import java.util.Set;

public class MimicryTechnique implements ICursedTechnique {
    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.GENUINE_MUTUAL_LOVE.get();
    }

    @Override
    public Ability getImbuement() {
        return JJKAbilities.MIMICRY_IMBUEMENT.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return Set.of(JJKAbilities.RIKA.get(),
                JJKAbilities.MIMICRY.get(),
                JJKAbilities.COMMAND_PURE_LOVE.get());
    }
}
