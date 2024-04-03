package radon.jujutsu_kaisen.data.sorcerer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.data.mission.Mission;

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

    float getAbilityOutput(Ability ability);

    float getAbilityOutput();

    float getBaseOutput();

    float getExperience();

    void setExperience(float experience);

    boolean addExperience(float amount);

    float getDomainSize();

    void setDomainSize(float domainSize);

    void addAdditional(ICursedTechnique technique);

    void removeAdditional(ICursedTechnique technique);

    boolean hasAdditional(ICursedTechnique technique);

    Set<ICursedTechnique> getAdditional();

    @Nullable ICursedTechnique getCurrentAdditional();

    void setCurrentAdditional(@Nullable ICursedTechnique technique);

    @Nullable ICursedTechnique getTechnique();

    void setTechnique(@Nullable ICursedTechnique technique);

    Set<ICursedTechnique> getActiveTechniques();

    Set<ICursedTechnique> getTechniques();

    boolean hasTechnique(ICursedTechnique technique);

    boolean hasActiveTechnique(ICursedTechnique technique);

    CursedEnergyNature getNature();

    void setNature(CursedEnergyNature nature);

    boolean hasTrait(Trait trait);

    void addTrait(Trait trait);

    void addTraits(List<Trait> traits);

    void removeTrait(Trait trait);

    Set<Trait> getTraits();

    void setTraits(Set<Trait> traits);

    void setType(JujutsuType type);

    JujutsuType getType();

    void increaseBrainDamage();

    int getBrainDamage();

    void resetBrainDamage();

    void setBurnout(int duration);

    int getBurnout();

    boolean hasBurnout();

    void resetBurnout();

    float getMaxEnergy();

    void setMaxEnergy(float maxEnergy);

    float getEnergy();

    void addEnergy(float amount);

    void useEnergy(float amount);

    void setEnergy(float energy);

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
