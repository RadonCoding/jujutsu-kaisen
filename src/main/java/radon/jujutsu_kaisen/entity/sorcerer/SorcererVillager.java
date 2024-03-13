package radon.jujutsu_kaisen.entity.sorcerer;

import it.unimi.dsi.fastutil.bytes.ByteHash;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.entity.ai.goal.*;
import radon.jujutsu_kaisen.entity.base.ISorcerer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SorcererVillager extends Villager implements ISorcerer {
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

    public void addMajor(Skill skill) {
        this.majors.add(skill);
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
    public void onAddedToWorld() {
        super.onAddedToWorld();

        IJujutsuCapability cap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        ISkillData skillData = cap.getSkillData();

        this.init(sorcererData, skillData);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("grade", this.grade.ordinal());

        ListTag majorsTag = new ListTag();

        for (Skill major : this.majors) {
            majorsTag.add(IntTag.valueOf(major.ordinal()));
        }
        pCompound.put("majors", majorsTag);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.grade = SorcererGrade.values()[pCompound.getInt("grade")];

        for (Tag tag : pCompound.getList("majors", Tag.TAG_INT)) {
            this.majors.add(Skill.values()[((IntTag) tag).getAsInt()]);
        }
    }

    @Override
    protected void registerGoals() {
        int target = 1;
        int goal = 1;

        this.goalSelector.addGoal(goal++, new WaterWalkingFloatGoal(this));
        this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(goal++, new SorcererGoal(this));
        this.goalSelector.addGoal(goal++, new ChantGoal<>(this));
        this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(target++, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(target, new NearestAttackableCurseGoal(this, true));
    }

    @Override
    public boolean hasMeleeAttack() {
        return true;
    }

    @Override
    public boolean hasArms() {
        return true;
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public boolean canChant() {
        return true;
    }

    @Override
    public float getExperience() {
        return this.grade.getRequiredExperience();
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return null;
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }
}
