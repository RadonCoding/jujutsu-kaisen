package radon.jujutsu_kaisen.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.Set;

public class ProjectionSorceryTechnique implements ICursedTechnique {
    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.TIME_CELL_MOON_PALACE.get();
    }

    @Override
    public Ability getImbuement() {
        return JJKAbilities.PROJECTION_SORCERY_IMBUEMENT.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return Set.of(JJKAbilities.PROJECTION_SORCERY.get(),
                JJKAbilities.TWENTY_FOUR_FRAME_RULE.get(),
                JJKAbilities.AIR_FRAME.get());
    }
}
