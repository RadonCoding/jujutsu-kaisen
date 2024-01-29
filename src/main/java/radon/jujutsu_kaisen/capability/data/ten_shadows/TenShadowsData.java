package radon.jujutsu_kaisen.capability.data.ten_shadows;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import radon.jujutsu_kaisen.JJKConstants;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.*;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.WheelEntity;

import java.util.*;

public class TenShadowsData implements ITenShadowsData {
    private final Set<ResourceLocation> tamed;
    private final Set<ResourceLocation> dead;
    private final List<ItemStack> shadowInventory;
    private final Set<Adaptation> adapted;
    private final Map<Adaptation, Integer> adapting;
    private TenShadowsMode mode;

    private LivingEntity owner;

    public TenShadowsData() {
        this.mode = TenShadowsMode.SUMMON;
        this.tamed = new HashSet<>();
        this.dead = new HashSet<>();
        this.adapted = new HashSet<>();
        this.adapting = new HashMap<>();
        this.shadowInventory = new ArrayList<>();
    }

    private void updateAdaptation() {
        ISorcererData cap = this.owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (!cap.hasToggled(JJKAbilities.WHEEL.get())) return;

        Iterator<Map.Entry<Adaptation, Integer>> iter = this.adapting.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Adaptation, Integer> entry = iter.next();

            int timer = entry.getValue();

            if (++timer >= JJKConstants.REQUIRED_ADAPTATION) {
                iter.remove();

                this.adapted.add(entry.getKey());

                if (this.owner instanceof MahoragaEntity mahoraga) {
                    mahoraga.onAdaptation();
                }

                WheelEntity wheel = cap.getSummonByClass(WheelEntity.class);

                if (wheel != null) {
                    wheel.spin();
                }
            } else {
                entry.setValue(timer);
            }
        }
    }

    @Override
    public void tick(LivingEntity owner) {
        if (this.owner == null) {
            this.owner = owner;
        }

        if (!this.owner.level().isClientSide) {
            this.updateAdaptation();
        }
    }

    @Override
    public void init(LivingEntity owner) {
        this.owner = owner;
    }

    @Override
    public boolean hasTamed(Registry<EntityType<?>> registry, EntityType<?> entity) {
        return this.tamed.contains(registry.getKey(entity));
    }

    @Override
    public void tame(Registry<EntityType<?>> registry, EntityType<?> entity) {
        this.tamed.add(registry.getKey(entity));
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
    public boolean isDead(Registry<EntityType<?>> registry, EntityType<?> entity) {
        return this.dead.contains(registry.getKey(entity));
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
    public void kill(Registry<EntityType<?>> registry, EntityType<?> entity) {
        this.dead.add(registry.getKey(entity));
    }

    @Override
    public void revive(boolean full) {
        this.dead.clear();

        if (full) {
            this.tamed.clear();
        }
    }

    @Override
    public Set<Adaptation> getAdapted() {
        return this.adapted;
    }

    @Override
    public void addAdapted(Set<Adaptation> adaptations) {
        this.adapted.addAll(adaptations);
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
        RegistryAccess registry = this.owner.level().registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new Adaptation(types.getKey(source.type()),
                source instanceof JJKDamageSources.JujutsuDamageSource jujutsu ? jujutsu.getAbility() : null);
    }

    @Override
    public float getAdaptationProgress(DamageSource source) {
        return this.getAdaptationProgress(this.getAdaptation(source));
    }

    @Override
    public float getAdaptationProgress(Adaptation adaptation) {
        return this.adapted.contains(adaptation) ? 1.0F : (float) this.adapting.getOrDefault(adaptation, 0) / JJKConstants.REQUIRED_ADAPTATION;
    }

    @Override
    public Adaptation.Type getAdaptationType(DamageSource source) {
        Adaptation adaptation = this.getAdaptation(source);
        return this.getAdaptationType(adaptation);
    }

    @Override
    public Adaptation.Type getAdaptationType(Adaptation adaptation) {
        RegistryAccess registry = this.owner.level().registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);

        DamageType type = types.get(adaptation.getKey());

        if (type == types.get(DamageTypes.MOB_ATTACK) || type == types.get(DamageTypes.PLAYER_ATTACK)) {
            return Adaptation.Type.COUNTER;
        }
        return Adaptation.Type.DAMAGE;
    }

    @Override
    public Map<Adaptation.Type, Float> getAdaptationTypes() {
        Map<Adaptation.Type, Float> adaptations = new HashMap<>();

        for (Adaptation adaptation : this.adapting.keySet()) {
            adaptations.put(this.getAdaptationType(adaptation), this.getAdaptationProgress(adaptation));
        }
        for (Adaptation adaptation : this.adapted) {
            adaptations.put(this.getAdaptationType(adaptation), this.getAdaptationProgress(adaptation));
        }
        return adaptations;
    }

    @Override
    public boolean isAdaptedTo(DamageSource source) {
        Adaptation adaptation = this.getAdaptation(source);
        return this.adapted.contains(adaptation);
    }

    @Override
    public boolean isAdaptedTo(Ability ability) {
        for (Adaptation adapted : this.adapted) {
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
    public boolean isAdaptedTo(CursedTechnique technique) {
        for (Ability ability : technique.getAbilities()) {
            if (this.isAdaptedTo(ability)) return true;
        }
        return false;
    }

    @Override
    public void tryAdapt(DamageSource source) {
        RegistryAccess registry = this.owner.level().registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);

        Adaptation adaptation = new Adaptation(types.getKey(source.type()),
                source instanceof JJKDamageSources.JujutsuDamageSource jujutsu ? jujutsu.getAbility() : null);

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

        if (!this.adapting.containsKey(adaptation)) {
            this.adapting.put(adaptation, 0);
        } else {
            int timer = this.adapting.get(adaptation);
            timer += JJKConstants.ADAPTATION_STEP;
            this.adapting.put(adaptation, timer);
        }
    }

    @Override
    public TenShadowsMode getMode() {
        return this.mode;
    }

    @Override
    public void setMode(TenShadowsMode mode) {
        this.mode = mode;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("mode", this.mode.ordinal());

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

        for (Adaptation adaptation : this.adapted) {
            adaptedTag.add(adaptation.serializeNBT());
        }
        nbt.put("adapted", adaptedTag);

        ListTag adaptingTag = new ListTag();

        for (Map.Entry<Adaptation, Integer> entry : this.adapting.entrySet()) {
            CompoundTag data = new CompoundTag();
            data.put("adaptation", entry.getKey().serializeNBT());
            data.putInt("stage", entry.getValue());
            adaptingTag.add(data);
        }
        nbt.put("adapting", adaptingTag);

        ListTag shadowInventoryTag = new ListTag();

        for (ItemStack stack : this.shadowInventory) {
            shadowInventoryTag.add(stack.save(new CompoundTag()));
        }
        nbt.put("shadow_inventory", shadowInventoryTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.mode = TenShadowsMode.values()[nbt.getInt("mode")];

        this.tamed.clear();

        for (Tag key : nbt.getList("tamed", Tag.TAG_STRING)) {
            this.tamed.add(new ResourceLocation(key.getAsString()));
        }

        this.dead.clear();

        for (Tag key : nbt.getList("dead", Tag.TAG_STRING)) {
            this.dead.add(new ResourceLocation(key.getAsString()));
        }

        this.adapted.clear();

        for (Tag key : nbt.getList("adapted", Tag.TAG_COMPOUND)) {
            this.adapted.add(new Adaptation((CompoundTag) key));
        }

        this.adapting.clear();

        for (Tag key : nbt.getList("adapting", Tag.TAG_COMPOUND)) {
            CompoundTag adaptation = (CompoundTag) key;
            this.adapting.put(new Adaptation(adaptation.getCompound("adaptation")), adaptation.getInt("stage"));
        }

        this.shadowInventory.clear();

        for (Tag key : nbt.getList("shadow_inventory", Tag.TAG_COMPOUND)) {
            this.shadowInventory.add(ItemStack.of((CompoundTag) key));
        }
    }
}
