package radon.jujutsu_kaisen.capability;

import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JujutsuAbilities;

public enum CursedTechnique {
    GETO(300.0F),
    GOJO(450.0F, JujutsuAbilities.INFINITY.get(), JujutsuAbilities.RED.get(),
            JujutsuAbilities.BLUE.get(), JujutsuAbilities.HOLLOW_PURPLE.get(),
            JujutsuAbilities.UNLIMITED_VOID.get()),
    SUKUNA(450.0F),
    TOGE(100.0F),
    YUJI(300.0F),
    YUTA(500.0F);

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
