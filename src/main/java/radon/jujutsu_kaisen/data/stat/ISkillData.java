package radon.jujutsu_kaisen.data.stat;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Map;

public interface ISkillData extends INBTSerializable<CompoundTag> {
    int getSkill(Skill skill);

    void setSkill(Skill skill, int level);

    void increaseSkill(Skill skill, int amount);

    void resetSkills();
}
