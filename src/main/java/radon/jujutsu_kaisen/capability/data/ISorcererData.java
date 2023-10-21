package radon.jujutsu_kaisen.capability.data;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.*;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

@AutoRegisterCapability
public interface ISorcererData {
    void tick(LivingEntity owner);

    int getPoints();
    void addPoints(int points);

    boolean isUnlocked(Ability ability);
    void unlock(Ability ability);
    void unlockAll(List<Ability> abilities);

    void createPact(UUID recipient, Pact pact);
    boolean hasPact(UUID recipient, Pact pact);
    void removePact(UUID recipient, Pact pact);

    void createPactRequest(UUID recipient, Pact pact);
    void removePactRequest(UUID recipient, Pact pact);
    boolean hasRequestedPact(UUID recipient, Pact pact);

    void addBindingVow(BindingVow vow);
    void removeBindingVow(BindingVow vow);
    boolean hasBindingVow(BindingVow vow);

    void addChant(Ability ability, String chant);
    void removeChant(Ability ability, String chant);
    boolean hasChant(Ability ability, String chant);
    boolean hasChant(String chant);
    Set<String> getChants(Ability ability);

    float getOutput(LivingEntity owner);
    float getAbilityPower(LivingEntity owner);
    float getRealPower();

    float getExperience();
    void setExperience(float experience);
    boolean addExperience(float amount);

    float getDomainSize();
    void setDomainSize(float domainSize);

    List<DomainExpansionEntity> getDomains(ServerLevel level);
    void onInsideDomain(DomainExpansionEntity domain);

    boolean hasToggled(Ability ability);

    @Nullable CursedTechnique getAdditional();
    void setAdditional(CursedTechnique technique);

    @Nullable CursedTechnique getTechnique();
    boolean hasTechnique(CursedTechnique technique);
    void setTechnique(@Nullable CursedTechnique technique);

    CursedEnergyNature getNature();
    void setNature(CursedEnergyNature nature);

    SorcererGrade getGrade();
    void setGrade(SorcererGrade grade);

    boolean hasTrait(Trait trait);
    void addTrait(Trait trait);
    void addTraits(List<Trait> traits);
    void removeTrait(Trait trait);
    Set<Trait> getTraits();

    void setType(JujutsuType type);
    JujutsuType getType();

    void toggle(LivingEntity owner, Ability ability);
    void clearToggled();
    Set<Ability> getToggled();

    void addCooldown(LivingEntity owner, Ability ability);
    int getRemainingCooldown(Ability ability);
    boolean isCooldownDone(Ability ability);

    void addDuration(LivingEntity owner, Ability ability);
    int getRemaining(Ability ability);

    void setBurnout(int duration);
    int getBurnout();
    boolean hasBurnout();

    void resetCooldowns();
    void resetBurnout();

    float getMaxEnergy(LivingEntity owner);
    void setMaxEnergy(float maxEnergy);

    float getEnergy();
    void useEnergy(float amount);
    void addEnergy(float amount);
    void setEnergy(float energy);

    void addExtraEnergy(float amount);

    void addUsed(float amount);

    void onBlackFlash(LivingEntity owner);
    long getLastBlackFlashTime();
    void resetBlackFlash();
    boolean isInZone(LivingEntity owner);

    void delayTickEvent(Runnable task, int delay);
    void scheduleTickEvent(Callable<Boolean> task, int duration);

    void copy(@Nullable CursedTechnique technique);
    Set<CursedTechnique> getCopied();

    void setCurrentCopied(@Nullable CursedTechnique technique);
    @Nullable CursedTechnique getCurrentCopied();

    void absorb(@Nullable CursedTechnique technique);
    void unabsorb(CursedTechnique technique);
    Set<CursedTechnique> getAbsorbed();

    void setCurrentAbsorbed(@Nullable CursedTechnique technique);
    @Nullable CursedTechnique getCurrentAbsorbed();

    @Nullable Ability getChanneled();
    void channel(LivingEntity owner, @Nullable Ability ability);
    boolean isChanneling(Ability ability);
    int getCharge();

    void generate(ServerPlayer player);

    void addSummon(Entity entity);
    void removeSummon(Entity entity);
    List<Entity> getSummons(ServerLevel level);
    <T extends Entity> @Nullable T getSummonByClass(ServerLevel level, Class<T> clazz);
    <T extends Entity> void unsummonByClass(ServerLevel level, Class<T> clazz);
    <T extends Entity> void removeSummonByClass(ServerLevel level, Class<T> clazz);
    <T extends Entity> boolean hasSummonOfClass(ServerLevel level, Class<T> clazz);

    boolean hasTamed(Registry<EntityType<?>> registry, EntityType<?> entity);
    void tame(Registry<EntityType<?>> registry, EntityType<?> entity);

    boolean isDead(Registry<EntityType<?>> registry, EntityType<?> entity);
    void kill(Registry<EntityType<?>> registry, EntityType<?> entity);
    void revive(boolean full);

    void setDomain(DomainExpansionEntity domain);
    @Nullable DomainExpansionEntity getDomain(ServerLevel level);

    Set<Ability> getAdapted();
    void addAdapted(Set<Ability> adaptations);

    Map<Ability, Integer> getAdapting();
    void addAdapting(Map<Ability, Integer> adapting);

    void addShadowInventory(ItemStack stack);
    ItemStack getShadowInventory(int index);
    List<ItemStack> getShadowInventory();
    void removeShadowInventory(int index);

    float getAdaptation(DamageSource source);
    float getAdaptation(Ability ability);

    boolean isAdaptedTo(DamageSource source);
    boolean isAdaptedTo(@Nullable Ability ability);
    boolean isAdaptedTo(CursedTechnique technique);

    void tryAdapt(DamageSource source);
    void tryAdapt(Ability ability);

    TenShadowsMode getMode();
    void setMode(TenShadowsMode mode);

    void addCurse(Registry<EntityType<?>> registry, EntityType<?> type);
    void removeCurse(Registry<EntityType<?>> registry, EntityType<?> type);
    Map<EntityType<?>, Integer> getCurses(Registry<EntityType<?>> registry);
    boolean hasCurse(Registry<EntityType<?>> registry, EntityType<?> type);

    int getSpeedStacks();
    void addSpeedStack();

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
