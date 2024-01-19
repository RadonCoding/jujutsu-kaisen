package radon.jujutsu_kaisen.entity.base;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.*;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;
import java.util.Set;

public interface ISorcerer {
    boolean canChant();

    float getExperience();

    default float getMaxEnergy() {
        return 0.0F;
    }

    default int getCursedEnergyColor() {
        return -1;
    }

    SorcererGrade getGrade();

    @Nullable CursedTechnique getTechnique();

    default @Nullable CursedTechnique getAdditional() {
        return null;
    }

    default @NotNull List<Trait> getTraits() {
        return List.of();
    }

    default @Nullable CursedEnergyNature getNature() {
        return CursedEnergyNature.BASIC;
    }

    default @NotNull List<Ability> getCustom() {
        return List.of();
    }

    default List<Ability> getUnlocked() {
        return List.of();
    }

    JujutsuType getJujutsuType();

    default void init(ISorcererData data) {
        data.setExperience(this.getExperience());
        data.setTechnique(this.getTechnique());
        data.setAdditional(this.getAdditional());
        data.setNature(this.getNature());
        data.addTraits(this.getTraits());
        data.setType(this.getJujutsuType());
        data.unlockAll(this.getUnlocked());

        if (this.getMaxEnergy() > 0.0F) {
            data.setMaxEnergy(this.getMaxEnergy());
        }
        data.setEnergy(data.getMaxEnergy());

        if (this.getCursedEnergyColor() != -1) {
            data.setCursedEnergyColor(this.getCursedEnergyColor());
        }

        if (this.canChant()) {
            for (Ability ability : JJKAbilities.getAbilities((LivingEntity) this)) {
                if (!ability.isTechnique() || !ability.isScalable((LivingEntity) this)) continue;

                Set<String> chants = HelperMethods.getRandomWordCombo(5);

                while (!data.isChantsAvailable(chants)) chants = HelperMethods.getRandomWordCombo(5);

                data.addChants(ability, chants);
            }
        }
    }
}
