package radon.jujutsu_kaisen.capability.data;

import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.CurseGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class SorcererData implements ISorcererData {
    private boolean initialized;

    private CursedTechnique technique;

    private Trait trait;

    private float experience;
    private SorcererGrade grade;

    private float energy;

    private int burnout;

    private final Set<Ability> toggledAbilities;

    private final List<DelayedTickEvent> delayedTickEvents;
    private final Map<Ability, Integer> cooldowns;
    private final Set<UUID> domains;

    private static final UUID MAX_HEALTH_UUID = UUID.fromString("72ff5080-3a82-4a03-8493-3be970039cfe");

    private static final float CURSED_ENERGY = 1000.0F;

    public SorcererData() {
        this.grade = SorcererGrade.GRADE_4;

        this.trait = Trait.NONE;
        this.technique = CursedTechnique.NONE;

        this.toggledAbilities = new HashSet<>();
        this.delayedTickEvents = new ArrayList<>();
        this.cooldowns = new HashMap<>();
        this.domains = new HashSet<>();
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
                System.out.println(ability.checkStatus(owner));
                remove.add(ability);
            } else {
                ability.run(owner);
            }
        }

        for (Ability ability : remove) {
            disableToggledAbility(owner, ability);
        }
    }

    private boolean applyModifier(LivingEntity owner, Attribute attribute, UUID identifier, String name, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = owner.getAttribute(attribute);
        AttributeModifier modifier = new AttributeModifier(identifier, name, amount, operation);

        if (instance != null) {
            AttributeModifier existing = instance.getModifier(identifier);

            if (existing != null) {
                if (existing.getAmount() != amount) {
                    instance.removeModifier(identifier);
                    instance.addTransientModifier(modifier);
                    return true;
                }
            } else {
                instance.addTransientModifier(modifier);
                return true;
            }
        }
        return false;
    }

    private void updateDomains(LivingEntity owner) {
        if (owner.level instanceof ServerLevel level) {
            Iterator<UUID> iter = this.domains.iterator();

            while (iter.hasNext()) {
                UUID identifier = iter.next();
                Entity entity = level.getEntity(identifier);

                if (!(entity instanceof DomainExpansionEntity domain) || !entity.isAlive() ||
                        entity.isRemoved() || !domain.isInsideBarrier(owner)) {
                    iter.remove();
                }
            }
        }
    }

    public void tick(LivingEntity owner) {
        for (Ability ability : this.toggledAbilities) {
            ability.run(owner);
        }

        this.updateDomains(owner);
        this.updateCooldowns();
        this.updateTickEvents(owner);
        this.updateToggledAbilities(owner);

        if (this.burnout > 0) {
            this.burnout--;
        }
        this.energy = Math.min(this.energy + 0.25F, this.getMaxEnergy());

        SorcererGrade grade = this.getGrade();

        if (this.trait == Trait.HEAVENLY_RESTRICTION) {
            if (this.applyModifier(owner, Attributes.MAX_HEALTH, MAX_HEALTH_UUID, "Max health",
                    grade.ordinal() * 20.0D, AttributeModifier.Operation.ADDITION)) {
                owner.setHealth(owner.getMaxHealth());
            }
            owner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, Math.min(4, grade.ordinal()),
                    false, false, false));
            owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2, Math.min(4, grade.ordinal()),
                    false, false, false));
            owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2, Math.min(2, grade.ordinal()),
                    false, false, false));
        } else {
            if (this.applyModifier(owner, Attributes.MAX_HEALTH, MAX_HEALTH_UUID, "Max health",
                    grade.ordinal() * 10.0D, AttributeModifier.Operation.ADDITION)) {
                owner.setHealth(owner.getMaxHealth());
            }
            owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2, Math.min(3, grade.ordinal()),
                    false, false, false));
            owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2, Math.min(1, grade.ordinal()),
                    false, false, false));
        }
    }

    public CursedTechnique getTechnique() {
        return this.technique;
    }

    @Override
    public void setTechnique(CursedTechnique technique) {
        this.technique = technique;
    }

    @Override
    public SorcererGrade getGrade() {
        return this.grade;
    }

    @Override
    public void setGrade(SorcererGrade grade) {
        this.grade = grade;
    }

    @Override
    public Trait getTrait() {
        return this.trait;
    }

    @Override
    public void setTrait(@Nullable Trait trait) {
        this.trait = trait;
    }

    @Override
    public void exorcise(LivingEntity owner, CurseGrade grade) {
        if (this.grade == SorcererGrade.SPECIAL_GRADE) return;

        SorcererGrade next = SorcererGrade.values()[this.grade.ordinal() + 1];

        this.experience += grade.getExperience();

        // If the sorcerer has enough experience and the curse exorcised was higher rank than the current rank of the sorcerer
        if (this.experience >= next.getRequiredExperience() && grade.ordinal() >= next.ordinal()) {
            this.grade = next;

            if (owner instanceof Player player) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.rank_up", JujutsuKaisen.MOD_ID), next.getComponent()));
            }
        }
    }

    public void toggleAbility(LivingEntity owner, Ability ability) {
        if (ability instanceof Ability.IToggled toggled) {
            if (owner.level.isClientSide) {
                if (this.hasToggledAbility(ability)) {
                    owner.sendSystemMessage(toggled.getDisableMessage());
                } else {
                    owner.sendSystemMessage(toggled.getEnableMessage());
                }
            }
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
    public void resetCooldowns() {
        this.cooldowns.clear();
    }

    @Override
    public void resetBurnout() {
        this.burnout = 0;
    }

    @Override
    public float getEnergy() {
        return this.energy;
    }

    @Override
    public float getMaxEnergy() {
        if (this.trait == Trait.HEAVENLY_RESTRICTION) {
            return 0.0F;
        }
        return Math.min(5000, CURSED_ENERGY * (this.getGrade().ordinal() + 1));
    }

    @Override
    public void useEnergy(float amount) {
        this.energy -= amount;
    }

    @Override
    public void setEnergy(float energy) {
        this.energy = energy;
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
        this.trait = Trait.SIX_EYES;
        this.grade = SorcererGrade.SPECIAL_GRADE;

        /*if (HelperMethods.RANDOM.nextInt(10) == 0) {
            this.trait = SpecialTrait.HEAVENLY_RESTRICTION;
        } else {
            if (HelperMethods.RANDOM.nextInt(10) == 0) {
                this.trait = SpecialTrait.SIX_EYES;
            }
            this.technique = HelperMethods.randomEnum(CursedTechnique.class);
        }*/

        this.energy = this.getMaxEnergy();
    }

    @Override
    public List<DomainExpansionEntity> getDomains(ServerLevel level) {
        List<DomainExpansionEntity> result = new ArrayList<>();

        for (UUID identifier : this.domains) {
            Entity entity = level.getEntity(identifier);

            if (entity instanceof DomainExpansionEntity domain) {
                result.add(domain);
            }
        }
        return result;
    }

    @Override
    public void onInsideDomain(DomainExpansionEntity domain) {
        this.domains.add(domain.getUUID());
    }

    public boolean hasToggledAbility(Ability ability) {
        return this.toggledAbilities.contains(ability);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("initialized", this.initialized);
        nbt.putInt("technique", this.technique.ordinal());
        nbt.putInt("trait", this.trait.ordinal());
        nbt.putFloat("experience", this.experience);
        nbt.putFloat("energy", this.energy);
        nbt.putInt("burnout", this.burnout);
        nbt.putInt("grade", this.grade.ordinal());

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

        ListTag domainsTag = new ListTag();

        for (UUID identifier : this.domains) {
            domainsTag.add(LongTag.valueOf(identifier.getLeastSignificantBits()));
            domainsTag.add(LongTag.valueOf(identifier.getMostSignificantBits()));
        }
        nbt.put("domains", domainsTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.initialized = nbt.getBoolean("initialized");
        this.technique = CursedTechnique.values()[nbt.getInt("technique")];
        this.trait = Trait.values()[nbt.getInt("trait")];
        this.experience = nbt.getFloat("experience");
        this.energy = nbt.getFloat("energy");
        this.burnout = nbt.getInt("burnout");
        this.grade = SorcererGrade.values()[nbt.getInt("grade")];

        for (Tag key : nbt.getList("toggled", Tag.TAG_STRING)) {
            this.toggledAbilities.add(JJKAbilities.getValue(new ResourceLocation(key.getAsString())));
        }

        for (Tag key : nbt.getList("cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag cooldown = (CompoundTag) key;
            this.cooldowns.put(JJKAbilities.getValue(new ResourceLocation(cooldown.getString("identifier"))),
                    cooldown.getInt("cooldown"));
        }

        ListTag domainsTag = nbt.getList("domains", Tag.TAG_COMPOUND);

        for (int i = 0; i < domainsTag.size(); i += 2) {
            if (domainsTag.get(i) instanceof LongTag least && domainsTag.get(i + 1) instanceof LongTag most) {
                this.domains.add(new UUID(least.getAsLong(), most.getAsLong()));
            }
        }
    }
}
