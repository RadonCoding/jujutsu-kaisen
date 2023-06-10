package radon.jujutsu_kaisen.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JujutsuAbilities;

import java.util.*;
import java.util.function.Consumer;

public class SorcererData implements ISorcererData {
    private boolean initialized;
    private CursedTechnique technique;
    private SpecialTrait trait;
    private float experience;

    private float energy;
    private float maxEnergy;

    private final Set<Ability> toggledAbilities;
    private final List<DelayedTickEvent> delayedTickEvents = new ArrayList<>();
    private final Map<Ability, Integer> cooldowns = new HashMap<>();

    public SorcererData() {
        this.toggledAbilities = new HashSet<>();
    }

    private void updateCooldowns() {
        Iterator<Map.Entry<Ability, Integer>> iter = this.cooldowns.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Ability, Integer> entry = iter.next();

            int remaining = entry.getValue();

            if (remaining > 0) {
                this.cooldowns.put(entry.getKey(), --remaining);
            } else {
                iter.remove();
            }
        }
    }

    private void updateTickEvents(LivingEntity owner) {
        Iterator<DelayedTickEvent> iter = this.delayedTickEvents.iterator();

        while (iter.hasNext()) {
            DelayedTickEvent event = iter.next();

            event.tick();

            if (event.run(owner)) {
                iter.remove();
            }
        }
    }

    private void disableToggledAbility(LivingEntity owner, Ability ability) {
        this.toggledAbilities.remove(ability);

        if (ability instanceof Ability.IToggled toggled) {
            toggled.onDisabled(owner);
        }
    }

    private void updateToggledAbilities(LivingEntity owner) {
        List<Ability> remove = new ArrayList<>();

        for (Ability ability : this.toggledAbilities) {
            if (ability.checkStatus(owner) != Ability.Status.SUCCESS) {
                remove.add(ability);
            } else {
                ability.run(owner);
            }
        }

        for (Ability ability : remove) {
            disableToggledAbility(owner, ability);
        }
    }

    public void tick(LivingEntity owner) {
        for (Ability ability : this.toggledAbilities) {
            ability.run(owner);
        }

        this.updateCooldowns();
        this.updateTickEvents(owner);
        this.updateToggledAbilities(owner);

        this.energy = Math.min(this.energy + 1.0F, this.maxEnergy);
    }

    public CursedTechnique getTechnique() {
        return this.technique;
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.getGrade(this.experience);
    }

    @Override
    public void addExperience(float amount) {
        this.experience += amount;
    }

    @Override
    public SpecialTrait getTrait() {
        return this.trait;
    }

    public void toggleAbility(LivingEntity entity, Ability ability) {
        if (ability instanceof Ability.IToggled toggled) {
            if (this.toggledAbilities.contains(ability)) {
                toggled.onDisabled(entity);
                this.toggledAbilities.remove(ability);
            } else {
                toggled.onEnabled(entity);
                this.toggledAbilities.add(ability);
            }
        }
    }

    @Override
    public void addCooldown(Ability ability) {
        this.cooldowns.put(ability, ability.getCooldown());
    }

    @Override
    public int getRemainingCooldown(Ability ability) {
        return this.cooldowns.get(ability);
    }

    @Override
    public boolean isCooldownDone(Ability ability) {
        return !this.cooldowns.containsKey(ability);
    }

    @Override
    public float getEnergy() {
        return this.energy;
    }

    @Override
    public float getMaxEnergy() {
        return this.maxEnergy;
    }

    @Override
    public void useEnergy(float amount) {
        this.energy -= amount;
    }

    @Override
    public void delayTickEvent(Consumer<LivingEntity> task, int delay) {
        this.delayedTickEvents.add(new DelayedTickEvent(task, delay));
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void generate() {
        this.initialized = true;

        this.technique = CursedTechnique.GOJO;
        this.maxEnergy = this.technique.getMaxEnergy();
        this.trait = SpecialTrait.HEAVENLY_RESTRICTION;

        /*if (HelperMethods.RANDOM.nextInt(10) == 0) {
            this.trait = SpecialTrait.HEAVENLY_RESTRICTION;
        } else {
            if (HelperMethods.RANDOM.nextInt(10) == 0) {
                this.trait = SpecialTrait.SIX_EYES;
            }
            this.technique = HelperMethods.randomEnum(CursedTechnique.class);
        }*/
    }

    public boolean hasToggledAbility(Ability ability) {
        return this.toggledAbilities.contains(ability);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("initialized", this.initialized);

        if (this.technique != null) {
            nbt.putInt("technique", this.technique.ordinal());
        }
        if (this.trait != null) {
            nbt.putInt("trait", this.trait.ordinal());
        }
        nbt.putFloat("experience", this.experience);
        nbt.putFloat("energy", this.energy);

        ListTag toggledTag = new ListTag();

        for (Ability ability : this.toggledAbilities) {
            toggledTag.add(StringTag.valueOf(JujutsuAbilities.getKey(ability).toString()));
        }
        nbt.put("toggled", toggledTag);

        ListTag cooldownsTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.cooldowns.entrySet()) {
            CompoundTag cooldown = new CompoundTag();
            cooldown.putString("identifier", JujutsuAbilities.getKey(entry.getKey()).toString());
            cooldown.putInt("cooldown", entry.getValue());
            cooldownsTag.add(cooldown);
        }
        nbt.put("cooldowns", cooldownsTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.initialized = nbt.getBoolean("initialized");

        if (nbt.contains("technique")) {
            this.technique = CursedTechnique.values()[nbt.getInt("technique")];
        }
        if (nbt.contains("trait")) {
            this.trait = SpecialTrait.values()[nbt.getInt("trait")];
        }
        this.experience = nbt.getFloat("experience");
        this.energy = nbt.getFloat("energy");

        for (Tag key : nbt.getList("toggled", Tag.TAG_STRING)) {
            this.toggledAbilities.add(JujutsuAbilities.getValue(new ResourceLocation(key.getAsString())));
        }

        for (Tag key : nbt.getList("cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag cooldown = (CompoundTag) key;
            this.cooldowns.put(JujutsuAbilities.getValue(new ResourceLocation(cooldown.getString("identifier"))),
                    cooldown.getInt("cooldown"));
        }
    }
}
