package radon.jujutsu_kaisen.entity.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.*;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ISorcerer {
    boolean hasMeleeAttack();

    boolean hasArms();

    boolean canJump();

    boolean canChant();

    float getExperience();

    default float getMaxEnergy() {
        return 0.0F;
    }

    default int getCursedEnergyColor() {
        return -1;
    }

    default SorcererGrade getGrade() {
        Entity entity = (Entity) this;

        if (!entity.isAddedToWorld()) return SorcererUtil.getGrade(this.getExperience());

        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return SorcererGrade.GRADE_4;

        ISorcererData data = cap.getSorcererData();

        if (data == null) return SorcererGrade.GRADE_4;

        return SorcererUtil.getGrade(data.getExperience());
    }

    @Nullable ICursedTechnique getTechnique();

    default @Nullable ICursedTechnique getAdditional() {
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

    default Set<Ability> getUnlocked() {
        return Set.of();
    }

    JujutsuType getJujutsuType();

    static Set<String> getRandomChantCombo(int count) {
        List<? extends String> chants = ConfigHolder.SERVER.chants.get();

        Set<String> combo = new HashSet<>();

        while (combo.size() < Math.min(chants.size(), count)) {
            combo.add(chants.get(HelperMethods.RANDOM.nextInt(chants.size())));
        }
        return combo;
    }

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

                Set<String> chants = getRandomChantCombo(5);

                while (!data.isChantsAvailable(chants)) chants = getRandomChantCombo(5);

                data.addChants(ability, chants);
            }
        }
    }
}
