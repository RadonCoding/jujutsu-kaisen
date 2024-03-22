package radon.jujutsu_kaisen.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.LinkedHashSet;
import java.util.Set;

public class DisasterFlamesTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.EMBER_INSECTS.get());
        ABILITIES.add(JJKAbilities.EMBER_INSECT_FLIGHT.get());
        ABILITIES.add(JJKAbilities.VOLCANO.get());
        ABILITIES.add(JJKAbilities.MAXIMUM_METEOR.get());
        ABILITIES.add(JJKAbilities.DISASTER_FLAMES.get());
        ABILITIES.add(JJKAbilities.FLAMETHROWER.get());
        ABILITIES.add(JJKAbilities.FIREBALL.get());
        ABILITIES.add(JJKAbilities.FIRE_BEAM.get());
    }

    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.COFFIN_OF_THE_IRON_MOUNTAIN.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}
