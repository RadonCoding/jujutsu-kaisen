package radon.jujutsu_kaisen.capability.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.Registry;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.entity.EntityTypeTest;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.CurseGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Callable;

public class SorcererData implements ISorcererData {
    private boolean initialized;

    private @Nullable CursedTechnique technique;
    private @Nullable CursedTechnique copied;
    private int copiedTimer;

    private float experience;
    private SorcererGrade grade;

    private float energy;
    private float maxEnergy;
    private float used;

    private boolean curse;

    private int burnout;

    private long lastBlackFlashTime;

    private @Nullable Ability channeled;
    private @Nullable UUID domain;

    private final Set<Ability> toggled;

    private final Set<Trait> traits;
    private final List<DelayedTickEvent> delayedTickEvents;
    private final List<ScheduledTickEvent> scheduledTickEvents;
    private final Map<Ability, Integer> cooldowns;
    private final Map<Ability, Integer> durations;
    private final Set<UUID> domains;
    private final Set<UUID> summons;
    private final Set<ResourceLocation> tamed;

    private final Set<Ability.Classification> adapted;
    private final Map<Ability.Classification, Integer> adapting;

    private static final UUID MAX_HEALTH_UUID = UUID.fromString("72ff5080-3a82-4a03-8493-3be970039cfe");

    private static final float ENERGY_AMOUNT = 0.25F;
    private static final int REQUIRED_ADAPTATION = 3;

    public SorcererData() {
        this.setGrade(SorcererGrade.GRADE_4);

        this.toggled = new HashSet<>();
        this.traits = new HashSet<>();
        this.delayedTickEvents = new ArrayList<>();
        this.scheduledTickEvents = new ArrayList<>();
        this.cooldowns = new HashMap<>();
        this.durations = new HashMap<>();
        this.domains = new HashSet<>();
        this.summons = new HashSet<>();
        this.tamed = new HashSet<>();

        this.adapted = new HashSet<>();
        this.adapting = new HashMap<>();
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

    private void updateDurations(LivingEntity owner) {
        Iterator<Map.Entry<Ability, Integer>> iter = this.durations.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Ability, Integer> entry = iter.next();

            Ability ability = entry.getKey();
            int remaining = entry.getValue();

            if (remaining > 0) {
                this.durations.put(entry.getKey(), --remaining);
            } else {
                if (this.hasToggled(ability)) {
                    this.toggle(owner, ability);
                }
                iter.remove();
            }
        }
    }

    private void updateTickEvents() {
        Iterator<DelayedTickEvent> delayed = this.delayedTickEvents.iterator();

        while (delayed.hasNext()) {
            DelayedTickEvent current = delayed.next();

            current.tick();

            if (current.run()) {
                delayed.remove();
            }
        }

        Iterator<ScheduledTickEvent> scheduled = this.scheduledTickEvents.iterator();

        while (scheduled.hasNext()) {
            ScheduledTickEvent current = scheduled.next();

            current.tick();

            if (current.run()) {
                scheduled.remove();
            }
        }
    }

    private void updateToggled(LivingEntity owner) {
        List<Ability> remove = new ArrayList<>();

        for (Ability ability : this.toggled) {
            Ability.Status status = ability.checkStatus(owner);

            if (status == Ability.Status.SUCCESS) {
                ability.run(owner);
            } else if (status == Ability.Status.UNUSUABLE || !((Ability.IToggled) ability).isPassive()) {
                remove.add(ability);
            }
        }

        for (Ability ability : remove) {
            this.toggle(owner, ability);
        }
    }

