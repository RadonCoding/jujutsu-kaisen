package radon.jujutsu_kaisen.data.ten_shadows;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JJKConstants;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.IAdditionalAdaptation;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.ICursedTechnique;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.WheelEntity;

import java.util.*;

public class TenShadowsData implements ITenShadowsData {
    private final Set<ResourceLocation> tamed;
    private final Set<ResourceLocation> dead;
    private final List<ItemStack> shadowInventory;
    private final Map<Adaptation, Integer> adapted;
    private final Map<Adaptation, Integer> adapting;

    private final LivingEntity owner;

    public TenShadowsData(LivingEntity owner) {
        this.owner = owner;

        this.tamed = new HashSet<>();
        this.dead = new HashSet<>();
        this.adapted = new HashMap<>();
        this.adapting = new HashMap<>();
        this.shadowInventory = new ArrayList<>();
    }

    private void updateAdaptation() {
        IJujutsuCapability cap = this.owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IAbilityData abilityData = cap.getAbilityData();

        if (!abilityData.hasToggled(JJKAbilities.WHEEL.get())) return;

        Iterator<Map.Entry<Adaptation, Integer>> iter = this.adapting.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Adaptation, Integer> entry = iter.next();

            int timer = entry.getValue();

            if (++timer >= JJKConstants.REQUIRED_ADAPTATION) {
                iter.remove();

                if (this.owner instanceof MahoragaEntity mahoraga) {
                    if (!this.adapted.containsKey(entry.getKey())) {
                        mahoraga.onAdaptation();
                    }
                }
                this.adapted.put(entry.getKey(), this.adapted.getOrDefault(entry.getKey(), 0) + 1);

                WheelEntity wheel = sorcererData.getSummonByClass(WheelEntity.class);

                if (wheel != null) {
                    wheel.spin();
                }
            } else {
                entry.setValue(timer);
            }
        }
    }

    @Override
    public void tick() {
        this.updateAdaptation();
    }

    @Override
    public boolean hasTamed(EntityType<?> entity) {
        return this.tamed.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity));
    }

    @Override
    public void tame(EntityType<?> entity) {
        this.tamed.add(BuiltInRegistries.ENTITY_TYPE.getKey(entity));
    }

    @Override
    public void setTamed(Set<ResourceLocation> tamed) {
        this.tamed.clear();
        this.tamed.addAll(tamed);
    }

    @Override
    public Set<ResourceLocation> getTamed() {
        return this.tamed;
    }

    @Override
    public boolean isDead(EntityType<?> entity) {
        return this.dead.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity));
    }

    @Override
    public Set<ResourceLocation> getDead() {
        return this.dead;
    }

    @Override
    public void setDead(Set<ResourceLocation> dead) {
        this.dead.clear();
        this.dead.addAll(dead);
    }

    @Override
    public void kill(EntityType<?> entity) {
        this.dead.add(BuiltInRegistries.ENTITY_TYPE.getKey(entity));
    }

    @Override
    public void revive(boolean full) {
        this.dead.clear();

        if (full) {
            this.tamed.clear();
        }
    }

    @Override
    public Map<Adaptation, Integer> getAdapted() {
        return this.adapted;
    }

    @Override
    public void addAdapted(Map<Adaptation, Integer> adaptations) {
        this.adapted.putAll(adaptations);
    }

    @Override
    public Map<Adaptation, Integer> getAdapting() {
        return this.adapting;
    }

    @Override
    public void addAdapting(Map<Adaptation, Integer> adapting) {
        this.adapting.putAll(adapting);
    }

    @Override
    public void addShadowInventory(ItemStack stack) {
        this.shadowInventory.add(stack);
    }

    @Override
    public ItemStack getShadowInventory(int index) {
        return this.shadowInventory.get(index);
    }

    @Override
    public List<ItemStack> getShadowInventory() {
        return this.shadowInventory;
    }

    @Override
    public void removeShadowInventory(int index) {
        this.shadowInventory.remove(index);
    }

    private Adaptation getAdaptation(DamageSource source) {
        RegistryAccess registry = this.owner.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new Adaptation(types.getKey(source.type()),
                source instanceof JJKDamageSources.JujutsuDamageSource cap ? cap.getAbility() : null);
    }

    @Override
    public float getAdaptationProgress(DamageSource source) {
        return this.getAdaptationProgress(this.getAdaptation(source));
    }

    @Override
    public float getAdaptationProgress(Adaptation adaptation) {
        return this.adapted.containsKey(adaptation) ? 1.0F : (float) this.adapting.getOrDefault(adaptation, 0) / JJKConstants.REQUIRED_ADAPTATION;
    }

    @Override
    public Adaptation.Type getAdaptationType(DamageSource source) {
        Adaptation adaptation = this.getAdaptation(source);
        return this.getAdaptationType(adaptation);
    }

    @Override
    public Adaptation.Type getAdaptationType(Adaptation adaptation) {
        RegistryAccess registry = this.owner.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);

        DamageType type = types.get(adaptation.getKey());

        if (type == types.get(DamageTypes.MOB_ATTACK) || type == types.get(DamageTypes.PLAYER_ATTACK)) {
            return Adaptation.Type.COUNTER;
        }
        return Adaptation.Type.DAMAGE;
    }

    @Override
    public int getAdaptation(Ability ability) {
        for (Map.Entry<Adaptation, Integer> entry : this.adapted.entrySet()) {
            Adaptation adapted = entry.getKey();

            Ability current = adapted.getAbility();

            if (current == null) continue;

            if (current == ability) return entry.getValue();

            Ability.Classification first = current.getClassification();
            Ability.Classification second = ability.getClassification();

            if (first == Ability.Classification.NONE || second == Ability.Classification.NONE) continue;
            if (first == second) return entry.getValue();
        }
        return 0;
    }

    @Override
    public boolean isAdaptedTo(DamageSource source) {
        if (source instanceof JJKDamageSources.JujutsuDamageSource jujutsu) {
            Ability ability = jujutsu.getAbility();

            if (ability != null) {
                return this.isAdaptedTo(ability);
            }
        }
        Adaptation adaptation = this.getAdaptation(source);
        return this.adapted.containsKey(adaptation);
    }

    @Override
    public boolean isAdaptedTo(Ability ability) {
        for (Adaptation adapted : this.adapted.keySet()) {
            Ability current = adapted.getAbility();

            if (current == null) continue;

            if (current == ability) return true;

            Ability.Classification first = current.getClassification();
            Ability.Classification second = ability.getClassification();

            if (first == Ability.Classification.NONE || second == Ability.Classification.NONE) continue;
            if (first == second) return true;
        }
        return false;
    }

    @Override
    public boolean isAdaptedTo(ICursedTechnique technique) {
        for (Ability ability : technique.getAbilities()) {
            if (this.isAdaptedTo(ability)) return true;
        }
        return false;
    }

    @Override
    public void tryAdapt(DamageSource source) {
        if (source instanceof JJKDamageSources.JujutsuDamageSource jujutsu) {
            Ability ability = jujutsu.getAbility();

            if (ability != null) {
                this.tryAdapt(ability);
            }
            return;
        }

        if (this.isAdaptedTo(source)) return;

        RegistryAccess registry = this.owner.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);

        Adaptation adaptation = new Adaptation(types.getKey(source.type()), null);

        if (!this.adapting.containsKey(adaptation)) {
            this.adapting.put(adaptation, 0);
        } else {
            int timer = this.adapting.get(adaptation);
            timer += JJKConstants.ADAPTATION_STEP;
            this.adapting.put(adaptation, timer);
        }
    }

    @Override
    public void tryAdapt(Ability ability) {
        Adaptation adaptation = new Adaptation(JJKDamageSources.JUJUTSU.location(), ability);

        if (this.isAdaptedTo(ability) && (!(ability instanceof IAdditionalAdaptation additional) || this.adapted.get(adaptation) >= additional.getAdditional() + 1)) return;

        if (!this.adapting.containsKey(adaptation)) {
            this.adapting.put(adaptation, 0);
        } else {
            int timer = this.adapting.get(adaptation);
            timer += JJKConstants.ADAPTATION_STEP;
            this.adapting.put(adaptation, timer);
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();

        ListTag tamedTag = new ListTag();

        for (ResourceLocation key : this.tamed) {
            tamedTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("tamed", tamedTag);

        ListTag deadTag = new ListTag();

        for (ResourceLocation key : this.dead) {
            deadTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("dead", deadTag);

        ListTag adaptedTag = new ListTag();

        for (Map.Entry<Adaptation, Integer> entry : this.adapted.entrySet()) {
            CompoundTag data = new CompoundTag();
            data.put("adaptation", entry.getKey().serializeNBT());
            data.putInt("stage", entry.getValue());
            adaptedTag.add(data);
        }
        nbt.put("adapted", adaptedTag);

        ListTag shadowInventoryTag = new ListTag();

        for (ItemStack stack : this.shadowInventory) {
            shadowInventoryTag.add(stack.save(provider, new CompoundTag()));
        }
        nbt.put("shadow_inventory", shadowInventoryTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        this.tamed.clear();

        for (Tag tag : nbt.getList("tamed", Tag.TAG_STRING)) {
            this.tamed.add(new ResourceLocation(tag.getAsString()));
        }

        this.dead.clear();

        for (Tag tag : nbt.getList("dead", Tag.TAG_STRING)) {
            this.dead.add(new ResourceLocation(tag.getAsString()));
        }

        this.adapted.clear();

        for (Tag tag : nbt.getList("adapted", Tag.TAG_COMPOUND)) {
            CompoundTag DATA = (CompoundTag) tag;
            this.adapted.put(new Adaptation(DATA.getCompound("adaptation")), DATA.getInt("stage"));
        }

        this.shadowInventory.clear();

        for (Tag key : nbt.getList("shadow_inventory", Tag.TAG_COMPOUND)) {
            this.shadowInventory.add(ItemStack.parse(provider, key).orElseThrow());
        }
    }
}
