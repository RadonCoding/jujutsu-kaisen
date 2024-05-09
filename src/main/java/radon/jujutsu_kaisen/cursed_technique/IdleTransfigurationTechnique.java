package radon.jujutsu_kaisen.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.Ability;

import java.util.LinkedHashSet;
import java.util.Set;

public class IdleTransfigurationTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.IDLE_TRANSFIGURATION.get());
        ABILITIES.add(JJKAbilities.SOUL_DECIMATION.get());
        ABILITIES.add(JJKAbilities.SOUL_REINFORCEMENT.get());
        ABILITIES.add(JJKAbilities.SOUL_RESTORATION.get());
        ABILITIES.add(JJKAbilities.BODY_REPEL.get());
        ABILITIES.add(JJKAbilities.FEROCIOUS_BODY_REPEL.get());
        ABILITIES.add(JJKAbilities.ARM_BLADE.get());
        ABILITIES.add(JJKAbilities.GUN.get());
        ABILITIES.add(JJKAbilities.HORSE_LEGS.get());
        ABILITIES.add(JJKAbilities.WINGS.get());
        ABILITIES.add(JJKAbilities.TRANSFIGURED_SOUL_SMALL.get());
        ABILITIES.add(JJKAbilities.TRANSFIGURED_SOUL_NORMAL.get());
        ABILITIES.add(JJKAbilities.TRANSFIGURED_SOUL_LARGE.get());
        ABILITIES.add(JJKAbilities.POLYMORPHIC_SOUL_ISOMER.get());
        ABILITIES.add(JJKAbilities.INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING.get());
    }

    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.SELF_EMBODIMENT_OF_PERFECTION.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}
