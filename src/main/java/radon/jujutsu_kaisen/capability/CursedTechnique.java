package radon.jujutsu_kaisen.capability;

import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JujutsuAbilities;

import java.lang.reflect.Array;

public enum CursedTechnique {
    GETO(1500.0F),
    GOJO(2250.0F, JujutsuAbilities.INFINITY.get(), JujutsuAbilities.RED.get()),
    SUKUNA(2250.0F),
    TOGE(1000.0F),
    YUJI(1500.0F),
    YUTA(2500.0F);

    final float maxEnergy;
    final Ability[] abilities;

    CursedTechnique(float maxEnergy, Ability... abilities) {
        this.maxEnergy = maxEnergy;
        this.abilities = abilities;
    }

    public float getMaxEnergy() {
        return this.maxEnergy;
    }

    public Ability[] getAbilities() {
        return this.abilities;
    }
}
