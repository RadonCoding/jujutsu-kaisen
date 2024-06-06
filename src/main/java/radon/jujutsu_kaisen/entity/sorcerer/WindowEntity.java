package radon.jujutsu_kaisen.entity.sorcerer;


import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WindowEntity extends SorcererEntity {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(WindowEntity.class, EntityDataSerializers.INT);

    private static final int MAX_VARIANTS = 4;
    private static final int TECHNIQUE_CHANCE = 10;

    private final Set<Skill> majors;

    private SorcererGrade grade;

    public WindowEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.majors = new HashSet<>();

        this.grade = SorcererGrade.values()[this.random.nextInt(SorcererGrade.GRADE_4.ordinal(), SorcererGrade.GRADE_2.ordinal() + 1)];

        this.setVariant(this.random.nextInt(1, MAX_VARIANTS + 1));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_VARIANT, 1);
    }

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, variant);
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

        pCompound.putInt("variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        for (Tag tag : pCompound.getList("majors", Tag.TAG_INT)) {
            this.majors.add(Skill.values()[((IntTag) tag).getAsInt()]);
        }
        this.setVariant(pCompound.getInt("variant"));
    }

    @Override
    public float getExperience() {
        return this.grade.getRequiredExperience();
    }

    @Override
    @Nullable
    public CursedTechnique getTechnique() {
        if (this.random.nextInt(TECHNIQUE_CHANCE) == 0) {
            List<CursedTechnique> techniques = ConfigHolder.SERVER.getUnlockableTechniques();
            return techniques.get(this.random.nextInt(techniques.size()));
        }
        return null;
    }

    @Override
    @Nullable
    public CursedEnergyNature getNature() {
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
