package radon.jujutsu_kaisen.entity.sorcerer;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.bytes.ByteHash;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.entity.ai.goal.*;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SorcererVillager extends SorcererEntity {
    private static final int TECHNIQUE_CHANCE = 10;

    private final Set<Skill> majors;

    private SorcererGrade grade;

    public SorcererVillager(EntityType<? extends Villager> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.majors = new HashSet<>();

        this.grade = SorcererGrade.values()[this.random.nextInt(SorcererGrade.GRADE_4.ordinal(), SorcererGrade.GRADE_2.ordinal() + 1)];
    }

    public void setGrade(SorcererGrade grade) {
        this.grade = grade;
    }

    public void addMajors(Skill... skills) {
        this.majors.addAll(List.of(skills));
    }

    @Nullable
    @Override
    public Set<Skill> getMajors() {
        return this.majors;
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.init();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        ListTag majorsTag = new ListTag();

        for (Skill major : this.majors) {
            majorsTag.add(IntTag.valueOf(major.ordinal()));
        }
        pCompound.put("majors", majorsTag);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        for (Tag tag : pCompound.getList("majors", Tag.TAG_INT)) {
            this.majors.add(Skill.values()[((IntTag) tag).getAsInt()]);
        }
    }

    @Override
    public float getExperience() {
        return this.grade.getRequiredExperience();
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        if (this.random.nextInt(TECHNIQUE_CHANCE) == 0) {
            List<ICursedTechnique> techniques = ConfigHolder.SERVER.getUnlockableTechniques();
            return techniques.get(this.random.nextInt(techniques.size()));
        }
        return null;
    }

    @Override
    public @Nullable CursedEnergyNature getNature() {
        if (this.random.nextInt(ConfigHolder.SERVER.cursedEnergyNatureRarity.get()) == 0) {
            return HelperMethods.randomEnum(CursedEnergyNature.class, Set.of(CursedEnergyNature.BASIC));
        }
        return super.getNature();
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }
}
