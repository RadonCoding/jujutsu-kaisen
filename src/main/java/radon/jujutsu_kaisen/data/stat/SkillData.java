package radon.jujutsu_kaisen.data.stat;


import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SkillData implements ISkillData {
    private final Map<Skill, Integer> skills;

    public SkillData() {
        this.skills = new HashMap<>();
    }

    @Override
    public int getSkill(Skill skill) {
        return this.skills.getOrDefault(skill, 0);
    }

    @Override
    public void setSkill(Skill skill, int level) {
        this.skills.put(skill, level);
    }

    @Override
    public void increaseSkill(Skill skill, int amount) {
        this.skills.put(skill, this.skills.getOrDefault(skill, 0) + amount);
    }

    @Override
    public void resetSkills() {
        this.skills.clear();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();

        ListTag statsTag = new ListTag();

        for (Map.Entry<Skill, Integer> entry : this.skills.entrySet()) {
            CompoundTag data = new CompoundTag();
            data.putInt("skill", entry.getKey().ordinal());
            data.putInt("level", entry.getValue());
            statsTag.add(data);
        }
        nbt.put("skills", statsTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        this.skills.clear();

        for (Tag tag : nbt.getList("skills", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) tag;
            this.skills.put(Skill.values()[data.getInt("skill")],
                    data.getInt("level"));
        }
    }
}