    private void updateChanneled(LivingEntity owner) {
        if (this.channeled != null) {
            if (this.channeled.checkStatus(owner) == Ability.Status.SUCCESS) {
                this.channeled.run(owner);
            } else {
                this.channel(owner, this.channeled);
            }
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

                if (!(entity instanceof DomainExpansionEntity) || !entity.isAlive() ||
                        entity.isRemoved() || !((DomainExpansionEntity) entity).isInsideBarrier(owner)) {
                    iter.remove();
                }
            }
        }
    }

    private void updateCopied(LivingEntity owner) {
        if (this.copied != null) {
            if (++this.copiedTimer == 5 * 60 * 20) {
                if (owner.level.isClientSide) {
                    owner.sendSystemMessage(Component.translatable(String.format("chat.%s.copy_expire", JujutsuKaisen.MOD_ID)));
                }
                this.copied = null;
                this.copiedTimer = 0;
            }
        }
    }

    private void giveAdvancement(ServerPlayer player, String name) {
        MinecraftServer server = player.getServer();
        assert server != null;
        Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(JujutsuKaisen.MOD_ID,
                String.format("%s/%s", JujutsuKaisen.MOD_ID, name)));

        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);

            if (!progress.isDone()) {
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criterion);
                }
            }
        }
    }

    private void checkAdvancements(ServerPlayer player) {
        if (this.traits.contains(Trait.SIX_EYES)) this.giveAdvancement(player, "six_eyes");
        if (this.traits.contains(Trait.HEAVENLY_RESTRICTION)) this.giveAdvancement(player, "heavenly_restriction");
        if (this.traits.contains(Trait.REVERSE_CURSED_TECHNIQUE)) this.giveAdvancement(player, "reverse_cursed_technique");
        if (this.traits.contains(Trait.SIMPLE_DOMAIN)) this.giveAdvancement(player, "simple_domain");
        if (this.traits.contains(Trait.DOMAIN_EXPANSION)) this.giveAdvancement(player, "domain_expansion");
        if (this.traits.contains(Trait.STRONGEST)) this.giveAdvancement(player, "strongest");
    }

    public void tick(LivingEntity owner) {
        this.updateDomains(owner);
        this.updateCopied(owner);

        this.updateCooldowns();
        this.updateDurations(owner);
        this.updateTickEvents();
        this.updateToggled(owner);
        this.updateChanneled(owner);

        if (this.used >= 5000.0F) {
            this.traits.add(Trait.DOMAIN_EXPANSION);
        }
        if (this.used >= 2500.0F) {
            this.traits.add(Trait.SIMPLE_DOMAIN);
        }

        if (owner instanceof ServerPlayer player) {
            if (!this.initialized) {
                this.initialized = true;
                this.generate(player);
            }
            this.checkAdvancements(player);
        }

        if (this.burnout > 0) {
            this.burnout--;
        }

        if (owner instanceof Player player) {
            FoodData data = player.getFoodData();
            this.energy = Math.min(this.energy + (ENERGY_AMOUNT * (data.getFoodLevel() / 20.0F)), this.getMaxEnergy());
        }

        SorcererGrade grade = this.getGrade();

        if (this.technique == CursedTechnique.DISASTER_FLAMES) {
            owner.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2, 0, false, false, false));
        }

        if (this.traits.contains(Trait.SIX_EYES) && !owner.getItemBySlot(EquipmentSlot.HEAD).is(JJKItems.SATORU_BLINDFOLD.get())) {
            owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 2, 0, false, false, false));
        }

        if (this.traits.contains(Trait.HEAVENLY_RESTRICTION)) {
            if (this.applyModifier(owner, Attributes.MAX_HEALTH, MAX_HEALTH_UUID, "Max health",
                    Math.ceil((grade.ordinal() * 10.0D) / 20) * 20, AttributeModifier.Operation.ADDITION)) {
                owner.setHealth(owner.getMaxHealth());
            }
            owner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, Mth.floor(4.0F * ((float) (this.grade.ordinal() + 1) / SorcererGrade.values().length)),
                    false, false, false));
            owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2, Mth.floor(4.0F * ((float) (this.grade.ordinal() + 1) / SorcererGrade.values().length)),
                    false, false, false));
            owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2, Mth.floor(3.0F * ((float) (this.grade.ordinal() + 1) / SorcererGrade.values().length)),
                    false, false, false));
            owner.addEffect(new MobEffectInstance(JJKEffects.UNDETECTABLE.get(), 2, 0,
                    false, false, false));
        } else {
            if (this.applyModifier(owner, Attributes.MAX_HEALTH, MAX_HEALTH_UUID, "Max health",
                    Math.ceil((grade.ordinal() * 5.0D) / 20) * 20, AttributeModifier.Operation.ADDITION)) {
                owner.setHealth(owner.getMaxHealth());
            }
            owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2, Mth.floor(2.0F * ((float) (this.grade.ordinal() + 1) / SorcererGrade.values().length)),
                    false, false, false));
            owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2, Mth.floor((this.traits.contains(Trait.STRONGEST) ? 3.0F : 2.0F)
                    * ((float) (this.grade.ordinal() + 1) / SorcererGrade.values().length)), false, false, false));
        }
    }

    public @Nullable CursedTechnique getTechnique() {
        return this.technique;
    }

    @Override
    public void setTechnique(@Nullable CursedTechnique technique) {
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
    public boolean hasTrait(Trait trait) {
        return this.traits.contains(trait);
    }

    @Override
    public void addTrait(Trait trait) {
        this.traits.add(trait);
    }

    @Override
    public void addTraits(List<Trait> traits) {
        this.traits.addAll(traits);
    }

    @Override
    public void removeTrait(Trait trait) {
        this.traits.remove(trait);
    }

    @Override
    public void setCurse(boolean curse) {
        this.curse = curse;
    }

    @Override
    public boolean isCurse() {
        return this.curse;
    }

    @Override
    public void exorcise(LivingEntity owner, CurseGrade grade) {
        if (this.grade == SorcererGrade.SPECIAL_GRADE) return;

        SorcererGrade next = SorcererGrade.values()[this.grade.ordinal() + 1];

        this.experience += grade.getExperience();

        if (owner instanceof Player player) {
            player.sendSystemMessage(Component.translatable(String.format("chat.%s.exorcise", JujutsuKaisen.MOD_ID), grade.getExperience(),
                    this.experience, next.getRequiredExperience()));
        }

        // If the sorcerer has enough experience and the curse/sorcerer exorcised was higher rank than the current rank of the curse/sorcerer
        if (this.experience >= next.getRequiredExperience() && grade.ordinal() >= next.ordinal()) {
            this.setGrade(next);

            if (owner instanceof Player player) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.rank_up", JujutsuKaisen.MOD_ID), next.getName()));
            }
        }
    }

    public void toggle(LivingEntity owner, Ability ability) {
        if (owner.level.isClientSide) {
            if (((Ability.IToggled) ability).shouldLog()) {
                if (this.hasToggled(ability)) {
                    owner.sendSystemMessage(((Ability.IToggled) ability).getDisableMessage());
                } else {
                    owner.sendSystemMessage(((Ability.IToggled) ability).getEnableMessage());
                }
            }
        }
        if (this.toggled.contains(ability)) {
            this.toggled.remove(ability);
            ((Ability.IToggled) ability).onDisabled(owner);
        } else {
            this.toggled.add(ability);
            ((Ability.IToggled) ability).onEnabled(owner);
        }
    }

    @Override
    public void clearToggled() {
        this.toggled.clear();
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
    public void addDuration(LivingEntity owner, Ability ability) {
        this.durations.put(ability, ((Ability.IToggled) ability).getRealDuration(owner));
    }

    @Override
    public int getRemaining(Ability ability) {
        if (!this.durations.containsKey(ability)) {
            return 0;
        }
        return this.durations.get(ability);
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
        if (this.traits.contains(Trait.HEAVENLY_RESTRICTION)) {
            return 0.0F;
        }
        return this.energy;
    }

    @Override
    public float getMaxEnergy() {
        if (this.maxEnergy == 0.0F) {
            this.maxEnergy = ConfigHolder.SERVER.maxCursedEnergyDefault.get();
        }
        return this.maxEnergy * ((float) (this.grade.ordinal() + 1) / SorcererGrade.values().length);
    }

    @Override
    public void setMaxEnergy(float maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    @Override
    public void useEnergy(float amount) {
        this.energy -= amount;
        this.used += amount;
    }

    @Override
    public void setEnergy(float energy) {
        this.energy = energy;
    }

    @Override
    public void onBlackFlash(LivingEntity owner) {
        this.lastBlackFlashTime = owner.level.getGameTime();

        if (owner instanceof ServerPlayer player) {
            this.giveAdvancement(player, "black_flash");
        }
    }

    @Override
    public long getLastBlackFlashTime() {
        return this.lastBlackFlashTime;
    }

    @Override
    public boolean isInZone(LivingEntity owner) {
        return ((owner.level.getGameTime() - this.lastBlackFlashTime) / 20) < 5;
    }

    @Override
    public void delayTickEvent(Runnable task, int delay) {
        this.delayedTickEvents.add(new DelayedTickEvent(task, delay));
    }

    @Override
    public void scheduleTickEvent(Callable<Boolean> task, int duration) {
        this.scheduledTickEvents.add(new ScheduledTickEvent(task, duration));
    }

    @Override
    public void setCopied(@Nullable CursedTechnique technique) {
        this.copied = technique;
        this.copiedTimer = 0;
    }

    @Override
    public @Nullable CursedTechnique getCopied() {
        return this.copied;
    }

    @Override
    public void channel(LivingEntity owner, @Nullable Ability ability) {
        this.channeled = this.channeled == ability ? null : ability;
    }

    @Override
    public boolean isChanneling(Ability ability) {
        return this.channeled == ability;
    }

    @Override
    public void addSummon(Entity entity) {
        this.summons.add(entity.getUUID());
    }

    @Override
    public List<Entity> getSummons(ServerLevel level) {
        List<Entity> entities = new ArrayList<>();

        for (UUID identifier : this.summons) {
            Entity entity = level.getEntity(identifier);

            if (entity == null) continue;

            entities.add(entity);
        }
        return entities;
    }

    @Override
    public <T extends Entity> @Nullable T getSummonByClass(ServerLevel level, Class<T> clazz) {
        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        for (UUID identifier : this.summons) {
            Entity entity = level.getEntity(identifier);

            if (entity == null) continue;

            T summon = test.tryCast(entity);

            if (summon != null) {
                return summon;
            }
        }
        return null;
    }

    @Override
    public <T extends Entity> void unsummonByClass(ServerLevel level, Class<T> clazz) {
        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        Iterator<UUID> iter = this.summons.iterator();

        while (iter.hasNext()) {
            UUID identifier = iter.next();

            Entity entity = level.getEntity(identifier);

            if (entity == null) continue;

            T summon = test.tryCast(entity);

            if (summon != null) {
                summon.discard();
                iter.remove();
            }
        }
    }

    @Override
    public <T extends Entity> boolean hasSummonOfClass(ServerLevel level, Class<T> clazz) {
        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        for (UUID identifier : this.summons) {
            Entity entity = level.getEntity(identifier);

            if (entity == null) continue;

            T summon = test.tryCast(entity);

            if (summon != null) {
                return true;
            }
        }
        return false;
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
    public void setDomain(DomainExpansionEntity domain) {
        this.domain = domain.getUUID();
    }

    @Override
    public DomainExpansionEntity getDomain(ServerLevel level) {
        if (this.domain != null) {
            Entity entity = level.getEntity(this.domain);

            if (entity instanceof DomainExpansionEntity) {
                return (DomainExpansionEntity) entity;
            }
        }
        return null;
    }

    @Override
    public Set<Ability.Classification> getAdapted() {
        return this.adapted;
    }

    @Override
    public void adaptAll(Set<Ability.Classification> adaptations) {
        this.adapted.addAll(adaptations);
    }

    private @Nullable Ability.Classification getClassification(DamageSource source) {
        Ability ability;

        if (source instanceof JJKDamageSources.JujutsuDamageSource jujutsu && (ability = jujutsu.getAbility()) != null) {
            return ability.getClassification();
        }
        return null;
    }

    @Override
    public boolean isAdaptedTo(DamageSource source) {
        Ability.Classification classification = this.getClassification(source);
        return classification != null && this.adapted.contains(classification);
    }

    @Override
    public boolean isAdaptedTo(Ability ability) {
        return this.adapted.contains(ability.getClassification());
    }

    @Override
    public boolean tryAdapt(DamageSource source) {
        Ability.Classification classification = this.getClassification(source);

        if (classification == null) return false;

        if (!this.adapting.containsKey(classification)) {
            this.adapting.put(classification, 1);
        } else {
            int stage = this.adapting.get(classification);

            stage++;

            if (stage >= REQUIRED_ADAPTATION) {
                this.adapting.remove(classification);
                this.adapted.add(classification);
                return true;
            } else {
                this.adapting.put(classification, stage);
            }
        }
        return false;
    }

    @Override
    public void generate(ServerPlayer player) {
        this.initialized = true;

        this.traits.clear();

        if (HelperMethods.RANDOM.nextInt(10) == 0) {
            this.addTrait(Trait.HEAVENLY_RESTRICTION);
        } else {
            if (HelperMethods.RANDOM.nextInt(10) == 0) {
                this.addTrait(Trait.SIX_EYES);
            }
            this.technique = HelperMethods.randomEnum(CursedTechnique.class);
            this.curse = HelperMethods.RANDOM.nextInt(5) == 0;

            assert this.technique != null;

            player.sendSystemMessage(Component.translatable(String.format("chat.%s.technique", JujutsuKaisen.MOD_ID), this.technique.getName()));

            if (this.curse) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.curse", JujutsuKaisen.MOD_ID)));
            } else {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.sorcerer", JujutsuKaisen.MOD_ID)));
            }
        }
        this.energy = this.getMaxEnergy();

        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(this.serializeNBT()), player);
    }

    @Override
    public List<DomainExpansionEntity> getDomains(ServerLevel level) {
        List<DomainExpansionEntity> result = new ArrayList<>();

        for (UUID identifier : this.domains) {
            Entity entity = level.getEntity(identifier);

            if (entity instanceof DomainExpansionEntity) {
                result.add((DomainExpansionEntity) entity);
            }
        }
        return result;
    }

    @Override
    public void onInsideDomain(DomainExpansionEntity domain) {
        this.domains.add(domain.getUUID());
    }

    public boolean hasToggled(Ability ability) {
        return this.toggled.contains(ability);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("initialized", this.initialized);

        if (this.technique != null) {
            nbt.putInt("technique", this.technique.ordinal());
        }
        if (this.copied != null) {
            nbt.putInt("copied", this.copied.ordinal());
        }
        nbt.putInt("copied_timer", this.copiedTimer);
        nbt.putFloat("experience", this.experience);
        nbt.putFloat("energy", this.energy);
        nbt.putFloat("max_energy", this.maxEnergy);
        nbt.putFloat("used", this.used);
        nbt.putBoolean("curse", this.curse);
        nbt.putInt("burnout", this.burnout);
        nbt.putInt("grade", this.grade.ordinal());

        if (this.domain != null) {
            nbt.putUUID("domain", this.domain);
        }

        if (this.channeled != null) {
            nbt.putString("channeled", JJKAbilities.getKey(this.channeled).toString());
        }

        ListTag toggledTag = new ListTag();

        for (Ability ability : this.toggled) {
            toggledTag.add(StringTag.valueOf(JJKAbilities.getKey(ability).toString()));
        }
        nbt.put("toggled", toggledTag);

        ListTag traitsTag = new ListTag();

        for (Trait trait : this.traits) {
            traitsTag.add(IntTag.valueOf(trait.ordinal()));
        }
        nbt.put("traits", traitsTag);

        ListTag cooldownsTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.cooldowns.entrySet()) {
            CompoundTag cooldown = new CompoundTag();
            cooldown.putString("identifier", JJKAbilities.getKey(entry.getKey()).toString());
            cooldown.putInt("cooldown", entry.getValue());
            cooldownsTag.add(cooldown);
        }
        nbt.put("cooldowns", cooldownsTag);

        ListTag durationsTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.durations.entrySet()) {
            CompoundTag cooldown = new CompoundTag();
            cooldown.putString("identifier", JJKAbilities.getKey(entry.getKey()).toString());
            cooldown.putInt("duration", entry.getValue());
            durationsTag.add(cooldown);
        }
        nbt.put("durations", durationsTag);

        ListTag domainsTag = new ListTag();

        for (UUID identifier : this.domains) {
            domainsTag.add(LongTag.valueOf(identifier.getLeastSignificantBits()));
            domainsTag.add(LongTag.valueOf(identifier.getMostSignificantBits()));
        }
        nbt.put("domains", domainsTag);

        ListTag summonsTag = new ListTag();

        for (UUID identifier : this.summons) {
            summonsTag.add(LongTag.valueOf(identifier.getLeastSignificantBits()));
            summonsTag.add(LongTag.valueOf(identifier.getMostSignificantBits()));
        }
        nbt.put("summons", summonsTag);

        ListTag tamedTag = new ListTag();

        for (ResourceLocation key : this.tamed) {
            tamedTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("tamed", tamedTag);

        ListTag adaptedTag = new ListTag();

        for (Ability.Classification classification : this.adapted) {
            adaptedTag.add(IntTag.valueOf(classification.ordinal()));
        }
        nbt.put("adapted", adaptedTag);

        ListTag adaptingTag = new ListTag();

        for (Map.Entry<Ability.Classification, Integer> entry : this.adapting.entrySet()) {
            CompoundTag adaptation = new CompoundTag();
            adaptation.putInt("stage", entry.getValue());
            adaptation.putInt("classification", entry.getKey().ordinal());
            adaptingTag.add(adaptation);

        }
        nbt.put("adapting", adaptingTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.initialized = nbt.getBoolean("initialized");

        if (nbt.contains("technique")) {
            this.technique = CursedTechnique.values()[nbt.getInt("technique")];
        }
        if (nbt.contains("copied")) {
            this.copied = CursedTechnique.values()[nbt.getInt("copied")];
        }
        this.copiedTimer = nbt.getInt("copied_timer");
        this.experience = nbt.getFloat("experience");
        this.energy = nbt.getFloat("energy");
        this.maxEnergy = nbt.getFloat("max_energy");
        this.used = nbt.getFloat("used");
        this.curse = nbt.getBoolean("curse");
        this.burnout = nbt.getInt("burnout");
        this.grade = SorcererGrade.values()[nbt.getInt("grade")];

        if (nbt.hasUUID("domain")) {
            this.domain = nbt.getUUID("domain");
        }

        if (nbt.contains("channeled")) {
            this.channeled = JJKAbilities.getValue(new ResourceLocation(nbt.getString("channeled")));
        }

        this.toggled.clear();

        for (Tag key : nbt.getList("toggled", Tag.TAG_STRING)) {
            this.toggled.add(JJKAbilities.getValue(new ResourceLocation(key.getAsString())));
        }

        this.traits.clear();

        for (Tag key : nbt.getList("traits", Tag.TAG_INT)) {
            if (key instanceof IntTag tag) {
                this.traits.add(Trait.values()[tag.getAsInt()]);
            }
        }

        this.cooldowns.clear();

        for (Tag key : nbt.getList("cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag cooldown = (CompoundTag) key;
            this.cooldowns.put(JJKAbilities.getValue(new ResourceLocation(cooldown.getString("identifier"))),
                    cooldown.getInt("cooldown"));
        }

        this.durations.clear();

        for (Tag key : nbt.getList("duration", Tag.TAG_COMPOUND)) {
            CompoundTag durations = (CompoundTag) key;
            this.durations.put(JJKAbilities.getValue(new ResourceLocation(durations.getString("identifier"))),
                    durations.getInt("duration"));
        }

        this.domains.clear();

        ListTag domainsTag = nbt.getList("domains", Tag.TAG_LONG);

        for (int i = 0; i < domainsTag.size(); i += 2) {
            if (domainsTag.get(i) instanceof LongTag least && domainsTag.get(i + 1) instanceof LongTag most) {
                this.domains.add(new UUID(least.getAsLong(), most.getAsLong()));
            }
        }

        this.summons.clear();

        ListTag summonsTag = nbt.getList("summons", Tag.TAG_LONG);

        for (int i = 0; i < summonsTag.size(); i += 2) {
            if (summonsTag.get(i) instanceof LongTag least && summonsTag.get(i + 1) instanceof LongTag most) {
                this.summons.add(new UUID(least.getAsLong(), most.getAsLong()));
            }
        }

        this.tamed.clear();

        for (Tag key : nbt.getList("tamed", Tag.TAG_STRING)) {
            this.tamed.add(new ResourceLocation(key.getAsString()));
        }

        this.adapted.clear();

        for (Tag key : nbt.getList("adapted", Tag.TAG_INT)) {
            this.adapted.add(Ability.Classification.values()[((IntTag) key).getAsInt()]);
        }

        this.adapting.clear();

        for (Tag key : nbt.getList("adapting", Tag.TAG_COMPOUND)) {
            CompoundTag adaptation = (CompoundTag) key;
            this.adapting.put(Ability.Classification.values()[adaptation.getInt("classification")], adaptation.getInt("stage"));
        }
    }
}
