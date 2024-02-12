package radon.jujutsu_kaisen.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.LinkedHashSet;
import java.util.Set;

public class TenShadowsTechnique implements ICursedTechnique {
    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.CHIMERA_SHADOW_GARDEN.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(Set.of(JJKAbilities.SWITCH_MODE.get(),
                JJKAbilities.RELEASE_SHIKIGAMI.get(),
                JJKAbilities.SHADOW_STORAGE.get(),
                JJKAbilities.SHADOW_TRAVEL.get(),
                JJKAbilities.NUE_LIGHTNING.get(),
                JJKAbilities.PIERCING_WATER.get(),
                JJKAbilities.WHEEL.get(),
                JJKAbilities.GREAT_SERPENT_GRAB.get(),

                JJKAbilities.DIVINE_DOGS.get(),
                JJKAbilities.DIVINE_DOG_TOTALITY.get(),
                JJKAbilities.TOAD.get(),
                JJKAbilities.TOAD_FUSION.get(),
                JJKAbilities.GREAT_SERPENT.get(),
                JJKAbilities.NUE.get(),
                JJKAbilities.NUE_TOTALITY.get(),
                JJKAbilities.MAX_ELEPHANT.get(),
                JJKAbilities.RABBIT_ESCAPE.get(),
                JJKAbilities.TRANQUIL_DEER.get(),
                JJKAbilities.PIERCING_BULL.get(),
                JJKAbilities.AGITO.get(),
                JJKAbilities.MAHORAGA.get()));
    }
}
