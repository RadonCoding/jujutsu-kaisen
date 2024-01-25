package radon.jujutsu_kaisen.capability.data;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.Registry;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import radon.jujutsu_kaisen.JJKConstants;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityStopEvent;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.capability.data.sorcerer.*;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.WheelEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncVisualDataS2CPacket;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.PlayerUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class SorcererData implements ISorcererData {
    private boolean initialized;

    private int cursedEnergyColor;

    private int points;
    private final Set<Ability> unlocked;

    private float domainSize;

    private @Nullable CursedTechnique technique;

    private @Nullable CursedTechnique additional;

    private final Set<CursedTechnique> copied;
    private @Nullable CursedTechnique currentCopied;

    private final Set<CursedTechnique> absorbed;
    private @Nullable CursedTechnique currentAbsorbed;

    private int transfiguredSouls;

    private CursedEnergyNature nature;

    private float experience;
    private float output;

    private float energy;
    private float maxEnergy;
    private float extraEnergy;

    private JujutsuType type;

    private int burnout;
    private int brainDamage;
    private int brainDamageTimer;

    private long lastBlackFlashTime;

    private @Nullable Ability channeled;
    private int charge;

    private final Set<Ability> toggled;

    private final Set<Trait> traits;
    private final List<DelayedTickEvent> delayedTickEvents;
    private final Map<Ability, Integer> cooldowns;
    private final Map<Ability, Integer> durations;
    private final Set<Integer> summons;
    private final Map<UUID, Set<Pact>> acceptedPacts;
    private final Map<UUID, Set<Pact>> requestedPactsCreations;
    private final Map<UUID, Integer> createRequestExpirations;
    private final Map<UUID, Set<Pact>> requestedPactsRemovals;
    private final Set<BindingVow> bindingVows;
    private final Map<BindingVow, Integer> bindingVowCooldowns;
    private final Map<Ability, Set<String>> chants;

    // Ten shadows
    private final Set<ResourceLocation> tamed;
    private final Set<ResourceLocation> dead;
    private final List<ItemStack> shadowInventory;
    private final Set<Ability> adapted;
    private final Map<Ability, Integer> adapting;
    private TenShadowsMode mode;

    // Curse Manipulation
    private final List<AbsorbedCurse> curses;

    // Projection Sorcery
    private final List<AbstractMap.SimpleEntry<Vec3, Float>> frames;
    private int speedStacks;
    private int noMotionTime;

    private int fingers;

    private static final UUID MAX_HEALTH_UUID = UUID.fromString("72ff5080-3a82-4a03-8493-3be970039cfe");
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("4979087e-da76-4f8a-93ef-6e5847bfa2ee");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("a2aef906-ed31-49e8-a56c-decccbfa2c1f");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("9fe023ca-f22b-4429-a5e5-c099387d5441");
    private static final UUID PROJECTION_SORCERY_MOVEMENT_SPEED_UUID = UUID.fromString("23ecaba3-fbe8-44c1-93c4-5291aa9ee777");
    private static final UUID PROJECTION_ATTACK_SPEED_UUID = UUID.fromString("18cd1e25-656d-4172-b9f7-2f1b3daf4b89");
    private static final UUID PROJECTION_STEP_HEIGHT_UUID = UUID.fromString("1dbcbef7-8193-406a-b64d-8766ea505fdb");

    private LivingEntity owner;

    public SorcererData() {
        this.domainSize = 1.0F;

        this.unlocked = new HashSet<>();

        this.nature = CursedEnergyNature.BASIC;

        this.type = JujutsuType.SORCERER;

        this.copied = new LinkedHashSet<>();
        this.absorbed = new LinkedHashSet<>();

        this.output = 1.0F;

        this.mode = TenShadowsMode.SUMMON;

        this.lastBlackFlashTime = -1;

        this.toggled = new HashSet<>();
        this.traits = new HashSet<>();
        this.delayedTickEvents = new ArrayList<>();
        this.cooldowns = new HashMap<>();
        this.durations = new HashMap<>();
        this.summons = new HashSet<>();
        this.acceptedPacts = new HashMap<>();
        this.requestedPactsCreations = new HashMap<>();
        this.createRequestExpirations = new HashMap<>();
        this.requestedPactsRemovals = new HashMap<>();
        this.bindingVows = new HashSet<>();
        this.bindingVowCooldowns = new HashMap<>();
        this.chants = new HashMap<>();

        this.tamed = new HashSet<>();
        this.dead = new HashSet<>();

        this.adapted = new HashSet<>();
        this.adapting = new HashMap<>();

        this.curses = new ArrayList<>();

        this.frames = new ArrayList<>();

        this.shadowInventory = new ArrayList<>();
    }

    private void sync() {
        if (!this.owner.level().isClientSide) {
            ClientVisualHandler.ClientData data = new ClientVisualHandler.ClientData(this.getToggled(), this.channeled, this.getTraits(), this.getTechniques(), this.getTechnique(), this.getType(),
                    this.getExperience(), this.getCursedEnergyColor());
            PacketHandler.broadcast(new SyncVisualDataS2CPacket(this.owner.getUUID(), data.serializeNBT()));
        }
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

    private void updateDurations() {
        Iterator<Map.Entry<Ability, Integer>> iter = this.durations.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Ability, Integer> entry = iter.next();

            Ability ability = entry.getKey();

            if (!this.isChanneling(ability)) {
                iter.remove();
                continue;
            }

            int remaining = entry.getValue();

            if (remaining >= 0) {
                this.durations.put(entry.getKey(), --remaining);
            } else {
                if (ability instanceof Ability.IToggled) {
                    if (this.hasToggled(ability)) {
                        this.toggle(ability);
                    }
                } else if (ability instanceof Ability.IChannelened) {
                    if (this.isChanneling(ability)) {
                        this.channel(ability);
                    }
                }
                iter.remove();
            }
        }
    }

    private void updateTickEvents() {
        this.delayedTickEvents.removeIf(DelayedTickEvent::finished);

        for (DelayedTickEvent current : new ArrayList<>(this.delayedTickEvents)) {
            current.tick();

            if (current.finished()) {
                current.run();
            }
        }
    }

    private void updateToggled() {
        List<Ability> remove = new ArrayList<>();

        for (Ability ability : new ArrayList<>(this.toggled)) {
            Ability.Status status = ability.isStillUsable(this.owner);

            if (status == Ability.Status.SUCCESS || status == Ability.Status.COOLDOWN) {
                ability.run(this.owner);

                ((Ability.IToggled) ability).applyModifiers(this.owner);
            } else {
                remove.add(ability);
            }
        }

        for (Ability ability : remove) {
            this.toggle(ability);
        }
    }

    private void updateChanneled() {
        if (this.channeled != null) {
            Ability.Status status = this.channeled.isStillUsable(this.owner);

            if (status == Ability.Status.SUCCESS || status == Ability.Status.COOLDOWN) {
                this.channeled.run(this.owner);
            } else {
                this.channel(this.channeled);
            }
            this.charge++;
        } else {
            this.charge = 0;
        }
    }

    private void updateSummons() {
        if (!this.owner.level().isLoaded(this.owner.blockPosition())) return;

        if (this.owner.level() instanceof ServerLevel level) {
            Iterator<Integer> iter = this.summons.iterator();

            while (iter.hasNext()) {
                Integer identifier = iter.next();

                Entity entity = level.getEntity(identifier);

                if (entity == null || !entity.isAlive() || entity.isRemoved()) {
                    iter.remove();
                }
            }
        }
    }

    private void updateAdaptation() {
        if (this.toggled.contains(JJKAbilities.WHEEL.get())) {
            Iterator<Map.Entry<Ability, Integer>> iter = this.adapting.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<Ability, Integer> entry = iter.next();

                int timer = entry.getValue();

                if (++timer >= JJKConstants.REQUIRED_ADAPTATION) {
                    iter.remove();

                    this.adapted.add(entry.getKey());

                    if (this.owner instanceof MahoragaEntity mahoraga) {
                        mahoraga.onAdaptation();
                    }

                    WheelEntity wheel = this.getSummonByClass(WheelEntity.class);

                    if (wheel != null) {
                        wheel.spin();
                    }
                } else {
                    entry.setValue(timer);
                }
            }
        }
    }

    private void updateRequestExpirations() {
        Iterator<Map.Entry<UUID, Integer>> iter = this.createRequestExpirations.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<UUID, Integer> entry = iter.next();

            int remaining = entry.getValue();

            if (remaining > 0) {
                this.createRequestExpirations.put(entry.getKey(), --remaining);
            } else {
                iter.remove();
                this.requestedPactsCreations.remove(entry.getKey());
            }
        }
    }

    private void updateBindingVowCooldowns() {
        Iterator<Map.Entry<BindingVow, Integer>> iter = this.bindingVowCooldowns.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<BindingVow, Integer> entry = iter.next();

            int remaining = entry.getValue();

            if (remaining > 0) {
                this.bindingVowCooldowns.put(entry.getKey(), --remaining);
            } else {
                iter.remove();
                this.bindingVowCooldowns.remove(entry.getKey());
            }
        }
    }

    private void updateBrainDamage() {
        if (this.brainDamage == 0) return;

        if (!this.owner.level().isClientSide) {
            this.owner.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 10 * 20, this.brainDamage - 1, false, false, false));
        }

        this.brainDamageTimer++;

        if (this.brainDamageTimer >= JJKConstants.DECREASE_BRAIN_DAMAGE_INTERVAL) {
            this.brainDamageTimer = 0;
            this.brainDamage--;
        }
    }

    private void checkAdvancements(ServerPlayer player) {
        if (this.traits.contains(Trait.SIX_EYES)) PlayerUtil.giveAdvancement(player, "six_eyes");
        if (this.traits.contains(Trait.HEAVENLY_RESTRICTION)) PlayerUtil.giveAdvancement(player, "heavenly_restriction");
        if (this.traits.contains(Trait.VESSEL)) PlayerUtil.giveAdvancement(player, "vessel");
        if (this.unlocked.contains(JJKAbilities.RCT1.get()))
            PlayerUtil.giveAdvancement(player, "reverse_cursed_technique");
        if (this.traits.contains(Trait.PERFECT_BODY))
            PlayerUtil.giveAdvancement(player, "perfect_body");
    }

    @Override
    public void attack(DamageSource source, LivingEntity target) {
        if (this.owner == null) return;

        if (this.channeled instanceof Ability.IAttack attack) {
            if (this.channeled.getStatus(this.owner) == Ability.Status.SUCCESS && attack.attack(source, this.owner, target)) {
                this.channeled.charge(this.owner);
                this.charge = 0;
            }
        }

        for (Ability ability : this.toggled) {
            // In-case any of IAttack's kill the target just break the loop
            if (target.isDeadOrDying()) break;

            if (!(ability instanceof Ability.IAttack attack)) continue;
            if (ability.getStatus(this.owner) != Ability.Status.SUCCESS) continue;
            if (!attack.attack(source, this.owner, target)) continue;

            ability.charge(this.owner);
        }

        if (this.owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(this.serializeNBT()), player);
        }
    }

    @Override
    public void tick(LivingEntity owner) {
        if (this.owner == null) {
            this.owner = owner;
        }

        this.updateSummons();

        this.updateCooldowns();
        this.updateDurations();
        this.updateTickEvents();
        this.updateToggled();
        this.updateChanneled();

        this.updateRequestExpirations();
        this.updateBindingVowCooldowns();

        this.updateBrainDamage();

        if (!this.owner.level().isClientSide) {
            if (this.speedStacks > 0) {
                EntityUtil.applyModifier(this.owner, Attributes.MOVEMENT_SPEED, PROJECTION_SORCERY_MOVEMENT_SPEED_UUID, "Movement speed", this.speedStacks * 2.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
                EntityUtil.applyModifier(this.owner, Attributes.ATTACK_SPEED, PROJECTION_ATTACK_SPEED_UUID, "Attack speed", this.speedStacks, AttributeModifier.Operation.MULTIPLY_TOTAL);
                EntityUtil.applyModifier(this.owner, ForgeMod.STEP_HEIGHT_ADDITION.get(), PROJECTION_STEP_HEIGHT_UUID, "Step height addition", 2.0F, AttributeModifier.Operation.ADDITION);

                if (this.owner.walkDist == this.owner.walkDistO) {
                    this.noMotionTime++;
                } else if (this.noMotionTime == 1) {
                    this.noMotionTime = 0;
                }

                if (this.noMotionTime > 1) {
                    this.resetSpeedStacks();
                }
            } else {
                EntityUtil.removeModifier(this.owner, Attributes.MOVEMENT_SPEED, PROJECTION_SORCERY_MOVEMENT_SPEED_UUID);
                EntityUtil.removeModifier(this.owner, Attributes.ATTACK_SPEED, PROJECTION_ATTACK_SPEED_UUID);
                EntityUtil.removeModifier(this.owner, ForgeMod.STEP_HEIGHT_ADDITION.get(), PROJECTION_STEP_HEIGHT_UUID);
            }

            this.updateAdaptation();

            if (this.owner instanceof ServerPlayer player) {
                if (!this.initialized) {
                    this.initialized = true;
                    this.generate(player);
                }
                this.checkAdvancements(player);
            }
        }

        if (this.burnout > 0) {
            this.burnout--;
        }

        this.energy = Math.min(this.energy + (ConfigHolder.SERVER.cursedEnergyRegenerationAmount.get().floatValue() * (this.owner instanceof Player player ? (player.getFoodData().getFoodLevel() / 20.0F) : 1.0F)), this.getMaxEnergy());

        if (this.traits.contains(Trait.HEAVENLY_RESTRICTION)) {
            double health = Math.ceil(((this.getRealPower() - 1.0F) * 30.0D) / 20) * 20;

            if (EntityUtil.applyModifier(this.owner, Attributes.MAX_HEALTH, MAX_HEALTH_UUID, "Max health", health, AttributeModifier.Operation.ADDITION)) {
                this.owner.setHealth(this.owner.getMaxHealth());
            }

            double damage = this.getRealPower() * 3.5D;
            EntityUtil.applyModifier(this.owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", damage, AttributeModifier.Operation.ADDITION);

            double speed = this.getRealPower();
            EntityUtil.applyModifier(this.owner, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID, "Attack speed", speed, AttributeModifier.Operation.ADDITION);

            double movement = this.getRealPower() * 0.05D;
            EntityUtil.applyModifier(this.owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed", Math.min(this.owner.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 2,  movement), AttributeModifier.Operation.ADDITION);

            if (this.owner.getHealth() < this.owner.getMaxHealth()) {
                this.owner.heal(1.0F / 20);
            }
        } else {
            double health = Math.ceil(((this.getRealPower() - 1.0F) * 20.0D) / 20) * 20;

            double damage = this.getRealPower() * 1.7D;
            EntityUtil.applyModifier(this.owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", damage, AttributeModifier.Operation.ADDITION);

            if (EntityUtil.applyModifier(this.owner, Attributes.MAX_HEALTH, MAX_HEALTH_UUID, "Max health", health, AttributeModifier.Operation.ADDITION)) {
                this.owner.setHealth(this.owner.getMaxHealth());
            }
        }
    }

    @Override
    public void init(LivingEntity owner) {
        this.owner = owner;
    }

    @Override
    public int getCursedEnergyColor() {
        return this.cursedEnergyColor == 0 ? HelperMethods.getRGB24(ParticleColors.getCursedEnergyColor(this.type)) : this.cursedEnergyColor;
    }

    @Override
    public void setCursedEnergyColor(int color) {
        this.cursedEnergyColor = color;
        this.sync();
    }

    @Override
    public float getMaximumOutput() {
        return (this.isInZone() ? 1.2F : 1.0F) * (1.0F - ((float) this.brainDamage / JJKConstants.MAX_BRAIN_DAMAGE));
    }

    @Override
    public void increaseOutput() {
        this.output = Math.min(this.getMaximumOutput(), this.output + 0.1F);
    }

    @Override
    public void decreaseOutput() {
        this.output = Math.max(0.1F, this.output - 0.1F);
    }

    @Override
    public int getPoints() {
        return this.points;
    }

    @Override
    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public void addPoints(int points) {
        this.points += points;
    }

    @Override
    public void usePoints(int count) {
        this.points -= count;
    }

    @Override
    public boolean isUnlocked(Ability ability) {
        return this.unlocked.contains(ability);
    }

    @Override
    public void unlock(Ability ability) {
        this.unlocked.add(ability);
    }

    @Override
    public void unlockAll(List<Ability> abilities) {
        this.unlocked.addAll(abilities);
    }

    @Override
    public void createPact(UUID recipient, Pact pact) {
        if (!this.acceptedPacts.containsKey(recipient)) {
            this.acceptedPacts.put(recipient, new HashSet<>());
        }
        this.acceptedPacts.get(recipient).add(pact);
    }

    @Override
    public boolean hasPact(UUID recipient, Pact pact) {
        return this.acceptedPacts.getOrDefault(recipient, Set.of()).contains(pact);
    }

    @Override
    public void removePact(UUID recipient, Pact pact) {
        this.acceptedPacts.get(recipient).remove(pact);

        if (this.acceptedPacts.get(recipient).isEmpty()) {
            this.acceptedPacts.remove(recipient);
        }
    }

    @Override
    public void createPactCreationRequest(UUID recipient, Pact pact) {
        if (!this.requestedPactsCreations.containsKey(recipient)) {
            this.requestedPactsCreations.put(recipient, new HashSet<>());
        }
        this.requestedPactsCreations.get(recipient).add(pact);
        this.createRequestExpirations.put(recipient, 30 * 20);
    }

    @Override
    public void createPactRemovalRequest(UUID recipient, Pact pact) {
        if (!this.requestedPactsRemovals.containsKey(recipient)) {
            this.requestedPactsRemovals.put(recipient, new HashSet<>());
        }
        this.requestedPactsRemovals.get(recipient).add(pact);
    }

    @Override
    public void removePactCreationRequest(UUID recipient, Pact pact) {
        this.requestedPactsCreations.getOrDefault(recipient, new HashSet<>()).remove(pact);
        this.createRequestExpirations.remove(recipient);
    }

    @Override
    public void removePactRemovalRequest(UUID recipient, Pact pact) {
        this.requestedPactsRemovals.getOrDefault(recipient, new HashSet<>()).remove(pact);
    }

    @Override
    public boolean hasRequestedPactCreation(UUID recipient, Pact pact) {
        return this.requestedPactsCreations.getOrDefault(recipient, Set.of()).contains(pact);
    }

    @Override
    public boolean hasRequestedPactRemoval(UUID recipient, Pact pact) {
        return this.requestedPactsRemovals.getOrDefault(recipient, Set.of()).contains(pact);
    }

    @Override
    public void addBindingVow(BindingVow vow) {
        this.bindingVows.add(vow);
    }

    @Override
    public void removeBindingVow(BindingVow vow) {
        this.bindingVows.remove(vow);
    }

    @Override
    public boolean hasBindingVow(BindingVow vow) {
        return this.bindingVows.contains(vow);
    }

    @Override
    public void addBindingVowCooldown(BindingVow vow) {
        this.bindingVowCooldowns.put(vow, 20 * 60 * 30);
    }

    @Override
    public int getRemainingCooldown(BindingVow vow) {
        return this.bindingVowCooldowns.get(vow);
    }

    @Override
    public boolean isCooldownDone(BindingVow vow) {
        return !this.bindingVowCooldowns.containsKey(vow);
    }

    @Override
    public void addChant(Ability ability, String chant) {
        if (!this.chants.containsKey(ability)) {
            this.chants.put(ability, new LinkedHashSet<>());
        }
        this.chants.get(ability).add(chant);
    }

    @Override
    public void addChants(Ability ability, Set<String> chants) {
        this.chants.put(ability, chants);
    }

    @Override
    public void removeChant(Ability ability, String chant) {
        if (this.chants.containsKey(ability)) {
            this.chants.get(ability).remove(chant);

            if (this.chants.get(ability).isEmpty()) {
                this.chants.remove(ability);
            }
        }
    }

    @Override
    public boolean hasChant(Ability ability, String chant) {
        List<String> chants = new ArrayList<>(this.chants.getOrDefault(ability, Set.of()));

        if (chants.contains(chant)) return true;

        chants.add(chant);

        for (Map.Entry<Ability, Set<String>> entry : this.chants.entrySet()) {
            if (entry.getKey() == ability) continue;

            List<String> current = new ArrayList<>(entry.getValue());

            for (int i = 0; i < chants.size(); i++) {
                if (i > current.size() - 1) break;
                if (chants.get(i).equals(current.get(i))) return true;
            }
        }
        return false;
    }

    @Override
    public boolean isChantsAvailable(Set<String> chants) {
        for (Set<String> entry : this.chants.values()) {
            if (entry.containsAll(chants)) return false;
        }
        return true;
    }

    @Override
    public @Nullable Ability getAbility(String chant) {
        for (Map.Entry<Ability, Set<String>> entry : this.chants.entrySet()) {
            if (entry.getValue().contains(chant)) return entry.getKey();
        }
        return null;
    }

    @Override
    public @Nullable Ability getAbility(Set<String> chants) {
        for (Map.Entry<Ability, Set<String>> entry : this.chants.entrySet()) {
            if (entry.getValue().equals(chants)) return entry.getKey();
        }
        return null;
    }

    @Override
    public Set<String> getFirstChants() {
        return this.chants.values().stream().map(set -> set.stream().findFirst().orElseThrow()).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getFirstChants(Ability ability) {
        return this.chants.getOrDefault(ability, Set.of());
    }

    @Override
    public float getOutput() {
        return Math.min(this.getMaximumOutput(), this.output);
    }

    @Override
    public float getAbilityPower() {
        float power = this.getRealPower() * this.getOutput();

        if (this.technique != null) {
            Ability domain = this.technique.getDomain();

            if (domain != null && this.toggled.contains(domain)) {
                power *= 2.0F;
            }
        }
        return power;
    }

    @Override
    public float getRealPower() {
        return SorcererUtil.getPower(this.experience);
    }

    @Override
    public float getExperience() {
        return this.experience;
    }

    @Override
    public void setExperience(float experience) {
        this.experience = experience;
        this.sync();
    }

    @Override
    public boolean addExperience(float amount) {
        SorcererGrade previous = SorcererUtil.getGrade(this.experience);

        if (this.experience >= ConfigHolder.SERVER.maximumExperienceAmount.get().floatValue()) {
            return false;
        }

        this.experience = Math.min(ConfigHolder.SERVER.maximumExperienceAmount.get().floatValue(), this.experience + amount);

        SorcererGrade current = SorcererUtil.getGrade(this.experience);

        if (!this.owner.level().isClientSide && this.owner instanceof Player) {
            if (previous != current) {
                this.owner.sendSystemMessage(Component.translatable(String.format("chat.%s.rank_up", JujutsuKaisen.MOD_ID), current.getName()));
            }
        }
        this.sync();
        return true;
    }

    @Override
    public float getDomainSize() {
        return this.domainSize;
    }

    @Override
    public void setDomainSize(float domainSize) {
        this.domainSize = domainSize;
    }

    public @Nullable CursedTechnique getTechnique() {
        return this.technique;
    }

    @Override
    public Set<CursedTechnique> getTechniques() {
        Set<CursedTechnique> techniques = new HashSet<>();

        if (this.getTechnique() != null) techniques.add(this.getTechnique());
        if (this.getCurrentCopied() != null) techniques.add(this.getCurrentCopied());
        if (this.getCurrentAbsorbed() != null) techniques.add(this.getCurrentAbsorbed());
        if (this.getAdditional() != null) techniques.add(this.getAdditional());

        return techniques;
    }

    @Override
    public boolean hasTechnique(CursedTechnique technique) {
        return this.technique == technique || this.additional == technique || this.getCurrentCopied() == technique || this.currentAbsorbed == technique;
    }

    @Override
    public void setTechnique(@Nullable CursedTechnique technique) {
        this.technique = technique;
        this.sync();
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
    public void setGrade(SorcererGrade grade) {
        this.experience = grade.getRequiredExperience();
    }

    @Override
    public boolean hasTrait(Trait trait) {
        return this.traits.contains(trait);
    }

    @Override
    public void addTrait(Trait trait) {
        this.traits.add(trait);
        this.sync();
    }

    @Override
    public void addTraits(List<Trait> traits) {
        this.traits.addAll(traits);
        this.sync();
    }

    @Override
    public void removeTrait(Trait trait) {
        this.traits.remove(trait);
        this.sync();
    }

    @Override
    public Set<Trait> getTraits() {
        return this.traits;
    }

    @Override
    public void setTraits(Set<Trait> traits) {
        this.traits.clear();
        this.traits.addAll(traits);
        this.sync();
    }

    @Override
    public void setType(JujutsuType type) {
        this.type = type;
        this.sync();
    }

    @Override
    public JujutsuType getType() {
        return this.type;
    }

    public void toggle(Ability ability) {
        if (!this.owner.level().isClientSide && this.owner instanceof Player) {
            if (ability.shouldLog(this.owner)) {
                if (this.hasToggled(ability)) {
                    this.owner.sendSystemMessage(ability.getDisableMessage());
                } else {
                    this.owner.sendSystemMessage(ability.getEnableMessage());
                }
            }
        }

        if (this.toggled.contains(ability)) {
            this.toggled.remove(ability);

            ((Ability.IToggled) ability).onDisabled(this.owner);

            ((Ability.IToggled) ability).removeModifiers(this.owner);

            MinecraftForge.EVENT_BUS.post(new AbilityStopEvent(this.owner, ability));
        } else {
            this.toggled.add(ability);
            ((Ability.IToggled) ability).onEnabled(this.owner);
        }
        this.sync();
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
    public void addCooldown(Ability ability) {
        this.cooldowns.put(ability, ability.getRealCooldown(this.owner));
    }

    @Override
    public int getRemainingCooldown(Ability ability) {
        return this.cooldowns.getOrDefault(ability, 0);
    }

    @Override
    public boolean isCooldownDone(Ability ability) {
        return !this.cooldowns.containsKey(ability);
    }

    @Override
    public void addDuration(Ability ability) {
        this.durations.put(ability, ((Ability.IDurationable) ability).getRealDuration(this.owner));
    }

    @Override
    public void increaseBrainDamage() {
        this.brainDamage = Math.min(JJKConstants.MAX_BRAIN_DAMAGE, this.brainDamage + 1);
    }

    @Override
    public int getBrainDamage() {
        return this.brainDamage;
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
    public void addEnergy(float amount) {
        this.energy = Math.min(this.getMaxEnergy(), this.energy + amount);
        this.sync();
    }

    @Override
    public float getMaxEnergy() {
        long time = this.owner.level().getLevelData().getDayTime();
        boolean night = time >= 13000 && time < 24000;
        return (this.bindingVows.contains(BindingVow.OVERTIME) ? night ? 1.2F : 0.9F : 1.0F) *
                ((this.maxEnergy == 0.0F ? ConfigHolder.SERVER.cursedEnergyAmount.get().floatValue() : this.maxEnergy) *
                        this.getRealPower() * (float) Math.log(this.getRealPower() + 1.5)) + this.extraEnergy;
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
    public void setEnergy(float energy) {
        this.energy = energy;
    }

    @Override
    public float getExtraEnergy() {
        return this.extraEnergy;
    }

    @Override
    public void addExtraEnergy(float amount) {
        this.extraEnergy += amount;
    }

    @Override
    public void resetExtraEnergy() {
        this.extraEnergy = 0.0F;
    }

    @Override
    public void onBlackFlash() {
        this.lastBlackFlashTime = this.owner.level().getGameTime();

        this.output = this.getMaximumOutput();

        if (this.owner instanceof ServerPlayer player) {
            PlayerUtil.giveAdvancement(player, "black_flash");
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
    public boolean isInZone() {
        return this.lastBlackFlashTime != -1 && ((this.owner.level().getGameTime() - this.lastBlackFlashTime) / 20) < (5 * 60);
    }

    @Override
    public void delayTickEvent(Runnable task, int delay) {
        this.delayedTickEvents.add(new DelayedTickEvent(task, delay));
    }

    @Override
    public void uncopy(CursedTechnique technique) {
        this.copied.remove(technique);
    }

    @Override
    public void copy(@Nullable CursedTechnique technique) {
        this.copied.add(technique);
    }

    @Override
    public Set<CursedTechnique> getCopied() {
        if (!this.hasToggled(JJKAbilities.RIKA.get()) || !this.hasTechnique(CursedTechnique.MIMICRY)) {
            return Set.of();
        }
        return this.copied;
    }

    @Override
    public void setCurrentCopied(@Nullable CursedTechnique technique) {
        this.currentCopied = this.currentCopied == technique ? null : technique;
        this.sync();
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
        this.sync();
    }

    @Override
    public @Nullable CursedTechnique getCurrentAbsorbed() {
        if (!this.hasTechnique(CursedTechnique.CURSE_MANIPULATION)) {
            return null;
        }
        return this.currentAbsorbed;
    }

    @Override
    public int getTransfiguredSouls() {
        return this.transfiguredSouls;
    }

    @Override
    public void increaseTransfiguredSouls() {
        this.transfiguredSouls++;
    }

    @Override
    public void decreaseTransfiguredSouls() {
        this.transfiguredSouls--;
    }

    @Override
    public @Nullable Ability getChanneled() {
        return this.channeled;
    }

    @Override
    public void channel(@Nullable Ability ability) {
        if (this.channeled != null) {
            ((Ability.IChannelened) this.channeled).onStop(this.owner);

            if (this.channeled instanceof Ability.ICharged charged) {
                if (charged.onRelease(this.owner)) {
                    this.channeled.charge(this.owner);
                }
            }

            if (!this.owner.level().isClientSide && this.channeled.shouldLog(this.owner)) {
                this.owner.sendSystemMessage(this.channeled.getDisableMessage());
            }
            MinecraftForge.EVENT_BUS.post(new AbilityStopEvent(this.owner, ability));
        }

        if (this.channeled == ability) {
            this.channeled = null;
        } else {
            this.channeled = ability;

            if (this.channeled != null) {
                if (!this.owner.level().isClientSide && this.channeled.shouldLog(this.owner)) {
                    this.owner.sendSystemMessage(this.channeled.getEnableMessage());
                }
            }
        }
        this.sync();
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
        this.summons.add(entity.getId());
    }

    @Override
    public void removeSummon(Entity entity) {
        this.summons.remove(entity.getId());
    }

    @Override
    public List<Entity> getSummons() {
        List<Entity> entities = new ArrayList<>();

        for (Integer identifier : this.summons) {
            Entity entity = this.owner.level().getEntity(identifier);

            if (entity == null) continue;

            entities.add(entity);
        }
        return entities;
    }

    @Override
    public <T extends Entity> @Nullable T getSummonByClass(Class<T> clazz) {
        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        for (Integer identifier : this.summons) {
            Entity entity = this.owner.level().getEntity(identifier);

            if (entity == null) continue;

            T summon = test.tryCast(entity);

            if (summon != null) {
                return summon;
            }
        }
        return null;
    }

    @Override
    public <T extends Entity> void unsummonByClass(Class<T> clazz) {
        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        Iterator<Integer> iter = this.summons.iterator();

        while (iter.hasNext()) {
            Integer identifier = iter.next();

            Entity entity = this.owner.level().getEntity(identifier);

            if (entity == null) continue;

            T summon = test.tryCast(entity);

            if (summon != null) {
                iter.remove();
                summon.discard();
            }
        }
    }

    @Override
    public <T extends Entity> void removeSummonByClass(Class<T> clazz) {
        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        Iterator<Integer> iter = this.summons.iterator();

        while (iter.hasNext()) {
            Integer identifier = iter.next();

            Entity entity = this.owner.level().getEntity(identifier);

            if (entity == null) continue;

            T summon = test.tryCast(entity);

            if (summon != null) {
                iter.remove();
            }
        }
    }

    @Override
    public <T extends Entity> boolean hasSummonOfClass(Class<T> clazz) {
        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        for (Integer identifier : this.summons) {
            Entity entity = this.owner.level().getEntity(identifier);

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

    @Override
    public float getAdaptation(DamageSource source) {
        Ability ability = this.getAbility(source);
        return ability == null ? 0.0F : this.getAdaptation(ability);
    }

    @Override
    public float getAdaptation(Ability ability) {
        return this.isAdaptedTo(ability) ? 1.0F : (float) this.adapting.getOrDefault(ability, 0) / JJKConstants.REQUIRED_ADAPTATION;
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
            timer += JJKConstants.ADAPTATION_STEP;
            this.adapting.put(ability, timer);
        }
    }

    @Override
    public void tryAdapt(DamageSource source) {
        this.tryAdapt(this.getAbility(source));
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
    public void addCurse(AbsorbedCurse curse) {
        this.curses.add(curse);
    }

    @Override
    public void removeCurse(AbsorbedCurse curse) {
        this.curses.remove(curse);
    }

    @Override
    public List<AbsorbedCurse> getCurses() {
        if (!this.hasTechnique(CursedTechnique.CURSE_MANIPULATION)) return List.of();

        List<AbsorbedCurse> sorted = new ArrayList<>(this.curses);
        sorted.sort((o1, o2) -> (int) (JJKAbilities.getCurseExperience(o2) - JJKAbilities.getCurseExperience(o1)));
        return sorted;
    }

    @Override
    public List<AbstractMap.SimpleEntry<Vec3, Float>> getFrames() {
        return this.frames;
    }

    @Override
    public void addFrame(Vec3 frame, float yaw) {
        this.frames.add(new AbstractMap.SimpleEntry<>(frame, yaw));
    }

    @Override
    public void removeFrame(AbstractMap.SimpleEntry<Vec3, Float> frame) {
        this.frames.remove(frame);
    }

    @Override
    public void resetFrames() {
        this.frames.clear();
    }

    private boolean isTraitAvailable(MinecraftServer server, Trait trait) {
        for (String name : server.getPlayerNames()) {
            ServerPlayer player;

            if ((player = server.getPlayerList().getPlayerByName(name)) == null) {
                player = server.getPlayerList().getPlayerForLogin(new GameProfile(null, name));
                server.getPlayerList().load(player);
            }
            if (!player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) continue;

            ISorcererData cap = player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.hasTrait(trait)) return false;
        }
        return true;
    }

    @Override
    public void generate(ServerPlayer owner) {
        this.initialized = true;

        this.technique = null;

        this.nature = CursedEnergyNature.BASIC;

        this.traits.remove(Trait.SIX_EYES);
        this.traits.remove(Trait.HEAVENLY_RESTRICTION);
        this.traits.remove(Trait.VESSEL);

        if ((!ConfigHolder.SERVER.uniqueTraits.get() || this.isTraitAvailable(owner.server, Trait.HEAVENLY_RESTRICTION)) &&
                HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.heavenlyRestrictionRarity.get()) == 0) {
            this.addTrait(Trait.HEAVENLY_RESTRICTION);
        } else {
            if (ConfigHolder.SERVER.uniqueTechniques.get()) {
                Set<CursedTechnique> taken = new HashSet<>();

                for (String name : owner.server.getPlayerNames()) {
                    ServerPlayer player;

                    if ((player = owner.server.getPlayerList().getPlayerByName(name)) == null) {
                        player = owner.server.getPlayerList().getPlayerForLogin(new GameProfile(null, name));
                        owner.server.getPlayerList().load(player);
                    }
                    if (!player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) continue;

                    ISorcererData cap = player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    CursedTechnique current = cap.getTechnique();

                    if (current == null) continue;

                    taken.add(current);
                }
                this.technique = HelperMethods.randomEnum(CursedTechnique.class, taken);
            } else {
                this.technique = HelperMethods.randomEnum(CursedTechnique.class);
            }

            if (HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.cursedEnergyNatureRarity.get()) == 0) {
                this.nature = HelperMethods.randomEnum(CursedEnergyNature.class, Set.of(CursedEnergyNature.BASIC));
                owner.sendSystemMessage(Component.translatable(String.format("chat.%s.nature", JujutsuKaisen.MOD_ID), this.nature.getName()));
            }
            this.type = HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.curseRarity.get()) == 0 ? JujutsuType.CURSE : JujutsuType.SORCERER;

            if ((!ConfigHolder.SERVER.uniqueTraits.get() || this.isTraitAvailable(owner.server, Trait.VESSEL)) && this.type == JujutsuType.SORCERER &&
                    HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.vesselRarity.get()) == 0) {
                this.addTrait(Trait.VESSEL);
            }

            if ((!ConfigHolder.SERVER.uniqueTraits.get() || this.isTraitAvailable(owner.server, Trait.SIX_EYES)) &&
                    HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.sixEyesRarity.get()) == 0) {
                this.addTrait(Trait.SIX_EYES);
            }

            assert this.technique != null;

            owner.sendSystemMessage(Component.translatable(String.format("chat.%s.technique", JujutsuKaisen.MOD_ID), this.technique.getName()));

            if (this.type == JujutsuType.CURSE) {
                owner.sendSystemMessage(Component.translatable(String.format("chat.%s.curse", JujutsuKaisen.MOD_ID)));
            } else {
                owner.sendSystemMessage(Component.translatable(String.format("chat.%s.sorcerer", JujutsuKaisen.MOD_ID)));
            }
        }
        this.energy = this.getMaxEnergy();

        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(this.serializeNBT()), owner);
    }

    @Override
    public int getSpeedStacks() {
        return this.speedStacks;
    }

    @Override
    public void addSpeedStack() {
        this.speedStacks = Math.min(JJKConstants.MAX_PROJECTION_SORCERY_STACKS, this.speedStacks + 1);
    }

    @Override
    public void resetSpeedStacks() {
        this.speedStacks = 0;
        this.noMotionTime = 0;
    }

    @Override
    public int getFingers() {
        return this.fingers;
    }

    @Override
    public void setFingers(int count) {
        this.fingers = count;
    }

    @Override
    public int addFingers(int count) {
        int real = Math.min(count, 20 - this.fingers);
        this.fingers += real;
        return real;
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
        this.sync();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("initialized", this.initialized);
        nbt.putInt("cursed_energy_color", this.cursedEnergyColor);
        nbt.putInt("points", this.points);
        nbt.putFloat("domain_size", this.domainSize);

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
        nbt.putInt("transfigured_souls", this.transfiguredSouls);
        nbt.putInt("nature", this.nature.ordinal());
        nbt.putFloat("experience", this.experience);
        nbt.putFloat("output", this.output);
        nbt.putFloat("energy", this.energy);
        nbt.putFloat("max_energy", this.maxEnergy);
        nbt.putFloat("extra_energy", this.extraEnergy);
        nbt.putInt("type", this.type.ordinal());
        nbt.putInt("burnout", this.burnout);
        nbt.putInt("brain_damage", this.brainDamage);
        nbt.putInt("brain_damage_timer", this.brainDamageTimer);
        nbt.putInt("mode", this.mode.ordinal());
        nbt.putInt("charge", this.charge);
        nbt.putLong("last_black_flash_time", this.lastBlackFlashTime);
        nbt.putInt("speed_stacks", this.speedStacks);
        nbt.putInt("fingers", this.fingers);

        ListTag unlockedTag = new ListTag();

        for (Ability ability : this.unlocked) {
            ResourceLocation key = JJKAbilities.getKey(ability);

            if (key == null) continue;

            unlockedTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("unlocked", unlockedTag);

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
            ResourceLocation key = JJKAbilities.getKey(ability);

            if (key == null) continue;

            toggledTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("toggled", toggledTag);

        ListTag traitsTag = new ListTag();

        for (Trait trait : this.traits) {
            traitsTag.add(IntTag.valueOf(trait.ordinal()));
        }
        nbt.put("traits", traitsTag);

        ListTag cooldownsTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.cooldowns.entrySet()) {
            ResourceLocation key = JJKAbilities.getKey(entry.getKey());

            if (key == null) continue;

            CompoundTag data = new CompoundTag();
            data.putString("identifier", key.toString());
            data.putInt("cooldown", entry.getValue());
            cooldownsTag.add(data);
        }
        nbt.put("cooldowns", cooldownsTag);

        ListTag summonsTag = new ListTag();

        for (Integer identifier : this.summons) {
            summonsTag.add(IntTag.valueOf(identifier));
        }
        nbt.put("summons", summonsTag);

        ListTag acceptedPactsTag = new ListTag();

        for (Map.Entry<UUID, Set<Pact>> entry : this.acceptedPacts.entrySet()) {
            CompoundTag data = new CompoundTag();
            data.putUUID("recipient", entry.getKey());

            ListTag pacts = new ListTag();

            for (Pact pact : entry.getValue()) {
                pacts.add(IntTag.valueOf(pact.ordinal()));
            }
            data.put("entries", pacts);

            acceptedPactsTag.add(data);
        }
        nbt.put("accepted_pacts", acceptedPactsTag);

        ListTag bindingVowsTag = new ListTag();

        for (BindingVow vow : this.bindingVows) {
            bindingVowsTag.add(IntTag.valueOf(vow.ordinal()));
        }
        nbt.put("binding_vows", bindingVowsTag);

        ListTag bindingVowCooldownsTag = new ListTag();

        for (Map.Entry<BindingVow, Integer> entry : this.bindingVowCooldowns.entrySet()) {
            CompoundTag data = new CompoundTag();
            data.putInt("vow", entry.getKey().ordinal());
            data.putInt("cooldown", entry.getValue());
        }
        nbt.put("binding_vow_cooldowns", bindingVowCooldownsTag);

        ListTag chantsTag = new ListTag();

        for (Map.Entry<Ability, Set<String>> entry : this.chants.entrySet()) {
            ResourceLocation key = JJKAbilities.getKey(entry.getKey());

            if (key == null) continue;

            CompoundTag data = new CompoundTag();
            data.putString("ability", key.toString());

            ListTag chants = new ListTag();

            for (String chant : entry.getValue()) {
                chants.add(StringTag.valueOf(chant));
            }
            data.put("entries", chants);

            chantsTag.add(data);
        }
        nbt.put("chants", chantsTag);

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
            ResourceLocation key = JJKAbilities.getKey(ability);

            if (key == null) continue;

            adaptedTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("adapted", adaptedTag);

        ListTag adaptingTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.adapting.entrySet()) {
            ResourceLocation key = JJKAbilities.getKey(entry.getKey());

            if (key == null) continue;

            CompoundTag data = new CompoundTag();
            data.putString("key", key.toString());
            data.putInt("stage", entry.getValue());
            adaptingTag.add(data);
        }
        nbt.put("adapting", adaptingTag);

        ListTag shadowInventoryTag = new ListTag();

        for (ItemStack stack : this.shadowInventory) {
            shadowInventoryTag.add(stack.save(new CompoundTag()));
        }
        nbt.put("shadow_inventory", shadowInventoryTag);

        ListTag cursesTag = new ListTag();

        for (AbsorbedCurse curse : this.curses) {
            cursesTag.add(curse.serializeNBT());
        }
        nbt.put("curses", cursesTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.initialized = nbt.getBoolean("initialized");

        this.cursedEnergyColor = nbt.getInt("cursed_energy_color");

        this.points = nbt.getInt("points");
        this.domainSize = nbt.getFloat("domain_size");

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
        this.transfiguredSouls = nbt.getInt("transfigured_souls");
        this.nature = CursedEnergyNature.values()[nbt.getInt("nature")];
        this.experience = nbt.getFloat("experience");
        this.output = nbt.getFloat("output");
        this.energy = nbt.getFloat("energy");
        this.maxEnergy = nbt.getFloat("max_energy");
        this.extraEnergy = nbt.getFloat("extra_energy");
        this.type = JujutsuType.values()[nbt.getInt("type")];
        this.burnout = nbt.getInt("burnout");
        this.brainDamage = nbt.getInt("brain_damage");
        this.brainDamageTimer = nbt.getInt("brain_damage_timer");
        this.mode = TenShadowsMode.values()[nbt.getInt("mode")];
        this.charge = nbt.getInt("charge");
        this.lastBlackFlashTime = nbt.getLong("last_black_flash_time");
        this.speedStacks = nbt.getInt("speed_stacks");
        this.fingers = nbt.getInt("fingers");

        this.unlocked.clear();

        for (Tag tag : nbt.getList("unlocked", Tag.TAG_STRING)) {
            this.unlocked.add(JJKAbilities.getValue(new ResourceLocation(tag.getAsString())));
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
            this.traits.add(Trait.values()[((IntTag) key).getAsInt()]);
        }

        this.cooldowns.clear();

        for (Tag key : nbt.getList("cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) key;
            this.cooldowns.put(JJKAbilities.getValue(new ResourceLocation(data.getString("identifier"))),
                    data.getInt("cooldown"));
        }

        this.summons.clear();

        ListTag summonsTag = nbt.getList("summons", Tag.TAG_INT);

        for (int i = 0; i < summonsTag.size(); i++) {
            this.summons.add(summonsTag.getInt(i));
        }

        this.acceptedPacts.clear();

        for (Tag key : nbt.getList("accepted_pacts", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) key;

            Set<Pact> pacts = new HashSet<>();

            for (Tag entry : data.getList("entries", Tag.TAG_INT)) {
                pacts.add(Pact.values()[((IntTag) entry).getAsInt()]);
            }
            this.acceptedPacts.put(data.getUUID("recipient"), pacts);
        }

        this.bindingVows.clear();

        for (Tag key : nbt.getList("binding_vows", Tag.TAG_INT)) {
            this.bindingVows.add(BindingVow.values()[((IntTag) key).getAsInt()]);
        }

        this.bindingVowCooldowns.clear();

        for (Tag key : nbt.getList("binding_vow_cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) key;
            this.bindingVowCooldowns.put(BindingVow.values()[data.getInt("vow")], data.getInt("cooldown"));
        }

        this.chants.clear();

        for (Tag key : nbt.getList("chants", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) key;

            Set<String> chants = new LinkedHashSet<>();

            for (Tag entry : data.getList("entries", Tag.TAG_STRING)) {
                chants.add(entry.getAsString());
            }
            this.chants.put(JJKAbilities.getValue(new ResourceLocation(data.getString("ability"))), chants);
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
            this.curses.add(new AbsorbedCurse(curse));
        }
    }
}
