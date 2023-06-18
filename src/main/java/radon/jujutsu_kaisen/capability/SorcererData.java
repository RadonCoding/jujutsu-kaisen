package radon.jujutsu_kaisen.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class SorcererData implements ISorcererData {
    private boolean initialized;

    private CursedTechnique technique;

    @Nullable
    private SpecialTrait trait;

    private float experience;

    private float energy;

    private int burnout;

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

        if (this.burnout > 0) {
            this.burnout--;
        }
        this.energy = Math.min(this.energy + 1.0F, this.getMaxEnergy());

        SorcererEffects.apply(owner, this.getGrade());
    }

    public @Nullable CursedTechnique getTechnique() {
        return this.technique;
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.getGrade(this.experience);
    }

    @Override
    public void setGrade(SorcererGrade grade) {
        this.experience = grade.getRequiredExperience();
    }

    @Override
    public @Nullable SpecialTrait getTrait() {
        return this.trait;
    }

    @Override
    public void setTrait(@Nullable SpecialTrait trait) {
        this.trait = trait;
    }

    @Override
    public void addExperience(LivingEntity owner, float amount) {
        SorcererGrade oldGrade = this.getGrade();
        this.experience += amount;
        SorcererGrade newGrade = this.getGrade();

        if (oldGrade != newGrade) {
            if (owner instanceof Player player) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.rank_up", JujutsuKaisen.MOD_ID), newGrade.getComponent()));
            }
        }
    }

    public void toggleAbility(LivingEntity owner, Ability ability) {
        if (ability instanceof Ability.IToggled toggled) {
            if (this.toggledAbilities.contains(ability)) {
                toggled.onDisabled(owner);
                this.toggledAbilities.remove(ability);
            } else {
                toggled.onEnabled(owner);
                this.toggledAbilities.add(ability);
            }
        }
    }

    @Override
    public void addCooldown(LivingEntity owner, Ability ability) {
        this.cooldowns.put(ability, ability.getRealCooldown(owner));
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
    public void setBurnout(int duration) {
        this.burnout = duration;
    }

    @Override
    public int getBurnout() {
        return this.burnout;
    }

    @Override
    public boolean hasBurnout() {
        return this.burnout > 0;
    }

    @Override
    public float getEnergy() {
        return this.energy;
    }

    @Override
    public float getMaxEnergy() {
        if (this.trait == SpecialTrait.HEAVENLY_RESTRICTION) {
            return 0.0F;
        }
        return this.technique.getMaxEnergy() * this.getGrade().getPower();
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
        this.trait = SpecialTrait.SIX_EYES;

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
        nbt.putInt("burnout", this.burnout);

        ListTag toggledTag = new ListTag();

        for (Ability ability : this.toggledAbilities) {
            toggledTag.add(StringTag.valueOf(JJKAbilities.getKey(ability).toString()));
        }
        nbt.put("toggled", toggledTag);

        ListTag cooldownsTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.cooldowns.entrySet()) {
            CompoundTag cooldown = new CompoundTag();
            cooldown.putString("identifier", JJKAbilities.getKey(entry.getKey()).toString());
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
        this.burnout = nbt.getInt("burnout");

        for (Tag key : nbt.getList("toggled", Tag.TAG_STRING)) {
            this.toggledAbilities.add(JJKAbilities.getValue(new ResourceLocation(key.getAsString())));
        }

        for (Tag key : nbt.getList("cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag cooldown = (CompoundTag) key;
            this.cooldowns.put(JJKAbilities.getValue(new ResourceLocation(cooldown.getString("identifier"))),
                    cooldown.getInt("cooldown"));
        }
    }
}
