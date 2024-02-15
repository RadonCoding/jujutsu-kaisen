package radon.jujutsu_kaisen.data.sorcerer;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import radon.jujutsu_kaisen.JJKConstants;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityStopEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.item.cursed_tool.MimicryKatanaItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.*;
import radon.jujutsu_kaisen.visual.ServerVisualHandler;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class SorcererData implements ISorcererData {
    private boolean initialized;

    private int cursedEnergyColor;

    private int points;
    private final Set<Ability> unlocked;

    private float domainSize;

    private @Nullable ICursedTechnique technique;

    private @Nullable ICursedTechnique additional;

    private final Set<ICursedTechnique> copied;
    private @Nullable ICursedTechnique currentCopied;

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
    private final Map<Ability, Integer> disrupted;
    private final Map<Ability, Integer> durations;
    private final Set<Integer> summons;
    private final Map<UUID, Set<Pact>> acceptedPacts;
    private final Map<UUID, Set<Pact>> requestedPactsCreations;
    private final Map<UUID, Integer> createRequestExpirations;
    private final Map<UUID, Set<Pact>> requestedPactsRemovals;
    private final Set<BindingVow> bindingVows;
    private final Map<BindingVow, Integer> bindingVowCooldowns;
    private final Map<Ability, Set<String>> chants;

    private int fingers;

    private static final UUID MAX_HEALTH_UUID = UUID.fromString("72ff5080-3a82-4a03-8493-3be970039cfe");
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("4979087e-da76-4f8a-93ef-6e5847bfa2ee");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("a2aef906-ed31-49e8-a56c-decccbfa2c1f");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("9fe023ca-f22b-4429-a5e5-c099387d5441");

    private final LivingEntity owner;

    public SorcererData(LivingEntity owner) {
        this.owner = owner;

        this.domainSize = 1.0F;

        this.unlocked = new HashSet<>();

        this.nature = CursedEnergyNature.BASIC;

        this.type = JujutsuType.SORCERER;

        this.copied = new LinkedHashSet<>();

        this.output = 1.0F;

        this.lastBlackFlashTime = -1;

        this.toggled = new HashSet<>();
        this.traits = new HashSet<>();
        this.delayedTickEvents = new ArrayList<>();
        this.cooldowns = new HashMap<>();
        this.disrupted = new HashMap<>();
        this.durations = new HashMap<>();
        this.summons = new HashSet<>();
        this.acceptedPacts = new HashMap<>();
        this.requestedPactsCreations = new HashMap<>();
        this.createRequestExpirations = new HashMap<>();
        this.requestedPactsRemovals = new HashMap<>();
        this.bindingVows = new HashSet<>();
        this.bindingVowCooldowns = new HashMap<>();
        this.chants = new HashMap<>();
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

    private void updateDisrupted() {
        Iterator<Map.Entry<Ability, Integer>> iter = this.disrupted.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Ability, Integer> entry = iter.next();

            int remaining = entry.getValue();

            if (remaining > 0) {
                this.disrupted.put(entry.getKey(), --remaining);
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
            if (this.disrupted.containsKey(ability)) continue;

            Ability.Status status = ability.isStillUsable(this.owner);

            if (status == Ability.Status.SUCCESS || status == Ability.Status.COOLDOWN || (status == Ability.Status.ENERGY && ability instanceof Ability.IAttack)) {
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
            if (this.disrupted.containsKey(this.channeled)) return;

            Ability.Status status = this.channeled.isStillUsable(this.owner);

            if (status == Ability.Status.SUCCESS || status == Ability.Status.COOLDOWN || (status == Ability.Status.ENERGY && this.channeled instanceof Ability.IAttack)) {
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

            this.output = this.getMaximumOutput();
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
    public void tick() {
        this.updateSummons();

        this.updateCooldowns();
        this.updateDisrupted();
        this.updateDurations();
        this.updateTickEvents();
        this.updateToggled();
        this.updateChanneled();

        this.updateRequestExpirations();
        this.updateBindingVowCooldowns();

        this.updateBrainDamage();

        if (!this.owner.level().isClientSide) {
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

            double damage = this.getRealPower() * 3.0D;
            EntityUtil.applyModifier(this.owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", damage, AttributeModifier.Operation.ADDITION);

            double speed = this.getRealPower();
            EntityUtil.applyModifier(this.owner, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID, "Attack speed", speed, AttributeModifier.Operation.ADDITION);

            double movement = this.getRealPower() * 0.05D;
            EntityUtil.applyModifier(this.owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed", Math.min(this.owner.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 2,  movement), AttributeModifier.Operation.ADDITION);

            if (this.owner.getHealth() < this.owner.getMaxHealth()) {
                this.owner.heal(2.0F / 20);
            }
        } else {
            double health = Math.ceil(((this.getRealPower() - 1.0F) * 20.0D) / 20) * 20;

            if (EntityUtil.applyModifier(this.owner, Attributes.MAX_HEALTH, MAX_HEALTH_UUID, "Max health", health, AttributeModifier.Operation.ADDITION)) {
                this.owner.setHealth(this.owner.getMaxHealth());
            }
        }
    }

    @Override
    public int getCursedEnergyColor() {
        return this.cursedEnergyColor == 0 ? HelperMethods.getRGB24(ParticleColors.getCursedEnergyColor(this.type)) : this.cursedEnergyColor;
    }

    @Override
    public void setCursedEnergyColor(int color) {
        this.cursedEnergyColor = color;
        ServerVisualHandler.sync(this.owner);
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
    public Set<Ability> getUnlocked() {
        return this.unlocked;
    }

    @Override
    public void unlock(Ability ability) {
        this.unlocked.add(ability);
    }

    @Override
    public void unlockAll(Set<Ability> abilities) {
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
                power *= 1.2F;
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
        ServerVisualHandler.sync(this.owner);
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
        ServerVisualHandler.sync(this.owner);
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

    public @Nullable ICursedTechnique getTechnique() {
        return this.technique;
    }

    @Override
    public void setTechnique(@Nullable ICursedTechnique technique) {
        this.technique = technique;
        ServerVisualHandler.sync(this.owner);
    }

    private Set<ICursedTechnique> getMimicryTechniques() {
        Set<ICursedTechnique> techniques = new HashSet<>();

        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(this.owner.getItemInHand(InteractionHand.MAIN_HAND));
        stacks.addAll(CuriosUtil.findSlots(this.owner, this.owner.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand"));
        stacks.removeIf(ItemStack::isEmpty);

        for (ItemStack stack : stacks) {
            if (!(stack.getItem() instanceof MimicryKatanaItem)) continue;
            techniques.add(MimicryKatanaItem.getTechnique(stack));
        }
        return techniques;
    }

    @Override
    public Set<ICursedTechnique> getActiveTechniques() {
        IJujutsuCapability cap = this.owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Set.of();

        ICurseManipulationData curseManipulationData = cap.getCurseManipulationData();

        Set<ICursedTechnique> techniques = new HashSet<>();

        if (this.technique != null) {
            techniques.add(this.technique);
        }

        if (this.additional != null) {
            techniques.add(this.additional);
        }

        if (this.toggled.contains(JJKAbilities.RIKA.get())) {
            if (this.currentCopied != null) {
                techniques.add(this.currentCopied);
            }
        }

        ICursedTechnique absorbed = curseManipulationData.getCurrentAbsorbed();

        if (absorbed != null) {
            techniques.add(absorbed);
        }
        techniques.addAll(this.getMimicryTechniques());

        return techniques;
    }

    @Override
    public Set<ICursedTechnique> getTechniques() {
        IJujutsuCapability cap = this.owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return Set.of();

        ICurseManipulationData curseManipulationData = cap.getCurseManipulationData();

        Set<ICursedTechnique> techniques = new HashSet<>();

        if (this.technique != null) {
            techniques.add(this.technique);
        }

        if (this.additional != null) {
            techniques.add(this.additional);
        }

        techniques.addAll(this.copied);
        techniques.addAll(curseManipulationData.getAbsorbed());
        techniques.addAll(this.getMimicryTechniques());

        return techniques;
    }

    @Override
    public boolean hasTechnique(ICursedTechnique technique) {
        return this.getTechniques().contains(technique);
    }

    @Override
    public boolean hasActiveTechnique(ICursedTechnique technique) {
        return this.getActiveTechniques().contains(technique);
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
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public void addTraits(List<Trait> traits) {
        this.traits.addAll(traits);
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public void removeTrait(Trait trait) {
        this.traits.remove(trait);
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public Set<Trait> getTraits() {
        return this.traits;
    }

    @Override
    public void setTraits(Set<Trait> traits) {
        this.traits.clear();
        this.traits.addAll(traits);
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public void setType(JujutsuType type) {
        this.type = type;
        ServerVisualHandler.sync(this.owner);
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

            NeoForge.EVENT_BUS.post(new AbilityStopEvent(this.owner, ability));
        } else {
            this.toggled.add(ability);

            ((Ability.IToggled) ability).onEnabled(this.owner);
        }
        ServerVisualHandler.sync(this.owner);
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
    public void resetBrainDamage() {
        this.brainDamage = 0;
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
    public void disrupt(Ability ability, int duration) {
        this.disrupted.put(ability, duration);
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
        return Math.min(this.getMaxEnergy(), this.energy);
    }

    @Override
    public void addEnergy(float amount) {
        this.energy = Math.min(this.getMaxEnergy(), this.energy + amount);
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public float getMaxEnergy() {
        long time = this.owner.level().getLevelData().getDayTime();
        boolean night = time >= 13000 && time < 24000;
        return (this.bindingVows.contains(BindingVow.OVERTIME) ? night ? 1.2F : 0.9F : 1.0F) *
                ((this.maxEnergy == 0.0F ? ConfigHolder.SERVER.cursedEnergyAmount.get().floatValue() : this.maxEnergy) *
                        this.getRealPower() * (float) Math.log(this.getRealPower() + 1)) + this.extraEnergy;
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
    public void uncopy(ICursedTechnique technique) {
        if (this.currentCopied == technique) {
            this.currentCopied = null;
        }
        this.copied.remove(technique);
    }

    @Override
    public void copy(@Nullable ICursedTechnique technique) {
        this.copied.add(technique);
    }

    @Override
    public Set<ICursedTechnique> getCopied() {
        return this.copied;
    }

    @Override
    public void setCurrentCopied(@Nullable ICursedTechnique technique) {
        this.currentCopied = this.currentCopied == technique ? null : technique;
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public @Nullable ICursedTechnique getCurrentCopied() {
        return this.currentCopied;
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
    public void useTransfiguredSouls(int amount) {
        this.transfiguredSouls -= amount;
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
            NeoForge.EVENT_BUS.post(new AbilityStopEvent(this.owner, ability));
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
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public boolean isChanneling(Ability ability) {
        return this.channeled == ability && !this.disrupted.containsKey(this.channeled);
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
    public void generate(ServerPlayer owner) {
        this.initialized = true;

        this.technique = null;

        this.nature = CursedEnergyNature.BASIC;

        this.traits.remove(Trait.SIX_EYES);
        this.traits.remove(Trait.HEAVENLY_RESTRICTION);
        this.traits.remove(Trait.VESSEL);

        Set<ICursedTechnique> taken = new HashSet<>();
        Set<Trait> traits = new HashSet<>();

        if (ConfigHolder.SERVER.uniqueTraits.get() || ConfigHolder.SERVER.uniqueTraits.get()) {
            GameProfileCache cache = owner.server.getProfileCache();

            if (cache == null) throw new NullPointerException();

            for (GameProfileCache.GameProfileInfo info : cache.load()) {
                GameProfile profile = info.getProfile();

                if (profile.getId() == owner.getUUID()) continue;

                ServerPlayer player;

                if ((player = owner.server.getPlayerList().getPlayerByName(profile.getName())) == null) {
                    player = owner.server.getPlayerList().getPlayerForLogin(profile, ClientInformation.createDefault());
                    owner.server.getPlayerList().load(player);
                }

                IJujutsuCapability cap = player.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) continue;

                ISorcererData data = cap.getSorcererData();
                taken.addAll(data.getActiveTechniques());
                traits.addAll(data.getTraits());
            }
        }

        if ((!ConfigHolder.SERVER.uniqueTraits.get() || traits.contains(Trait.HEAVENLY_RESTRICTION)) &&
                HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.heavenlyRestrictionRarity.get()) == 0) {
            this.addTrait(Trait.HEAVENLY_RESTRICTION);
        } else {
            List<ICursedTechnique> unlockable = ConfigHolder.SERVER.getUnlockableTechniques();

            if (ConfigHolder.SERVER.uniqueTechniques.get()) {
                unlockable.removeAll(taken);
            }
            this.technique = unlockable.get(HelperMethods.RANDOM.nextInt(unlockable.size()));

            if (HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.cursedEnergyNatureRarity.get()) == 0) {
                this.nature = HelperMethods.randomEnum(CursedEnergyNature.class, Set.of(CursedEnergyNature.BASIC));
                owner.sendSystemMessage(Component.translatable(String.format("chat.%s.nature", JujutsuKaisen.MOD_ID), this.nature.getName()));
            }
            this.type = HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.curseRarity.get()) == 0 ? JujutsuType.CURSE : JujutsuType.SORCERER;

            if ((!ConfigHolder.SERVER.uniqueTraits.get() || traits.contains(Trait.VESSEL)) && this.type == JujutsuType.SORCERER &&
                    HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.vesselRarity.get()) == 0) {
                this.addTrait(Trait.VESSEL);
            }

            if ((!ConfigHolder.SERVER.uniqueTraits.get() || traits.contains(Trait.SIX_EYES)) &&
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
        return this.toggled.contains(ability) && !this.disrupted.containsKey(ability);
    }

    @Override
    public @Nullable ICursedTechnique getAdditional() {
        return this.additional;
    }

    @Override
    public void setAdditional(@Nullable ICursedTechnique technique) {
        this.additional = technique;
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("initialized", this.initialized);
        nbt.putInt("cursed_energy_color", this.cursedEnergyColor);
        nbt.putInt("points", this.points);
        nbt.putFloat("domain_size", this.domainSize);

        if (this.technique != null) {
            nbt.putString("technique", JJKCursedTechniques.getKey(this.technique).toString());
        }
        if (this.additional != null) {
            nbt.putString("additional", JJKCursedTechniques.getKey(this.additional).toString());
        }
        if (this.currentCopied != null) {
            nbt.putString("current_copied", JJKCursedTechniques.getKey(this.currentCopied).toString());
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
        nbt.putInt("charge", this.charge);
        nbt.putLong("last_black_flash_time", this.lastBlackFlashTime);
        nbt.putInt("fingers", this.fingers);

        ListTag unlockedTag = new ListTag();

        for (Ability ability : this.unlocked) {
            ResourceLocation key = JJKAbilities.getKey(ability);

            if (key == null) continue;

            unlockedTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("unlocked", unlockedTag);

        ListTag copiedTag = new ListTag();

        for (ICursedTechnique technique : this.copied) {
            copiedTag.add(StringTag.valueOf(JJKCursedTechniques.getKey(technique).toString()));
        }
        nbt.put("copied", copiedTag);

        ListTag toggledTag = new ListTag();

        for (Ability ability : this.toggled) {
            ResourceLocation key = JJKAbilities.getKey(ability);

            if (key == null) continue;

            toggledTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("toggled", toggledTag);

        nbt.put("traits", new IntArrayTag(this.traits.stream().map(Enum::ordinal).toList()));

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

        ListTag disruptedTag = new ListTag();

        for (Map.Entry<Ability, Integer> entry : this.disrupted.entrySet()) {
            ResourceLocation key = JJKAbilities.getKey(entry.getKey());

            if (key == null) continue;

            CompoundTag data = new CompoundTag();
            data.putString("identifier", key.toString());
            data.putInt("duration", entry.getValue());
            disruptedTag.add(data);
        }
        nbt.put("disrupted", disruptedTag);

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

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.initialized = nbt.getBoolean("initialized");

        this.cursedEnergyColor = nbt.getInt("cursed_energy_color");

        this.points = nbt.getInt("points");
        this.domainSize = nbt.getFloat("domain_size");

        if (nbt.contains("technique")) {
            this.technique = JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("technique")));
        }
        if (nbt.contains("additional")) {
            this.additional = JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("additional")));
        }
        if (nbt.contains("current_copied")) {
            this.currentCopied = JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("current_copied")));
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
        this.charge = nbt.getInt("charge");
        this.lastBlackFlashTime = nbt.getLong("last_black_flash_time");
        this.fingers = nbt.getInt("fingers");

        this.unlocked.clear();

        for (Tag tag : nbt.getList("unlocked", Tag.TAG_STRING)) {
            this.unlocked.add(JJKAbilities.getValue(new ResourceLocation(tag.getAsString())));
        }

        this.copied.clear();

        for (Tag tag : nbt.getList("copied", Tag.TAG_STRING)) {
            this.copied.add(JJKCursedTechniques.getValue(new ResourceLocation(tag.getAsString())));
        }

        this.toggled.clear();

        for (Tag key : nbt.getList("toggled", Tag.TAG_STRING)) {
            this.toggled.add(JJKAbilities.getValue(new ResourceLocation(key.getAsString())));
        }

        this.traits.clear();

        for (int index : nbt.getIntArray("traits")) {
            this.traits.add(Trait.values()[index]);
        }

        this.cooldowns.clear();

        for (Tag key : nbt.getList("cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) key;
            this.cooldowns.put(JJKAbilities.getValue(new ResourceLocation(data.getString("identifier"))),
                    data.getInt("cooldown"));
        }

        this.disrupted.clear();

        for (Tag key : nbt.getList("disrupted", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) key;
            this.disrupted.put(JJKAbilities.getValue(new ResourceLocation(data.getString("identifier"))),
                    data.getInt("duration"));
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
    }
}
