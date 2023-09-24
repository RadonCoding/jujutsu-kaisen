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
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.*;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.WheelEntity;
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

    private @Nullable CursedTechnique additional;

    private final Set<CursedTechnique> copied;
    private @Nullable CursedTechnique currentCopied;

    private final Set<CursedTechnique> absorbed;
    private @Nullable CursedTechnique currentAbsorbed;

    private CursedEnergyNature nature;

    private float experience;
    private SorcererGrade grade;

    private float energy;
    private float maxEnergy;
    private float used;

    private JujutsuType type;

    private int burnout;

    private long lastBlackFlashTime;

    private @Nullable Ability channeled;
    private int charge;
    private @Nullable UUID domain;

    private final Set<Ability> toggled;

    private final Set<Trait> traits;
    private final List<DelayedTickEvent> delayedTickEvents;
    private final List<ScheduledTickEvent> scheduledTickEvents;
    private final Map<Ability, Integer> cooldowns;
    private final Map<Ability, Integer> durations;
    private final Set<UUID> domains;
    private final Set<UUID> summons;

    // Ten shadows
    private final Set<ResourceLocation> tamed;
    private final Set<ResourceLocation> dead;
    private final List<ItemStack> shadowInventory;
    private final Set<Ability> adapted;
    private final Map<Ability, Integer> adapting;
    private TenShadowsMode mode;

    private final Map<ResourceLocation, Integer> curses;

    private static final UUID MAX_HEALTH_UUID = UUID.fromString("72ff5080-3a82-4a03-8493-3be970039cfe");
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("4979087e-da76-4f8a-93ef-6e5847bfa2ee");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("a2aef906-ed31-49e8-a56c-decccbfa2c1f");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("9fe023ca-f22b-4429-a5e5-c099387d5441");

    private static final float ENERGY_AMOUNT = 0.25F;
    private static final int REQUIRED_ADAPTATION = 30 * 20;
    private static final int ADAPTATION_STEP = 5 * 20;

    public SorcererData() {
        this.type = JujutsuType.SORCERER;

        this.copied = new LinkedHashSet<>();
        this.absorbed = new LinkedHashSet<>();

        this.nature = CursedEnergyNature.BASIC;

        this.grade = SorcererGrade.GRADE_4;
        this.mode = TenShadowsMode.SUMMON;

        this.lastBlackFlashTime = -1;

        this.toggled = new HashSet<>();
        this.traits = new HashSet<>();
        this.delayedTickEvents = new ArrayList<>();
        this.scheduledTickEvents = new ArrayList<>();
        this.cooldowns = new HashMap<>();
        this.durations = new HashMap<>();
        this.domains = new HashSet<>();
        this.summons = new HashSet<>();

        this.tamed = new HashSet<>();
        this.dead = new HashSet<>();

        this.adapted = new HashSet<>();
        this.adapting = new HashMap<>();

        this.curses = new LinkedHashMap<>();

        this.shadowInventory = new ArrayList<>();
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
                if (ability instanceof Ability.IToggled) {
                    if (this.hasToggled(ability)) {
                        this.toggle(owner, ability);
                    }
                } else if (ability instanceof Ability.IChannelened) {
                    if (this.isChanneling(ability)) {
                        this.channel(owner, null);
                    }
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
            Ability.Status status = this.channeled.checkStatus(owner);

            if (status == Ability.Status.SUCCESS) {
                this.channeled.run(owner);
            } else {
                this.channel(owner, this.channeled);
            }
            this.charge++;
        } else {
            this.charge = 0;
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

    private void removeModifier(LivingEntity owner, Attribute attribute, UUID identifier) {
        AttributeInstance instance = owner.getAttribute(attribute);

        if (instance != null) {
            instance.removeModifier(identifier);
        }
    }

    private void updateDomains(LivingEntity owner) {
        if (owner.level instanceof ServerLevel level) {
            Iterator<UUID> iter = this.domains.iterator();

            while (iter.hasNext()) {
                UUID identifier = iter.next();
                Entity entity = level.getEntity(identifier);

                if (!(entity instanceof DomainExpansionEntity) || !entity.isAlive() ||
                        entity.isRemoved() || !((DomainExpansionEntity) entity).isInsideBarrier(owner.blockPosition())) {
                    iter.remove();
                }
            }
        }
    }

    private void updateSummons(LivingEntity owner) {
        if (owner.level instanceof ServerLevel level) {
            Iterator<UUID> iter = this.summons.iterator();

            while (iter.hasNext()) {
                UUID identifier = iter.next();

                Entity entity = level.getEntity(identifier);

                if (entity == null || !entity.isAlive() || entity.isRemoved()) {
                    iter.remove();
                }
            }
        }
    }

    private void updateDomain(LivingEntity owner) {
        if (this.domain == null) return;

        if (owner.level instanceof ServerLevel level) {
            Entity entity = level.getEntity(this.domain);

            if (entity == null || !entity.isAlive() || entity.isRemoved()) {
                this.domain = null;
            }
        }
    }

    private void updateAdaptation(ServerLevel level) {
        if (this.toggled.contains(JJKAbilities.WHEEL.get())) {
            Iterator<Map.Entry<Ability, Integer>> iter = this.adapting.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<Ability, Integer> entry = iter.next();

                int timer = entry.getValue();

                if (++timer >= REQUIRED_ADAPTATION) {
                    iter.remove();

                    this.adapted.add(entry.getKey());

                    WheelEntity wheel = this.getSummonByClass(level, WheelEntity.class);

                    if (wheel != null) {
                        wheel.spin();
                    }
                }
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
        if (this.traits.contains(Trait.CURSED_WOMB)) this.giveAdvancement(player, "cursed_womb");
        if (this.traits.contains(Trait.EVOLVED_CURSE)) this.giveAdvancement(player, "evolved_curse");
    }

    public void tick(LivingEntity owner) {
        this.updateDomain(owner);
        this.updateDomains(owner);
        this.updateSummons(owner);

        this.updateCooldowns();
        this.updateDurations(owner);
        this.updateTickEvents();
        this.updateToggled(owner);
        this.updateChanneled(owner);

        if (owner.level instanceof ServerLevel level) {
            this.updateAdaptation(level);
        }

        if (this.used >= 10000.0F) {
            this.traits.add(Trait.DOMAIN_EXPANSION);
        }
        if (this.used >= 5000.0F) {
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

        this.energy = Math.min(this.energy + (ENERGY_AMOUNT * (owner instanceof Player player ? (player.getFoodData().getFoodLevel() / 20.0F) : 1.0F) *
                (this.traits.contains(Trait.EVOLVED_CURSE) ? 1.5F : 1.0F)), this.getMaxEnergy());

        SorcererGrade grade = this.getGrade();

        if (this.traits.contains(Trait.SIX_EYES) && !owner.getItemBySlot(EquipmentSlot.HEAD).is(JJKItems.SATORU_BLINDFOLD.get())) {
            owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
        }

        if (this.traits.contains(Trait.HEAVENLY_RESTRICTION)) {
            double health = Math.ceil((Math.log(grade.ordinal() + 1) * (this.traits.contains(Trait.STRONGEST) ? 80.0D : 40.0D)) / 20) * 20;

            if (this.applyModifier(owner, Attributes.MAX_HEALTH, MAX_HEALTH_UUID, "Max health", health, AttributeModifier.Operation.ADDITION)) {
                owner.setHealth(owner.getMaxHealth());
            }

            double damage = this.grade.getPower(owner) * (this.traits.contains(Trait.STRONGEST) ? 6.0D : 3.0D);
            this.applyModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", damage, AttributeModifier.Operation.ADDITION);

            double speed = this.grade.getPower(owner) * (this.traits.contains(Trait.STRONGEST) ? 1.0D : 0.5D);
            this.applyModifier(owner, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID, "Attack speed", speed, AttributeModifier.Operation.ADDITION);

            double movement = this.grade.getPower(owner) * (this.traits.contains(Trait.STRONGEST) ? 0.1D : 0.05D);
            this.applyModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed", movement, AttributeModifier.Operation.ADDITION);

            float resistanceMultiplier = this.traits.contains(Trait.STRONGEST) ? 2.0F : 1.0F;
            int resistance = Mth.floor(resistanceMultiplier * ((float) (this.grade.ordinal() + 1) / SorcererGrade.values().length));
            owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2, resistance, false, false, false));

            owner.addEffect(new MobEffectInstance(JJKEffects.UNDETECTABLE.get(), 2, 0, false, false, false));
        } else {
            this.removeModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID);

            double health = Math.ceil((Math.log(grade.ordinal() + 1)
                    * (this.traits.contains(Trait.STRONGEST) ? 40.0D : 20.0D)
                    * (this.traits.contains(Trait.EVOLVED_CURSE) ? 1.25D : 1.0D)
                    * (this.traits.contains(Trait.CURSED_WOMB) ? 0.5D : 1.0D)) / 20) * 20;

            if (this.applyModifier(owner, Attributes.MAX_HEALTH, MAX_HEALTH_UUID, "Max health", health, AttributeModifier.Operation.ADDITION)) {
                owner.setHealth(owner.getMaxHealth());
            }

            double damage = this.grade.getPower(owner) * (this.traits.contains(Trait.STRONGEST) ? 3.0D : 1.5D) / 2.0F;

            if (this.traits.contains(Trait.CURSED_WOMB)) {
                damage *= 0.5D;
            } else if (this.traits.contains(Trait.EVOLVED_CURSE)) {
                damage *= 1.25D;
            }
            this.applyModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", damage, AttributeModifier.Operation.ADDITION);

            float resistanceMultiplier = this.traits.contains(Trait.STRONGEST) ? 1.0F : 0.0F;
            int resistance = Mth.floor(resistanceMultiplier * ((float) (this.grade.ordinal() + 1) / SorcererGrade.values().length));
            owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2, resistance, false, false, false));
        }
    }

    public @Nullable CursedTechnique getTechnique() {
        return this.technique;
    }

    @Override
    public boolean hasTechnique(CursedTechnique technique) {
        return this.technique == technique || this.additional == technique || this.getCurrentCopied() == technique || this.currentAbsorbed == technique;
    }

    @Override
    public void setTechnique(@Nullable CursedTechnique technique) {
        this.technique = technique;
    }

    @Override
    public CursedEnergyNature getNature() {
        return this.nature;
    }

    @Override
    public void setNature(CursedEnergyNature nature) {
        this.nature = nature;
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
    public Set<Trait> getTraits() {
        return this.traits;
    }

    @Override
    public void setType(JujutsuType type) {
        this.type = type;
    }

    @Override
    public JujutsuType getType() {
        return this.type;
    }

    @Override
    public void exorcise(LivingEntity owner, SorcererGrade grade) {
        if (this.grade == SorcererGrade.SPECIAL_GRADE) return;

        SorcererGrade next = SorcererGrade.values()[this.grade.ordinal() + 1];

        this.experience += grade.getReward();

        if (owner instanceof Player player) {
            player.sendSystemMessage(Component.translatable(String.format("chat.%s.exorcise_%s", JujutsuKaisen.MOD_ID,
                            this.type == JujutsuType.SORCERER ? "curse" : "sorcerer"), grade.getReward(), this.experience, next.getRequiredExperience()));
        }

        // If the owner has enough experience and the curse/sorcerer exorcised was higher or equal to the next rank
        if (this.experience >= next.getRequiredExperience() && grade.ordinal() >= next.ordinal()) {
            this.setGrade(next);

            if (owner instanceof Player player) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.rank_up", JujutsuKaisen.MOD_ID), next.getName()));
            }
        }
    }

    @Override
    public void consume(LivingEntity owner, SorcererGrade grade) {
        if (this.grade == SorcererGrade.SPECIAL_GRADE) return;

        SorcererGrade next = SorcererGrade.values()[this.grade.ordinal() + 1];

        this.experience += grade.getReward();

        if (owner instanceof Player player) {
            player.sendSystemMessage(Component.translatable(String.format("chat.%s.consume", JujutsuKaisen.MOD_ID), grade.getReward(),
                    this.experience, next.getRequiredExperience()));
        }

        // If the owner has enough experience and the cursed object consumed was higher or equal to the next rank
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
    public Set<Ability> getToggled() {
        return this.toggled;
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
        this.durations.put(ability, ((Ability.IDurationable) ability).getRealDuration(owner));
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
        return this.maxEnergy * ((float) (this.grade.ordinal() + 1) / SorcererGrade.values().length)
                * (this.traits.contains(Trait.STRONGEST) ? 1.5F : 1.0F)
                * (this.traits.contains(Trait.EVOLVED_CURSE) ? 1.5F : 1.0F)
                * (this.traits.contains(Trait.CURSED_WOMB) ? 0.5F : 1.0F);
    }

    @Override
    public void setMaxEnergy(float maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    @Override
    public void useEnergy(float amount) {
        this.energy -= amount;
    }

    @Override
    public void addEnergy(float amount) {
        this.energy += amount;
    }

    @Override
    public void setEnergy(float energy) {
        this.energy = energy;
    }

    @Override
    public void addUsed(float amount) {
        this.used += amount;
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
    public void resetBlackFlash() {
        this.lastBlackFlashTime = -1;
    }

    @Override
    public boolean isInZone(LivingEntity owner) {
        return this.lastBlackFlashTime != -1 && ((owner.level.getGameTime() - this.lastBlackFlashTime) / 20) < (5 * 60);
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
    public void copy(@Nullable CursedTechnique technique) {
        this.copied.add(technique);
    }

    @Override
    public Set<CursedTechnique> getCopied() {
        if (!this.hasToggled(JJKAbilities.RIKA.get()) || !this.hasTechnique(CursedTechnique.RIKA)) {
            return Set.of();
        }
        return this.copied;
    }

    @Override
    public void setCurrentCopied(@Nullable CursedTechnique technique) {
        this.currentCopied = this.currentCopied == technique ? null : technique;
    }

    @Override
    public @Nullable CursedTechnique getCurrentCopied() {
        if (!this.toggled.contains(JJKAbilities.RIKA.get())) {
            return null;
        }
        return this.currentCopied;
    }

    @Override
    public void absorb(@Nullable CursedTechnique technique) {
        this.absorbed.add(technique);
    }

    @Override
    public void unabsorb(CursedTechnique technique) {
        this.absorbed.remove(technique);
        this.currentAbsorbed = null;
    }

    @Override
    public Set<CursedTechnique> getAbsorbed() {
        if (!this.hasTechnique(CursedTechnique.CURSE_MANIPULATION)) {
            return Set.of();
        }
        return this.absorbed;
    }

    @Override
    public void setCurrentAbsorbed(@Nullable CursedTechnique technique) {
        this.currentAbsorbed = this.currentAbsorbed == technique ? null : technique;
    }

    @Override
    public @Nullable CursedTechnique getCurrentAbsorbed() {
        if (!this.hasTechnique(CursedTechnique.CURSE_MANIPULATION)) {
            return null;
        }
        return this.currentAbsorbed;
    }

    @Override
    public void channel(LivingEntity owner, @Nullable Ability ability) {
        if (this.channeled != null) {
            ((Ability.IChannelened) this.channeled).onRelease(owner, this.charge);
        }
        this.channeled = this.channeled == ability ? null : ability;
    }

    @Override
    public boolean isChanneling(Ability ability) {
        return this.channeled == ability;
    }

    @Override
    public int getCharge() {
        return this.charge;
    }

    @Override
    public void addSummon(Entity entity) {
        this.summons.add(entity.getUUID());
    }

    @Override
    public void removeSummon(Entity entity) {
        this.summons.remove(entity.getUUID());
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
                iter.remove();
                summon.discard();
            }
        }
    }

    @Override
    public <T extends Entity> void removeSummonByClass(ServerLevel level, Class<T> clazz) {
        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        Iterator<UUID> iter = this.summons.iterator();

        while (iter.hasNext()) {
            UUID identifier = iter.next();

            Entity entity = level.getEntity(identifier);

            if (entity == null) continue;

            T summon = test.tryCast(entity);

            if (summon != null) {
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
    public boolean isDead(Registry<EntityType<?>> registry, EntityType<?> entity) {
        return this.dead.contains(registry.getKey(entity));
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
    public Set<Ability> getAdapted() {
        return this.adapted;
    }

    @Override
    public void addAdapted(Set<Ability> adaptations) {
        this.adapted.addAll(adaptations);
    }

    @Override
    public Map<Ability, Integer> getAdapting() {
        return this.adapting;
    }

    @Override
    public void addAdapting(Map<Ability, Integer> adapting) {
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

    private @Nullable Ability getAbility(DamageSource source) {
        Ability ability;

        if (source instanceof JJKDamageSources.JujutsuDamageSource jujutsu && (ability = jujutsu.getAbility()) != null) {
            return ability;
        }
        return null;
    }

    @Override
    public boolean isAdaptedTo(DamageSource source) {
        Ability ability = this.getAbility(source);
        return ability != null && this.isAdaptedTo(ability);
    }

    @Override
    public boolean isAdaptedTo(Ability ability) {
        for (Ability adapted : this.adapted) {
            Ability.Classification first = adapted.getClassification();
            Ability.Classification second = ability.getClassification();
            if (first != Ability.Classification.NONE && second != Ability.Classification.NONE &&
                    adapted.getClassification() == ability.getClassification()) return true;
        }
        return this.adapted.contains(ability);
    }

    @Override
    public boolean isAdaptedTo(CursedTechnique technique) {
        for (Ability ability : technique.getAbilities()) {
            if (this.isAdaptedTo(ability)) return true;
        }
        return false;
    }

    @Override
    public void tryAdapt(@Nullable Ability ability) {
        if (ability == null) return;

        if (!this.adapting.containsKey(ability)) {
            this.adapting.put(ability, 0);
        } else {
            int timer = this.adapting.get(ability);
            timer += ADAPTATION_STEP;
            this.adapting.put(ability, timer);
        }
    }

    @Override
    public void tryAdapt(DamageSource source) {
        boolean melee = !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK));

        Ability ability;

        if (melee && source.getEntity() instanceof LivingEntity attacker && JJKAbilities.hasToggled(attacker, JJKAbilities.INFINITY.get())) {
            ability = JJKAbilities.INFINITY.get();
        } else {
            ability = this.getAbility(source);
        }
        this.tryAdapt(ability);
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
    public void addCurse(Registry<EntityType<?>> registry, EntityType<?> type) {
        ResourceLocation key = registry.getKey(type);
        this.curses.put(key, this.curses.getOrDefault(key, 0) + 1);
    }

    @Override
    public void removeCurse(Registry<EntityType<?>> registry, EntityType<?> type) {
        ResourceLocation key = registry.getKey(type);

        int count = this.curses.get(key) - 1;

        if (count == 0) {
            this.curses.remove(key);
        } else {
            this.curses.put(key, count);
        }
    }

    @Override
    public Map<EntityType<?>, Integer> getCurses(Registry<EntityType<?>> registry) {
        Map<EntityType<?>, Integer> curses = new HashMap<>();

        if (!this.hasTechnique(CursedTechnique.CURSE_MANIPULATION)) return curses;

        for (Map.Entry<ResourceLocation, Integer> entry : this.curses.entrySet()) {
            curses.put(registry.get(entry.getKey()), entry.getValue());
        }
        return curses;
    }

    @Override
    public boolean hasCurse(Registry<EntityType<?>> registry, EntityType<?> type) {
        return this.curses.containsKey(registry.getKey(type));
    }

    @Override
    public void generate(ServerPlayer player) {
        this.initialized = true;

        this.traits.clear();

        if (HelperMethods.RANDOM.nextInt(10) == 0) {
            this.addTrait(Trait.HEAVENLY_RESTRICTION);
        } else {
            this.technique = HelperMethods.randomEnum(CursedTechnique.class);

            if (HelperMethods.RANDOM.nextInt(5) == 0) {
                this.nature = HelperMethods.randomEnum(CursedEnergyNature.class);

                if (this.nature != CursedEnergyNature.BASIC) {
                    player.sendSystemMessage(Component.translatable(String.format("chat.%s.nature", JujutsuKaisen.MOD_ID), this.nature.getName()));
                }
            }
            this.type = HelperMethods.RANDOM.nextInt(5) == 0 ? JujutsuType.CURSE : JujutsuType.SORCERER;

            if (HelperMethods.RANDOM.nextInt(10) == 0) {
                this.addTrait(this.type == JujutsuType.SORCERER ? Trait.SIX_EYES : Trait.CURSED_WOMB);
            }

            assert this.technique != null;

            if (this.technique == CursedTechnique.RIKA) {
                this.addTrait(Trait.REVERSE_CURSED_TECHNIQUE);
            }

            player.sendSystemMessage(Component.translatable(String.format("chat.%s.technique", JujutsuKaisen.MOD_ID), this.technique.getName()));

            if (this.type == JujutsuType.CURSE) {
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
    public @Nullable CursedTechnique getAdditional() {
        return this.additional;
    }

    @Override
    public void setAdditional(@Nullable CursedTechnique technique) {
        this.additional = technique;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("initialized", this.initialized);

        if (this.technique != null) {
            nbt.putInt("technique", this.technique.ordinal());
        }
        if (this.additional != null) {
            nbt.putInt("additional", this.additional.ordinal());
        }
        if (this.currentCopied != null) {
            nbt.putInt("current_copied", this.currentCopied.ordinal());
        }
        if (nbt.contains("current_absorbed")) {
            this.currentAbsorbed = CursedTechnique.values()[nbt.getInt("current_absorbed")];
        }
        nbt.putInt("nature", this.nature.ordinal());
        nbt.putFloat("experience", this.experience);
        nbt.putFloat("energy", this.energy);
        nbt.putFloat("max_energy", this.maxEnergy);
        nbt.putFloat("used", this.used);
        nbt.putInt("type", this.type.ordinal());
        nbt.putInt("burnout", this.burnout);
        nbt.putInt("grade", this.grade.ordinal());
        nbt.putInt("mode", this.mode.ordinal());
        nbt.putInt("charge", this.charge);
        nbt.putLong("last_black_flash_time", this.lastBlackFlashTime);

        if (this.domain != null) {
            nbt.putUUID("domain", this.domain);
        }

        if (this.channeled != null) {
            nbt.putString("channeled", JJKAbilities.getKey(this.channeled).toString());
        }

        ListTag copiedTag = new ListTag();

        for (CursedTechnique technique : this.copied) {
            copiedTag.add(IntTag.valueOf(technique.ordinal()));
        }
        nbt.put("copied", copiedTag);

        ListTag absorbedTag = new ListTag();

        for (CursedTechnique technique : this.absorbed) {
            absorbedTag.add(IntTag.valueOf(technique.ordinal()));
        }
        nbt.put("absorbed", absorbedTag);

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

        ListTag deadTag = new ListTag();

        for (ResourceLocation key : this.dead) {
            deadTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("dead", deadTag);

        ListTag adaptedTag = new ListTag();

        for (Ability ability : this.adapted) {
            adaptedTag.add(StringTag.valueOf(JJKAbilities.getKey(ability).toString()));
        }
        nbt.put("adapted", adaptedTag);

        ListTag adaptingTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.adapting.entrySet()) {
            CompoundTag adaptation = new CompoundTag();
            adaptation.putString("key", JJKAbilities.getKey(entry.getKey()).toString());
            adaptation.putInt("stage", entry.getValue());
            adaptingTag.add(adaptation);
        }
        nbt.put("adapting", adaptingTag);

        ListTag shadowInventoryTag = new ListTag();

        for (ItemStack stack : this.shadowInventory) {
            shadowInventoryTag.add(stack.save(new CompoundTag()));
        }
        nbt.put("shadow_inventory", shadowInventoryTag);

        ListTag cursesTag = new ListTag();

        for (Map.Entry<ResourceLocation, Integer> entry : this.curses.entrySet()) {
            CompoundTag curse = new CompoundTag();
            curse.putString("key", entry.getKey().toString());
            curse.putInt("count", entry.getValue());
            cursesTag.add(curse);
        }
        nbt.put("curses", cursesTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.initialized = nbt.getBoolean("initialized");

        if (nbt.contains("technique")) {
            this.technique = CursedTechnique.values()[nbt.getInt("technique")];
        }
        if (nbt.contains("additional")) {
            this.additional = CursedTechnique.values()[nbt.getInt("additional")];
        }
        if (nbt.contains("current_copied")) {
            this.currentCopied = CursedTechnique.values()[nbt.getInt("current_copied")];
        }
        if (nbt.contains("current_absorbed")) {
            this.currentAbsorbed = CursedTechnique.values()[nbt.getInt("current_absorbed")];
        }
        this.nature = CursedEnergyNature.values()[nbt.getInt("nature")];
        this.experience = nbt.getFloat("experience");
        this.energy = nbt.getFloat("energy");
        this.maxEnergy = nbt.getFloat("max_energy");
        this.used = nbt.getFloat("used");
        this.type = JujutsuType.values()[nbt.getInt("type")];
        this.burnout = nbt.getInt("burnout");
        this.grade = SorcererGrade.values()[nbt.getInt("grade")];
        this.mode = TenShadowsMode.values()[nbt.getInt("mode")];
        this.charge = nbt.getInt("charge");
        this.lastBlackFlashTime = nbt.getLong("last_black_flash_time");

        if (nbt.hasUUID("domain")) {
            this.domain = nbt.getUUID("domain");
        }

        if (nbt.contains("channeled")) {
            this.channeled = JJKAbilities.getValue(new ResourceLocation(nbt.getString("channeled")));
        }

        this.copied.clear();

        for (Tag tag : nbt.getList("copied", Tag.TAG_INT)) {
            this.copied.add(CursedTechnique.values()[((IntTag) tag).getAsInt()]);
        }

        this.absorbed.clear();

        for (Tag tag : nbt.getList("absorbed", Tag.TAG_INT)) {
            this.absorbed.add(CursedTechnique.values()[((IntTag) tag).getAsInt()]);
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

        this.dead.clear();

        for (Tag key : nbt.getList("dead", Tag.TAG_STRING)) {
            this.dead.add(new ResourceLocation(key.getAsString()));
        }

        this.adapted.clear();

        for (Tag key : nbt.getList("adapted", Tag.TAG_INT)) {
            this.adapted.add(JJKAbilities.getValue(new ResourceLocation(key.getAsString())));
        }

        this.adapting.clear();

        for (Tag key : nbt.getList("adapting", Tag.TAG_COMPOUND)) {
            CompoundTag adaptation = (CompoundTag) key;
            this.adapting.put(JJKAbilities.getValue(new ResourceLocation(adaptation.getString("key"))), adaptation.getInt("stage"));
        }

        this.shadowInventory.clear();

        for (Tag key : nbt.getList("shadow_inventory", Tag.TAG_COMPOUND)) {
            this.shadowInventory.add(ItemStack.of((CompoundTag) key));
        }

        this.curses.clear();

        for (Tag key : nbt.getList("curses", Tag.TAG_COMPOUND)) {
            CompoundTag curse = (CompoundTag) key;
            this.curses.put(new ResourceLocation(curse.getString("key")), curse.getInt("count"));
        }
    }
}
