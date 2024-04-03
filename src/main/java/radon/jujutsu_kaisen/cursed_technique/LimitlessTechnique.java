package radon.jujutsu_kaisen.cursed_technique;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.LinkedHashSet;
import java.util.Set;

public class LimitlessTechnique implements ICursedTechnique {
    private static final Set<Ability> ABILITIES = new LinkedHashSet<>();

    static {
        ABILITIES.add(JJKAbilities.INFINITY.get());
        ABILITIES.add(JJKAbilities.RED.get());
        ABILITIES.add(JJKAbilities.BLUE_STILL.get());
        ABILITIES.add(JJKAbilities.BLUE_MOTION.get());
        ABILITIES.add(JJKAbilities.BLUE_FISTS.get());
        ABILITIES.add(JJKAbilities.HOLLOW_PURPLE.get());
        ABILITIES.add(JJKAbilities.TELEPORT_SELF.get());
        ABILITIES.add(JJKAbilities.TELEPORT_OTHERS.get());
        ABILITIES.add(JJKAbilities.AZURE_GLIDE.get());
    }

    @Override
    public @Nullable Ability getDomain() {
        return JJKAbilities.UNLIMITED_VOID.get();
    }

    @Override
    public Set<Ability> getAbilities() {
        return new LinkedHashSet<>(ABILITIES);
    }
}
