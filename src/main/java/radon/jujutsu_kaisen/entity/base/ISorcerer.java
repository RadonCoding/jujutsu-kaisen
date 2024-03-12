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
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
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

    default void init(ISorcererData sorcererData, ISkillData skillData) {
        sorcererData.setExperience(this.getExperience());
        sorcererData.setTechnique(this.getTechnique());
        sorcererData.setAdditional(this.getAdditional());
        sorcererData.setNature(this.getNature());
        sorcererData.addTraits(this.getTraits());
        sorcererData.setType(this.getJujutsuType());
        sorcererData.unlockAll(this.getUnlocked());

        if (this.getMaxEnergy() > 0.0F) {
            sorcererData.setMaxEnergy(this.getMaxEnergy());
        }

        if (this.getCursedEnergyColor() != -1) {
            sorcererData.setCursedEnergyColor(this.getCursedEnergyColor());
        }

        // Calculate skill points the NPC would get with their experience
        // Spread them evenly by giving each skill total_points / total_skill_count

        int distributed = sorcererData.getSkillPoints() / Skill.values().length;

        if (distributed > 0) {
            for (Skill skill : Skill.values()) {
                int amount = Math.min(ConfigHolder.SERVER.maximumSkillLevel.get(), distributed);
                skillData.increaseSkill(skill, amount);
                sorcererData.useSkillPoints(amount);
            }
        }
        sorcererData.setEnergy(sorcererData.getMaxEnergy());
    }
}
