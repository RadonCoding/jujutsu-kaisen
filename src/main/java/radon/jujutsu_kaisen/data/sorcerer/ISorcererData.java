package radon.jujutsu_kaisen.data.sorcerer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ISorcererData extends INBTSerializable<CompoundTag> {
    void attack(DamageSource source, LivingEntity target);

    void tick();

    int getCursedEnergyColor();

    void setCursedEnergyColor(int color);

    float getMaximumOutput();

    void increaseOutput();

    void decreaseOutput();

    int getPoints();

    void setPoints(int points);

    void addPoints(int points);

    void usePoints(int count);

    boolean isUnlocked(Ability ability);

    void unlock(Ability ability);

    void unlockAll(List<Ability> abilities);

    void createPact(UUID recipient, Pact pact);

    boolean hasPact(UUID recipient, Pact pact);

    void removePact(UUID recipient, Pact pact);

    void createPactCreationRequest(UUID recipient, Pact pact);

    void createPactRemovalRequest(UUID recipient, Pact pact);

    void removePactCreationRequest(UUID recipient, Pact pact);

    void removePactRemovalRequest(UUID recipient, Pact pact);

    boolean hasRequestedPactCreation(UUID recipient, Pact pact);

    boolean hasRequestedPactRemoval(UUID recipient, Pact pact);

    void addBindingVow(BindingVow vow);

    void removeBindingVow(BindingVow vow);

    boolean hasBindingVow(BindingVow vow);

    void addBindingVowCooldown(BindingVow vow);

    int getRemainingCooldown(BindingVow vow);

    boolean isCooldownDone(BindingVow vow);

    void addChant(Ability ability, String chant);

    void addChants(Ability ability, Set<String> chants);

    void removeChant(Ability ability, String chant);

    boolean hasChant(Ability ability, String chant);

    boolean isChantsAvailable(Set<String> chants);

    @Nullable Ability getAbility(String chant);

    @Nullable Ability getAbility(Set<String> chants);

    Set<String> getFirstChants();

    Set<String> getFirstChants(Ability ability);

    float getOutput();

    float getAbilityPower();

    float getRealPower();

    float getExperience();

    void setExperience(float experience);

    boolean addExperience(float amount);

    float getDomainSize();

    void setDomainSize(float domainSize);

    boolean hasToggled(Ability ability);

    @Nullable ICursedTechnique getAdditional();

    void setAdditional(ICursedTechnique technique);

    @Nullable ICursedTechnique getTechnique();

    void setTechnique(@Nullable ICursedTechnique technique);

    Set<ICursedTechnique> getActiveTechniques();

    Set<ICursedTechnique> getTechniques();

    boolean hasTechnique(ICursedTechnique technique);

    boolean hasActiveTechnique(ICursedTechnique technique);

    CursedEnergyNature getNature();

    void setNature(CursedEnergyNature nature);

    void setGrade(SorcererGrade grade);

    boolean hasTrait(Trait trait);

    void addTrait(Trait trait);

    void addTraits(List<Trait> traits);

    void removeTrait(Trait trait);

    Set<Trait> getTraits();

    void setTraits(Set<Trait> traits);

    void setType(JujutsuType type);

    JujutsuType getType();

    void toggle(Ability ability);

    void clearToggled();

    Set<Ability> getToggled();

    void addCooldown(Ability ability);

    int getRemainingCooldown(Ability ability);

    boolean isCooldownDone(Ability ability);

    void resetCooldowns();

    void disrupt(Ability ability, int duration);

    void addDuration(Ability ability);

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

    void delayTickEvent(Runnable task, int delay);
    
    void uncopy(ICursedTechnique technique);

    void copy(@Nullable ICursedTechnique technique);

    Set<ICursedTechnique> getCopied();

    void setCurrentCopied(@Nullable ICursedTechnique technique);

    @Nullable ICursedTechnique getCurrentCopied();

    int getTransfiguredSouls();

    void increaseTransfiguredSouls();

    void decreaseTransfiguredSouls();

    void useTransfiguredSouls(int amount);

    @Nullable Ability getChanneled();

    void channel(@Nullable Ability ability);

    boolean isChanneling(Ability ability);

    int getCharge();

    void generate(ServerPlayer owner);

    void addSummon(Entity entity);

    void removeSummon(Entity entity);

    List<Entity> getSummons();

    <T extends Entity> @Nullable T getSummonByClass(Class<T> clazz);

    <T extends Entity> void unsummonByClass(Class<T> clazz);

    <T extends Entity> void removeSummonByClass(Class<T> clazz);

    <T extends Entity> boolean hasSummonOfClass(Class<T> clazz);

    int getFingers();

    void setFingers(int count);

    int addFingers(int count);
}
