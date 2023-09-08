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
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.*;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public interface ISorcererData {
    void tick(LivingEntity owner);

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

    void setType(JujutsuType type);
    JujutsuType getType();

    void exorcise(LivingEntity owner, SorcererGrade grade);
    void consume(LivingEntity owner, SorcererGrade grade);

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

    float getEnergy();
    float getMaxEnergy();
    void setMaxEnergy(float maxEnergy);
    void useEnergy(float amount);
    void addEnergy(float amount);
    void setEnergy(float energy);

    void addUsed(float amount);

    void onBlackFlash(LivingEntity owner);
    long getLastBlackFlashTime();
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

    void channel(LivingEntity owner, @Nullable Ability ability);
    boolean isChanneling(Ability ability);
    int getCharge();

    void generate(ServerPlayer player);

    void addSummon(Entity entity);
    void removeSummon(Entity entity);
    List<Entity> getSummons(ServerLevel level);
    <T extends Entity> @Nullable T getSummonByClass(ServerLevel level, Class<T> clazz);
    <T extends Entity> void unsummonByClass(ServerLevel level, Class<T> clazz);
    <T extends Entity> boolean hasSummonOfClass(ServerLevel level, Class<T> clazz);

    boolean hasTamed(Registry<EntityType<?>> registry, EntityType<?> entity);
    void tame(Registry<EntityType<?>> registry, EntityType<?> entity);

    boolean isDead(Registry<EntityType<?>> registry, EntityType<?> entity);
    void kill(Registry<EntityType<?>> registry, EntityType<?> entity);
    void revive(boolean full);

    void setDomain(DomainExpansionEntity domain);
    @Nullable DomainExpansionEntity getDomain(ServerLevel level);

    Set<Ability.Classification> getAdapted();
    void adaptAll(Set<Ability.Classification> adaptations);

    void addShadowInventory(ItemStack stack);
    ItemStack getShadowInventory(int index);
    List<ItemStack> getShadowInventory();
    void removeShadowInventory(int index);

    boolean isAdaptedTo(DamageSource source);
    boolean isAdaptedTo(Ability ability);
    boolean isAdaptedTo(CursedTechnique technique);

    boolean tryAdapt(DamageSource source);
    boolean tryAdapt(Ability ability);

    TenShadowsMode getMode();
    void setMode(TenShadowsMode mode);

    void addCurse(Registry<EntityType<?>> registry, EntityType<?> type);
    void removeCurse(Registry<EntityType<?>> registry, EntityType<?> type);
    void removeAllCurses(Registry<EntityType<?>> registry, EntityType<?> type);
    Map<EntityType<?>, Integer> getCurses(Registry<EntityType<?>> registry);
    boolean hasCurse(Registry<EntityType<?>> registry, EntityType<?> type);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
