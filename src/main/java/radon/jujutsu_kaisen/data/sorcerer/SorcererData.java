package radon.jujutsu_kaisen.data.sorcerer;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
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
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.item.cursed_tool.MimicryKatanaItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.tags.JJKStructureTags;
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

    private final Set<ICursedTechnique> additional;
    private @Nullable ICursedTechnique currentAdditional;

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
    private final Set<SummonData> summons;

    private int fingers;

    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("4979087e-da76-4f8a-93ef-6e5847bfa2ee");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("a2aef906-ed31-49e8-a56c-decccbfa2c1f");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("9fe023ca-f22b-4429-a5e5-c099387d5441");
    private static final UUID HEALTH_UUID = UUID.fromString("7d1ab920-e9ff-11ee-bd3d-0242ac120002");

    private final LivingEntity owner;

    public SorcererData(LivingEntity owner) {
        this.owner = owner;

        this.domainSize = 1.0F;

        this.unlocked = new HashSet<>();

        this.additional = new HashSet<>();

        this.nature = CursedEnergyNature.BASIC;

        this.type = JujutsuType.SORCERER;

        this.output = 1.0F;

        this.lastBlackFlashTime = -1;

        this.traits = new HashSet<>();
        this.summons = new HashSet<>();
    }

    private void updateSummons() {
        if (this.owner.level().isClientSide) return;

        boolean dirty = false;

        for (ServerLevel level : ((ServerLevel) this.owner.level()).getServer().getAllLevels()) {
            Iterator<SummonData> iter = this.summons.iterator();

            while (iter.hasNext()) {
                SummonData data = iter.next();

                if (level.dimension() != data.getDimension() || !level.areEntitiesLoaded(data.getChunkPos())) continue;

                Entity entity = level.getEntity(data.getId());

                if (entity == null || !entity.isAlive() || entity.isRemoved()) {
                    dirty = true;
                    iter.remove();
                    continue;
                }
                data.setChunkPos(entity.chunkPosition().toLong());
            }
        }

        if (dirty && this.owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(this.serializeNBT()), player);
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
        if (this.traits.contains(Trait.HEAVENLY_RESTRICTION_BODY) || this.traits.contains(Trait.HEAVENLY_RESTRICTION_SORCERY)) PlayerUtil.giveAdvancement(player, "heavenly_restriction");
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

        /*
         * Applies the attribute modifiers to the player with heavenly restrictions
         */
        if (this.traits.contains(Trait.HEAVENLY_RESTRICTION_BODY)) {
            double damage = this.getBaseOutput() * 3.0D;
            EntityUtil.applyModifier(this.owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", damage, AttributeModifier.Operation.ADDITION);

            double speed = this.getBaseOutput();
            EntityUtil.applyModifier(this.owner, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID, "Attack speed", speed, AttributeModifier.Operation.ADDITION);

            double movement = this.getBaseOutput() * 0.05D;
            EntityUtil.applyModifier(this.owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed", Math.min(this.owner.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 2,  movement), AttributeModifier.Operation.ADDITION);

            if (this.owner.getHealth() < this.owner.getMaxHealth()) {
                this.owner.heal(1.0F / 20);
            }
        } else if (this.traits.contains(Trait.HEAVENLY_RESTRICTION_SORCERY)) {
            EntityUtil.applyModifier(this.owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", 0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
            EntityUtil.applyModifier(this.owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed", 0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
            EntityUtil.applyModifier(this.owner, Attributes.MAX_HEALTH, HEALTH_UUID, "Health", -14, AttributeModifier.Operation.ADDITION);
        }
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
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
        return this.getAbilityOutput() * ChantHandler.getOutput(this.owner, ability);
    }

    @Override
    public float getAbilityOutput() {
        float power = this.getBaseOutput() * this.getOutput();

        if (this.hasTrait(Trait.HEAVENLY_RESTRICTION_SORCERY)) power *= 1.5F;

        if (this.hasSummonOfClass(SimpleDomainEntity.class) || this.hasSummonOfClass(DomainExpansionEntity.class)) {
            power *= 1.2F;
        }

        if (this.isInZone()) power *= 1.2F;

        return power;
    }

    @Override
    public float getBaseOutput() {
        IJujutsuCapability cap = this.owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISkillData data = cap.getSkillData();
        return 1.0F + (data.getSkill(this.traits.contains(Trait.HEAVENLY_RESTRICTION_BODY) ? Skill.STRENGTH : Skill.OUTPUT) * 0.1F);
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

        if (this.currentAdditional != null) {
            techniques.add(this.currentAdditional);
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

        techniques.addAll(this.additional);
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
        if (this.traits.contains(Trait.HEAVENLY_RESTRICTION_BODY)) {
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

        float amount = ConfigHolder.SERVER.cursedEnergyAmount.get().floatValue() * ((1.0F + (skillData.getSkill(Skill.ENERGY)) * 0.5F));

        amount += this.extraEnergy;

        if (contractData.hasBindingVow(JJKBindingVows.OVERTIME.get())) {
            long time = this.owner.level().getLevelData().getDayTime();
            boolean night = time >= 13000 && time < 24000;
            amount *= night ? 1.2F : 0.9F;
        }

        if (cap.getSorcererData().hasTrait(Trait.HEAVENLY_RESTRICTION_SORCERY)) amount *= 10.0F;

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
        // Second black flash restores output
        if (this.isInZone()) {
            this.burnout = 0;
        }

        this.lastBlackFlashTime = this.owner.level().getGameTime();

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
        this.summons.add(new SummonData(entity));
    }

    @Override
    public void removeSummon(Entity entity) {
        this.summons.removeIf(data -> data.getId() == entity.getId());
    }

    @Override
    public List<Entity> getSummons() {
        List<Entity> entities = new ArrayList<>();

        for (SummonData data : this.summons) {
            Entity entity = this.owner.level().getEntity(data.getId());

            if (entity == null) continue;

            entities.add(entity);
        }
        return entities;
    }

    @Override
    public <T extends Entity> @Nullable T getSummonByClass(Class<T> clazz) {
        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        for (SummonData data : this.summons) {
            Entity entity = this.owner.level().getEntity(data.getId());

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
        List<T> entities = new ArrayList<>();

        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        for (SummonData data : this.summons) {
            Entity entity = this.owner.level().getEntity(data.getId());

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
        EntityTypeTest<Entity, T> test = EntityTypeTest.forClass(clazz);

        Iterator<SummonData> iter = this.summons.iterator();

        while (iter.hasNext()) {
            SummonData data = iter.next();

            Entity entity = this.owner.level().getEntity(data.getId());

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

        Iterator<SummonData> iter = this.summons.iterator();

        while (iter.hasNext()) {
            SummonData data = iter.next();

            Entity entity = this.owner.level().getEntity(data.getId());

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

        for (SummonData data : this.summons) {
            Entity entity = this.owner.level().getEntity(data.getId());

            if (entity == null) continue;

            T summon = test.tryCast(entity);

            if (summon != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Where the randomization of the player happens when he spawns in the world
     */
    @Override
    public void generate(ServerPlayer owner) {
        this.initialized = true;

        this.technique = null;

        this.nature = CursedEnergyNature.BASIC;

        this.traits.remove(Trait.SIX_EYES);
        this.traits.remove(Trait.HEAVENLY_RESTRICTION_BODY);
        this.traits.remove(Trait.HEAVENLY_RESTRICTION_SORCERY);
        this.traits.remove(Trait.VESSEL);

        Set<ICursedTechnique> taken = new HashSet<>();
        Set<Trait> traits = new HashSet<>();

        if (ConfigHolder.SERVER.uniqueTraits.get()) {
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

        if ((!ConfigHolder.SERVER.uniqueTraits.get() || !traits.contains(Trait.HEAVENLY_RESTRICTION_BODY)) &&
                HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.heavenlyRestrictionRarity.get()) == 0) {
            this.addTrait(Trait.HEAVENLY_RESTRICTION_BODY);
        } else {
            if ((!ConfigHolder.SERVER.uniqueTraits.get() || !traits.contains(Trait.HEAVENLY_RESTRICTION_SORCERY)) &&
                    HelperMethods.RANDOM.nextInt(ConfigHolder.SERVER.heavenlyRestrictionRarity.get()) == 0) {
                this.addTrait(Trait.HEAVENLY_RESTRICTION_SORCERY);
            }

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

        ServerLevel level = ((ServerLevel) owner.level());
        BlockPos pos = level.findNearestMapStructure(JJKStructureTags.HAS_SORCERERS, owner.blockPosition(), 100, true);

        if (pos != null) {
            ItemStack stack = MapItem.create(owner.level(), pos.getX(), pos.getZ(), (byte) 2, true, true);
            MapItem.renderBiomePreviewMap(level, stack);
            MapItemSavedData.addTargetDecoration(stack, pos, "+", MapDecoration.Type.RED_X);

            stack.setHoverName(Component.translatable(String.format("item.%s.headquarters_map", JujutsuKaisen.MOD_ID)));

            owner.addItem(stack);
        }
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
    public void addFingers(int count) {
        this.fingers += count;
    }

    @Override
    public void addAdditional(ICursedTechnique technique) {
        this.additional.add(technique);
    }

    @Override
    public void removeAdditional(ICursedTechnique technique) {
        if (this.currentAdditional == technique) {
            this.currentAdditional = null;
            ServerVisualHandler.sync(this.owner);
        }
        this.additional.remove(technique);
    }

    @Override
    public boolean hasAdditional(ICursedTechnique technique) {
        return this.additional.contains(technique);
    }

    @Override
    public @Nullable ICursedTechnique getCurrentAdditional() {
        return this.currentAdditional;
    }

    @Override
    public void setCurrentAdditional(@Nullable ICursedTechnique technique) {
        this.currentAdditional = technique;
        ServerVisualHandler.sync(this.owner);
    }

    @Override
    public Set<ICursedTechnique> getAdditional() {
        return this.additional;
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

        ListTag additionalTag = new ListTag();

        for (ICursedTechnique technique : this.additional) {
            additionalTag.add(StringTag.valueOf(JJKCursedTechniques.getKey(technique).toString()));
        }
        nbt.put("additional", additionalTag);

        if (this.currentAdditional != null) {
            nbt.putString("current_additional", JJKCursedTechniques.getKey(this.currentAdditional).toString());
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

        ListTag summonsTag = new ListTag();

        for (SummonData data : this.summons) {
            summonsTag.add(data.serializeNBT());
        }
        nbt.put("summons", summonsTag);

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

        for (Tag tag : nbt.getList("additional", Tag.TAG_STRING)) {
            this.additional.add(JJKCursedTechniques.getValue(new ResourceLocation(tag.getAsString())));
        }

        if (nbt.contains("current_additional")) {
            this.currentAdditional = JJKCursedTechniques.getValue(new ResourceLocation(nbt.getString("current_additional")));
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

        this.summons.clear();

        for (Tag tag : nbt.getList("summons", Tag.TAG_COMPOUND)) {
            this.summons.add(new SummonData((CompoundTag) tag));
        }
    }
}
