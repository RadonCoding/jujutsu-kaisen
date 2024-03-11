package radon.jujutsu_kaisen.data.stat;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Map;

public interface ISkillData extends INBTSerializable<CompoundTag> {
    int getSkill(Skill skill);

    void setSkill(Skill skill, int level);

    void increaseSkill(Skill skill, int amount);

    void resetSkills();
}
