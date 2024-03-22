package radon.jujutsu_kaisen.entity.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.*;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.util.SorcererUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Interface to make creating sorcerer NPCs easier
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

    default Set<Skill> getMajors() {
        return Set.of();
    }

    default void init() {
        IJujutsuCapability cap = ((Entity) this).getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        ISkillData skillData = cap.getSkillData();

        if (sorcererData.isInitialized()) return;

        sorcererData.setExperience(this.getExperience());

        int abilityPoints = Math.round(sorcererData.getExperience() / ConfigHolder.SERVER.abilityPointInterval.get().floatValue());
        int skillPoints = Math.round(sorcererData.getExperience() / ConfigHolder.SERVER.skillPointInterval.get().floatValue());

        sorcererData.setAbilityPoints(abilityPoints);
        sorcererData.setSkillPoints(skillPoints);

        sorcererData.setTechnique(this.getTechnique());
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

        Set<Skill> majors = this.getMajors();

        int points = sorcererData.getSkillPoints();

        // Calculate skill points the NPC would get with their experience
        // Spread them evenly by giving each skill total_points / total_skill_count

        if (majors.isEmpty()) {
            List<Skill> skills = new ArrayList<>();

            for (Skill skill : Skill.values()) {
                if (!skill.isValid((LivingEntity) this)) continue;

                skills.add(skill);
            }
            int distributed = points / skills.size();

            if (distributed > 0) {
                for (Skill skill : skills) {
                    int current = skillData.getSkill(skill);

                    int max = SorcererUtil.getMaximumSkillLevel(sorcererData.getExperience(), current, distributed);

                    int real = max - current;

                    if (real == 0) continue;

                    skillData.increaseSkill(skill, real);
                    sorcererData.useSkillPoints(real);
                }
            }
        } else {
            List<Skill> skills = new ArrayList<>();

            for (Skill skill : majors) {
                if (!skill.isValid((LivingEntity) this)) continue;

                skills.add(skill);
            }

            int distributed = points / skills.size();

            if (distributed > 0) {
                for (Skill major : skills) {
                    int current = skillData.getSkill(major);

                    int max = SorcererUtil.getMaximumSkillLevel(sorcererData.getExperience(), current, distributed);

                    int real = max - current;

                    if (real == 0) continue;

                    skillData.increaseSkill(major, real);
                    sorcererData.useSkillPoints(real);
                }
            }
        }
        sorcererData.setEnergy(sorcererData.getMaxEnergy());

        sorcererData.setInitialized(true);
    }
}
