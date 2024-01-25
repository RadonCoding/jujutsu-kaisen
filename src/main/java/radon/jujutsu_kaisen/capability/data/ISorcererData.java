package radon.jujutsu_kaisen.capability.data;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.*;

import java.util.*;

@AutoRegisterCapability
public interface ISorcererData {
    void attack(DamageSource source, LivingEntity target);

    void tick(LivingEntity owner);

    void init(LivingEntity owner);

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

    @Nullable CursedTechnique getAdditional();

    void setAdditional(CursedTechnique technique);

    @Nullable CursedTechnique getTechnique();
    Set<CursedTechnique> getTechniques();

    boolean hasTechnique(CursedTechnique technique);

    void setTechnique(@Nullable CursedTechnique technique);

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

    void addDuration(Ability ability);

    void increaseBrainDamage();
    int getBrainDamage();
    
    void setBurnout(int duration);

    int getBurnout();

    boolean hasBurnout();

    void resetCooldowns();

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
    
    void uncopy(CursedTechnique technique);

    void copy(@Nullable CursedTechnique technique);

    Set<CursedTechnique> getCopied();

    void setCurrentCopied(@Nullable CursedTechnique technique);

    @Nullable CursedTechnique getCurrentCopied();

    void absorb(@Nullable CursedTechnique technique);

    void unabsorb(CursedTechnique technique);

    Set<CursedTechnique> getAbsorbed();

    void setCurrentAbsorbed(@Nullable CursedTechnique technique);

    @Nullable CursedTechnique getCurrentAbsorbed();

    int getTransfiguredSouls();

    void increaseTransfiguredSouls();

    void decreaseTransfiguredSouls();

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

    boolean hasTamed(Registry<EntityType<?>> registry, EntityType<?> entity);
    void tame(Registry<EntityType<?>> registry, EntityType<?> entity);
    void setTamed(Set<ResourceLocation> tamed);
    Set<ResourceLocation> getTamed();
    boolean isDead(Registry<EntityType<?>> registry, EntityType<?> entity);
    Set<ResourceLocation> getDead();
    void setDead(Set<ResourceLocation> dead);

    void kill(Registry<EntityType<?>> registry, EntityType<?> entity);

    void revive(boolean full);

    Set<Adaptation> getAdapted();

    void addAdapted(Set<Adaptation> adaptations);

    Map<Adaptation, Integer> getAdapting();

    void addAdapting(Map<Adaptation, Integer> adapting);

    void addShadowInventory(ItemStack stack);

    ItemStack getShadowInventory(int index);

    List<ItemStack> getShadowInventory();

    void removeShadowInventory(int index);

    float getAdaptationProgress(DamageSource source);

    float getAdaptationProgress(Adaptation adaptation);

    Adaptation.Type getAdaptationType(DamageSource source);

    Adaptation.Type getAdaptationType(Adaptation adaptation);

    Map<Adaptation.Type, Float> getAdaptationTypes();

    boolean isAdaptedTo(DamageSource source);

    boolean isAdaptedTo(@Nullable Ability ability);

    boolean isAdaptedTo(CursedTechnique technique);

    void tryAdapt(DamageSource source);

    void tryAdapt(Ability ability);

    TenShadowsMode getMode();

    void setMode(TenShadowsMode mode);

    void addCurse(AbsorbedCurse curse);

    void removeCurse(AbsorbedCurse curse);

    List<AbsorbedCurse> getCurses();

    List<AbstractMap.SimpleEntry<Vec3, Float>> getFrames();
    void addFrame(Vec3 frame, float yaw);
    void removeFrame(AbstractMap.SimpleEntry<Vec3, Float> frame);
    void resetFrames();

    int getSpeedStacks();
    void addSpeedStack();
    void resetSpeedStacks();

    int getFingers();
    void setFingers(int count);
    int addFingers(int count);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
