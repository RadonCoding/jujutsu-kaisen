package radon.jujutsu_kaisen.data.sorcerer;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import java.util.List;
import java.util.Set;

public interface ISorcererData extends INBTSerializable<CompoundTag> {
    void tick();

    boolean isInitialized();

    void setInitialized(boolean initialized);

    int getCursedEnergyColor();

    void setCursedEnergyColor(int color);

    float getMaximumOutput();

    void increaseOutput();

    void decreaseOutput();

    int getAbilityPoints();

    void setAbilityPoints(int points);

    void addAbilityPoints(int points);

    void useAbilityPoints(int count);

    int getSkillPoints();

    void setSkillPoints(int points);

    void addSkillPoints(int points);

    void useSkillPoints(int count);

    boolean isUnlocked(Ability ability);

    Set<Ability> getUnlocked();

    void unlock(Ability ability);

    void unlockAll(Set<Ability> abilities);

    float getOutput();

    float getOutputBoost();

    float getAbilityOutput(Ability ability);

    float getAbilityOutput();

    float getBaseOutput();

    float getExperience();

    void setExperience(float experience);

    boolean addExperience(float amount);

    void addAdditional(CursedTechnique technique);

    void removeAdditional(CursedTechnique technique);

    boolean hasAdditional(CursedTechnique technique);

    Set<CursedTechnique> getAdditional();

    @Nullable CursedTechnique getCurrentAdditional();

    void setCurrentAdditional(@Nullable CursedTechnique technique);

    @Nullable CursedTechnique getTechnique();

    void setTechnique(@Nullable CursedTechnique technique);

    Set<CursedTechnique> getActiveTechniques();

    Set<CursedTechnique> getTechniques();

    boolean hasTechnique(CursedTechnique technique);

    boolean hasActiveTechnique(CursedTechnique technique);

    CursedEnergyNature getNature();

    void setNature(CursedEnergyNature nature);

    boolean hasTrait(Trait trait);

    void addTrait(Trait trait);

    void addTraits(List<Trait> traits);

    void removeTrait(Trait trait);

    Set<Trait> getTraits();

    void setTraits(Set<Trait> traits);

    JujutsuType getType();

    void setType(JujutsuType type);

    void increaseBrainDamage();

    int getBrainDamage();

    void resetBrainDamage();

    int getBurnout();

    void setBurnout(int duration);

    boolean hasBurnout();

    void resetBurnout();

    float getMaxEnergy();

    void setMaxEnergy(float maxEnergy);

    float getEnergy();

    void setEnergy(float energy);

    void addEnergy(float amount);

    void useEnergy(float amount);

    float getExtraEnergy();

    void addExtraEnergy(float amount);

    void resetExtraEnergy();

    void onBlackFlash();

    long getLastBlackFlashTime();

    void resetBlackFlash();

    boolean isInZone();

    void generate(ServerPlayer owner);

    void addSummon(Entity entity);

    void removeSummon(Entity entity);

    List<Entity> getSummons();

    <T extends Entity> @Nullable T getSummonByClass(Class<T> clazz);

    <T extends Entity> List<T> getSummonsByClass(Class<T> clazz);

    <T extends Entity> void unsummonByClass(Class<T> clazz);

    <T extends Entity> void removeSummonByClass(Class<T> clazz);

    <T extends Entity> boolean hasSummonOfClass(Class<T> clazz);

    int getFingers();

    void setFingers(int count);

    void addFingers(int count);
}
