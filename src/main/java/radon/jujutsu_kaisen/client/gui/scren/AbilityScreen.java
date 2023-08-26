package radon.jujutsu_kaisen.client.gui.scren;

import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.gui.scren.base.RadialScreen;

import java.util.List;

public class AbilityScreen extends RadialScreen {
    @Override
    protected List<Ability> getAbilities() {
        assert this.minecraft != null;

        List<Ability> abilities =  JJKAbilities.getAbilities(this.minecraft.player);
        abilities.removeIf(ability -> ability.getDisplayType() != DisplayType.RADIAL);
        return abilities;
    }
}