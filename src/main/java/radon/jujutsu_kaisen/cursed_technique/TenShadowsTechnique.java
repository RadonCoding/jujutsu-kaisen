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

public class TenShadowsTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.ABILITY_MODE.get());
        ABILITIES.add(JJKAbilities.RELEASE_SHIKIGAMI.get());
        ABILITIES.add(JJKAbilities.SHADOW_STORAGE.get());
        ABILITIES.add(JJKAbilities.SHADOW_TRAVEL.get());
        ABILITIES.add(JJKAbilities.NUE_LIGHTNING.get());
        ABILITIES.add(JJKAbilities.PIERCING_WATER.get());
        ABILITIES.add(JJKAbilities.WHEEL.get());
        ABILITIES.add(JJKAbilities.GREAT_SERPENT_GRAB.get());

        ABILITIES.add(JJKAbilities.DIVINE_DOGS.get());
        ABILITIES.add(JJKAbilities.DIVINE_DOG_TOTALITY.get());
        ABILITIES.add(JJKAbilities.TOAD.get());
        ABILITIES.add(JJKAbilities.TOAD_FUSION.get());
        ABILITIES.add(JJKAbilities.GREAT_SERPENT.get());
        ABILITIES.add(JJKAbilities.NUE.get());
        ABILITIES.add(JJKAbilities.NUE_TOTALITY.get());
        ABILITIES.add(JJKAbilities.MAX_ELEPHANT.get());
        ABILITIES.add(JJKAbilities.RABBIT_ESCAPE.get());
        ABILITIES.add(JJKAbilities.TRANQUIL_DEER.get());
        ABILITIES.add(JJKAbilities.PIERCING_BULL.get());
        ABILITIES.add(JJKAbilities.AGITO.get());
        ABILITIES.add(JJKAbilities.MAHORAGA.get());
    }

    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.CHIMERA_SHADOW_GARDEN.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}