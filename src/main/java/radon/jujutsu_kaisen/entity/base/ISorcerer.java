package radon.jujutsu_kaisen.entity.base;

import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;

import java.util.List;

public interface ISorcerer {
    SorcererGrade getGrade();
    @Nullable CursedTechnique getTechnique();
    List<Trait> getTraits();
    boolean isCurse();
    float getMaxEnergy();

    @Nullable Ability getDomain();

    default void init(ISorcererData data) {
        data.setGrade(this.getGrade());
        data.setTechnique(this.getTechnique());
        data.addTraits(this.getTraits());
        data.setCurse(this.isCurse());

        float maxEnergy = this.getMaxEnergy();

        if (maxEnergy > 0.0F) {
            data.setMaxEnergy(maxEnergy);
        }
        data.setEnergy(data.getMaxEnergy());
    }
}
