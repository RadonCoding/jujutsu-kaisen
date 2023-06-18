package radon.jujutsu_kaisen.capability;

import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CursedTechnique {
    GETO(300.0F),
    GOJO(450.0F, JJKAbilities.INFINITY.get(), JJKAbilities.RED.get(),
            JJKAbilities.BLUE.get(), JJKAbilities.HOLLOW_PURPLE.get(),
            JJKAbilities.UNLIMITED_VOID.get()),
    SUKUNA(450.0F),
    TOGE(100.0F),
    YUJI(300.0F),
    YUTA(500.0F);

    final float maxEnergy;
    final List<Ability> abilities;

    CursedTechnique(float maxEnergy, Ability... abilities) {
        this.maxEnergy = maxEnergy;

        this.abilities = new ArrayList<>(Arrays.asList(abilities));
        this.abilities.add(JJKAbilities.SMASH.get());
    }

    public float getMaxEnergy() {
        return this.maxEnergy;
    }

    public List<Ability> getAbilities() {
        return this.abilities;
    }
}
