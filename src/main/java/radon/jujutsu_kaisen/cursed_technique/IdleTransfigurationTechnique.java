package radon.jujutsu_kaisen.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.LinkedHashSet;
import java.util.Set;

public class IdleTransfigurationTechnique implements ICursedTechnique {
    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.SELF_EMBODIMENT_OF_PERFECTION.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(Set.of(JJKAbilities.IDLE_TRANSFIGURATION.get(),
                JJKAbilities.SOUL_DECIMATION.get(),
                JJKAbilities.SOUL_REINFORCEMENT.get(),
                JJKAbilities.SOUL_RESTORATION.get(),
                JJKAbilities.ARM_BLADE.get(),
                JJKAbilities.GUN.get(),
                JJKAbilities.HORSE_LEGS.get(),
                JJKAbilities.WINGS.get(),
                JJKAbilities.TRANSFIGURED_SOUL_SMALL.get(),
                JJKAbilities.TRANSFIGURED_SOUL_NORMAL.get(),
                JJKAbilities.TRANSFIGURED_SOUL_LARGE.get(),
                JJKAbilities.POLYMORPHIC_SOUL_ISOMER.get(),
                JJKAbilities.INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING.get()));
    }
}
