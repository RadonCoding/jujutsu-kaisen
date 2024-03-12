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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import radon.jujutsu_kaisen.JJKConstants;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.binding_vow.JJKBindingVows;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.contract.IContractData;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.item.cursed_tool.MimicryKatanaItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.*;
import radon.jujutsu_kaisen.visual.ServerVisualHandler;

import javax.annotation.Nullable;
import java.util.*;

public class SorcererData implements ISorcererData {
    private boolean initialized;

    private int cursedEnergyColor;

    private int abilityPoints;
    private int skillPoints;

    private final Set<Ability> unlocked;

    private float domainSize;

    private @Nullable ICursedTechnique technique;

    private @Nullable ICursedTechnique additional;

    private CursedEnergyNature nature;

    private float experience;
    private float current;
    private float output;

    private float energy;
    private float maxEnergy;
    private float extraEnergy;

    private JujutsuType type;

    private int burnout;
    private int brainDamage;
    private int brainDamageTimer;

    private long lastBlackFlashTime;

    private final Set<Trait> traits;
    private final Set<UUID> summons;

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

        this.output = 1.0F;

        this.lastBlackFlashTime = -1;

        this.traits = new HashSet<>();
        this.summons = new HashSet<>();
    }

    private void updateSummons() {
        if (!(this.owner.level() instanceof ServerLevel level)) return;
        if (!this.owner.level().isLoaded(this.owner.blockPosition())) return;

        Iterator<UUID> iter = this.summons.iterator();

        while (iter.hasNext()) {
            UUID identifier = iter.next();

            Entity entity = level.getEntity(identifier);

            if (entity == null || !entity.isAlive() || entity.isRemoved()) {
                iter.remove();
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
    public void tick() {
        this.updateSummons();

        this.updateBrainDamage();

        if (this.owner instanceof ServerPlayer player) {
            if (!this.initialized) {
                this.initialized = true;
                this.generate(player);
            }
            this.checkAdvancements(player);
        }

        if (this.burnout > 0) {
            this.burnout--;
        }

        this.energy = Math.min(this.getMaxEnergy(), this.energy + (ConfigHolder.SERVER.cursedEnergyRegenerationAmount.get().floatValue() * (this.owner instanceof Player player ? (player.getFoodData().getFoodLevel() / 20.0F) : 1.0F)));

        IJujutsuCapability cap = this.owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISkillData data = cap.getSkillData();

        if (this.traits.contains(Trait.HEAVENLY_RESTRICTION)) {
            double health = Math.ceil(((((data.getSkill(Skill.REINFORCEMENT) - 1) * 0.1D) - 1.0F) * 30.0D) / 20) * 20;

            if (EntityUtil.applyModifier(this.owner, Attributes.MAX_HEALTH, MAX_HEALTH_UUID, "Max health", health, AttributeModifier.Operation.ADDITION)) {
                this.owner.setHealth(this.owner.getMaxHealth());
            }

            double damage = this.getBaseOutput() * 3.0D;
            EntityUtil.applyModifier(this.owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", damage, AttributeModifier.Operation.ADDITION);

            double speed = this.getBaseOutput();
            EntityUtil.applyModifier(this.owner, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID, "Attack speed", speed, AttributeModifier.Operation.ADDITION);

            double movement = this.getBaseOutput() * 0.05D;
            EntityUtil.applyModifier(this.owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed", Math.min(this.owner.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 2,  movement), AttributeModifier.Operation.ADDITION);

            if (this.owner.getHealth() < this.owner.getMaxHealth()) {
                this.owner.heal(2.0F / 20);
            }
        } else {
            double health = Math.ceil((((data.getSkill(Skill.REINFORCEMENT) - 1) * 0.1D) * 20.0D) / 20) * 20;

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
        return Math.max(0.1F, 1.0F - ((float) this.brainDamage / JJKConstants.MAX_BRAIN_DAMAGE));
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
    public int getAbilityPoints() {
        return this.abilityPoints;
    }

    @Override
    public void setAbilityPoints(int points) {
        this.abilityPoints = points;
    }

    @Override
    public void addAbilityPoints(int points) {
        this.abilityPoints += points;
    }

    @Override
    public void useAbilityPoints(int count) {
        this.abilityPoints -= count;
    }

    @Override
    public int getSkillPoints() {
        return this.skillPoints;
    }

    @Override
    public void setSkillPoints(int points) {
        this.skillPoints = points;
    }

    @Override
    public void addSkillPoints(int points) {
        this.skillPoints += points;
    }

    @Override
    public void useSkillPoints(int count) {
        this.skillPoints -= count;
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
    public float getOutput() {
        return Math.min(this.getMaximumOutput(), this.output);
    }

    @Override
    public float getAbilityOutput(Ability ability) {
        IJujutsuCapability cap = this.owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        IAbilityData data = cap.getAbilityData();

        float power = this.getBaseOutput() * ChantHandler.getOutput(this.owner, ability);

        if (this.technique != null) {
            Ability domain = this.technique.getDomain();

            if (domain != null && data.hasToggled(domain)) {
                power *= 1.2F;
            }
        }

        if (this.isInZone()) power *= 1.2F;

        return power;
    }

    @Override
    public float getAbilityOutput() {
        IJujutsuCapability cap = this.owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        IAbilityData data = cap.getAbilityData();

        float power = this.getBaseOutput() * this.getOutput();

        if (this.technique != null) {
            Ability domain = this.technique.getDomain();

            if (domain != null && data.hasToggled(domain)) {
                power *= 1.2F;
            }
        }
        return power;
    }

    @Override
    public float getBaseOutput() {
        if (this.traits.contains(Trait.HEAVENLY_RESTRICTION)) {
            // Just makes it so heavenly restricted people's output scales with experience
            return 1.0F + (Math.min(ConfigHolder.SERVER.maximumSkillLevel.get(), this.experience / ConfigHolder.SERVER.skillPointInterval.get().floatValue()) * 0.1F);
        }

        IJujutsuCapability cap = this.owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISkillData data = cap.getSkillData();

        return 1.0F + (data.getSkill(Skill.OUTPUT) * 0.1F);
    }

    @Override
    public float getExperience() {
        return this.experience;
    }

    @Override
    public void setExperience(float experience) {
        this.experience = experience;

        int abilityPoints = Math.round(this.experience / ConfigHolder.SERVER.abilityPointInterval.get().floatValue());
        int skillPoints = Math.round(this.experience / ConfigHolder.SERVER.skillPointInterval.get().floatValue());

        this.abilityPoints = abilityPoints;
        this.skillPoints = skillPoints;

        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public boolean addExperience(float amount) {
        SorcererGrade previous = SorcererUtil.getGrade(this.experience);

        this.setExperience(this.experience + amount);

        this.current += amount;

        int abilityPoints = Math.round(this.current / ConfigHolder.SERVER.abilityPointInterval.get().floatValue());

        if (abilityPoints > 0) {
            this.abilityPoints += abilityPoints;

            if (!this.owner.level().isClientSide && this.owner instanceof Player) {
                this.owner.sendSystemMessage(Component.translatable(String.format("chat.%s.ability_points", JujutsuKaisen.MOD_ID), abilityPoints));
            }
        }

        int skillPoints = Math.round(this.current / ConfigHolder.SERVER.skillPointInterval.get().floatValue());

        if (skillPoints > 0) {
            this.skillPoints += skillPoints;

            if (!this.owner.level().isClientSide && this.owner instanceof Player) {
                this.owner.sendSystemMessage(Component.translatable(String.format("chat.%s.skill_points", JujutsuKaisen.MOD_ID), skillPoints));
            }
        }

        if (this.current > Math.max(ConfigHolder.SERVER.abilityPointInterval.get().floatValue(), ConfigHolder.SERVER.skillPointInterval.get().floatValue())) {
            this.current = 0.0F;
        }

        SorcererGrade current = SorcererUtil.getGrade(this.experience);

        if (!this.owner.level().isClientSide && this.owner instanceof Player) {
            if (previous != current) {
                this.owner.sendSystemMessage(Component.translatable(String.format("chat.%s.rank_up", JujutsuKaisen.MOD_ID), current.getName()));
            }
        }
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

        IAbilityData abilityData = cap.getAbilityData();
        ICurseManipulationData curseManipulationData = cap.getCurseManipulationData();
        IMimicryData mimicryData = cap.getMimicryData();

        Set<ICursedTechnique> techniques = new HashSet<>();

        if (this.technique != null) {
            techniques.add(this.technique);
        }

        if (this.additional != null) {
            techniques.add(this.additional);
        }

        if (abilityData.hasToggled(JJKAbilities.RIKA.get())) {
            ICursedTechnique copied = mimicryData.getCurrentCopied();

            if (copied != null) {
                techniques.add(copied);
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
        IMimicryData mimicryData = cap.getMimicryData();

        Set<ICursedTechnique> techniques = new HashSet<>();

        if (this.technique != null) {
            techniques.add(this.technique);
        }

        if (this.additional != null) {
            techniques.add(this.additional);
        }

        techniques.addAll(mimicryData.getCopied());
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
        if (this.maxEnergy > 0.0F) return this.maxEnergy;

        IJujutsuCapability cap = this.owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        IContractData contractData = cap.getContractData();
        ISkillData skillData = cap.getSkillData();

        float amount = ConfigHolder.SERVER.cursedEnergyAmount.get().floatValue() * (1 + skillData.getSkill(Skill.ENERGY));

        amount += this.extraEnergy;

        if (contractData.hasBindingVow(JJKBindingVows.OVERTIME.get())) {
            long time = this.owner.level().getLevelData().getDayTime();
            boolean night = time >= 13000 && time < 24000;
            amount *= night ? 1.2F : 0.9F;
        }
        return amount;
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

        this.burnout = 0;

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
    public void addSummon(Entity entity) {
        this.summons.add(entity.getUUID());
    }

    @Override
    public void removeSummon(Entity entity) {
        this.summons.remove(entity.getUUID());
    }

    @Override
    public List<Entity> getSummons() {
        if (!(this.owner.level() instanceof ServerLevel level)) return List.of();

        List<Entity> entities = new ArrayList<>();

        for (UUID identifier : this.summons) {
            Entity entity = level.getEntity(identifier);

            if (entity == null) continue;

            entities.add(entity);
        }
        return entities;
    }

    @Override
    public <T extends Entity> @Nullable T getSummonByClass(Class<T> clazz) {
        if (!(this.owner.level() instanceof ServerLevel level)) return null;

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
    public <T extends Entity> List<T> getSummonsByClass(Class<T> clazz) {
        if (!(this.owner.level() instanceof ServerLevel level)) return List.of();

        List<T> entities = new ArrayList<>();

        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        for (UUID identifier : this.summons) {
            Entity entity = level.getEntity(identifier);

            if (entity == null) continue;

            T summon = test.tryCast(entity);

            if (summon != null) {
                entities.add(summon);
            }
        }
        return entities;
    }

    @Override
    public <T extends Entity> void unsummonByClass(Class<T> clazz) {
        if (!(this.owner.level() instanceof ServerLevel level)) return;

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
    public <T extends Entity> void removeSummonByClass(Class<T> clazz) {
        if (!(this.owner.level() instanceof ServerLevel level)) return;

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
    public <T extends Entity> boolean hasSummonOfClass(Class<T> clazz) {
        if (!(this.owner.level() instanceof ServerLevel level)) return false;

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

        if ((!ConfigHolder.SERVER.uniqueTraits.get() || !traits.contains(Trait.HEAVENLY_RESTRICTION)) &&
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

            if ((!ConfigHolder.SERVER.uniqueTraits.get() || !traits.contains(Trait.VESSEL)) && this.type == JujutsuType.SORCERER &&
                    HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.vesselRarity.get()) == 0) {
                this.addTrait(Trait.VESSEL);
            }

            if ((!ConfigHolder.SERVER.uniqueTraits.get() || !traits.contains(Trait.SIX_EYES)) &&
                    HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.sixEyesRarity.get()) == 0) {
                this.addTrait(Trait.SIX_EYES);
            }

            if (this.technique != null) {
                owner.sendSystemMessage(Component.translatable(String.format("chat.%s.technique", JujutsuKaisen.MOD_ID), this.technique.getName()));
            }

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
        nbt.putInt("ability_points", this.abilityPoints);
        nbt.putInt("skill_points", this.skillPoints);
        nbt.putFloat("domain_size", this.domainSize);

        if (this.technique != null) {
            nbt.putString("technique", JJKCursedTechniques.getKey(this.technique).toString());
        }
        if (this.additional != null) {
            nbt.putString("additional", JJKCursedTechniques.getKey(this.additional).toString());
        }
        nbt.putInt("nature", this.nature.ordinal());
        nbt.putFloat("experience", this.experience);
        nbt.putFloat("current", this.current);
        nbt.putFloat("output", this.output);
        nbt.putFloat("energy", this.energy);
        nbt.putFloat("max_energy", this.maxEnergy);
        nbt.putFloat("extra_energy", this.extraEnergy);
        nbt.putInt("type", this.type.ordinal());
        nbt.putInt("burnout", this.burnout);
        nbt.putInt("brain_damage", this.brainDamage);
        nbt.putInt("brain_damage_timer", this.brainDamageTimer);
        nbt.putLong("last_black_flash_time", this.lastBlackFlashTime);
        nbt.putInt("fingers", this.fingers);

        ListTag unlockedTag = new ListTag();

        for (Ability ability : this.unlocked) {
            ResourceLocation key = JJKAbilities.getKey(ability);

            if (key == null) continue;

            unlockedTag.add(StringTag.valueOf(key.toString()));
        }
        nbt.put("unlocked", unlockedTag);

        nbt.put("traits", new IntArrayTag(this.traits.stream().map(Enum::ordinal).toList()));

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.initialized = nbt.getBoolean("initialized");

        this.cursedEnergyColor = nbt.getInt("cursed_energy_color");

        this.abilityPoints = nbt.getInt("ability_points");
        this.skillPoints = nbt.getInt("skill_points");
        this.domainSize = nbt.getFloat("domain_size");

        if (nbt.contains("technique")) {
            this.technique = JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("technique")));
        }
        if (nbt.contains("additional")) {
            this.additional = JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("additional")));
        }
        this.nature = CursedEnergyNature.values()[nbt.getInt("nature")];
        this.experience = nbt.getFloat("experience");
        this.current = nbt.getFloat("current");
        this.output = nbt.getFloat("output");
        this.energy = nbt.getFloat("energy");
        this.maxEnergy = nbt.getFloat("max_energy");
        this.extraEnergy = nbt.getFloat("extra_energy");
        this.type = JujutsuType.values()[nbt.getInt("type")];
        this.burnout = nbt.getInt("burnout");
        this.brainDamage = nbt.getInt("brain_damage");
        this.brainDamageTimer = nbt.getInt("brain_damage_timer");
        this.lastBlackFlashTime = nbt.getLong("last_black_flash_time");
        this.fingers = nbt.getInt("fingers");

        this.unlocked.clear();

        for (Tag tag : nbt.getList("unlocked", Tag.TAG_STRING)) {
            this.unlocked.add(JJKAbilities.getValue(new ResourceLocation(tag.getAsString())));
        }

        this.traits.clear();

        for (int index : nbt.getIntArray("traits")) {
            this.traits.add(Trait.values()[index]);
        }
    }
}
