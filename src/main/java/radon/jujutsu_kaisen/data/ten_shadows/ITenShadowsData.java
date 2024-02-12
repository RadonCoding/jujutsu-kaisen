package radon.jujutsu_kaisen.data.ten_shadows;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.*;

public interface ITenShadowsData extends INBTSerializable<CompoundTag> {
    void tick();

    boolean hasTamed(EntityType<?> entity);

    void tame(EntityType<?> entity);

    void setTamed(Set<ResourceLocation> tamed);

    Set<ResourceLocation> getTamed();

    boolean isDead(EntityType<?> entity);

    Set<ResourceLocation> getDead();

    void setDead(Set<ResourceLocation> dead);

    void kill(EntityType<?> entity);

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

    boolean isAdaptedTo(ICursedTechnique technique);

    void tryAdapt(DamageSource source);

    void tryAdapt(Ability ability);

    TenShadowsMode getMode();

    void setMode(TenShadowsMode mode);
}
