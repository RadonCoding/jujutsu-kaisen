package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncAbilityDataS2CPacket;
import radon.jujutsu_kaisen.util.DamageUtil;

import java.util.*;

public class DomainAmplification extends Ability implements IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;

        IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if ((data.hasActive(JJKAbilities.INFINITY.get()) || data.hasToggled(JJKAbilities.SOUL_REINFORCEMENT.get())) && owner.distanceTo(target) <= 3.0D) return true;

        return owner.getHealth() / owner.getMaxHealth() < 0.5F;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public boolean isCursedEnergyColor() {
        return true;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.2F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.DOMAIN;
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return null;

        ISorcererData data = cap.getSorcererData();

        ICursedTechnique technique = data.getTechnique();
        return technique == null || technique.getDomain() == null ? JJKAbilities.CURSED_ENERGY_FLOW.get() : technique.getDomain();
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.domainAmplificationCost.get();
    }

    public static class DomainAmplificationData extends SavedData {
        private static final SavedData.Factory<DomainAmplificationData> FACTORY = new SavedData.Factory<>(DomainAmplificationData::new, DomainAmplificationData::new, null);

        public static final String IDENTIFIER = "domain_amplification_data";

        private final Map<UUID, HitsNBT> hits;

        public DomainAmplificationData() {
            this.hits = new HashMap<>();
        }

        public DomainAmplificationData(CompoundTag nbt) {
            this();

            ListTag frozenTag = nbt.getList("hits", Tag.TAG_COMPOUND);

            for (Tag tag : frozenTag) {
                HitsNBT frozen = new HitsNBT((CompoundTag) tag);
                this.hits.put(frozen.getVictim(), frozen);
            }
        }

        @Override
        public @NotNull CompoundTag save(CompoundTag pCompoundTag) {
            ListTag frozenTag = new ListTag();
            frozenTag.addAll(this.hits.values());
            pCompoundTag.put("hits", frozenTag);
            return pCompoundTag;
        }

        public void hit(LivingEntity attacker, LivingEntity victim) {
            IJujutsuCapability attackerCap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);
            IJujutsuCapability victimCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (attackerCap == null || victimCap == null) return;

            if (!this.hits.containsKey(victim.getUUID())) {
                this.hits.put(victim.getUUID(), new HitsNBT(victim, 0));
            }

            ISkillData attackerData = attackerCap.getSkillData();

            int strength = Math.max(1, Math.round(attackerData.getSkill(Skill.OUTPUT) * 0.05F));

            IAbilityData victimAbilityData = victimCap.getAbilityData();
            ISkillData victimSkillData = victimCap.getSkillData();

            int required = Math.max(1, Math.round(victimSkillData.getSkill(Skill.OUTPUT) * 0.5F));

            HitsNBT nbt = this.hits.get(victim.getUUID());

            nbt.hit(strength);

            if (nbt.getHits() >= required) {
                boolean disabled = false;

                for (Ability ability : victimAbilityData.getToggled()) {
                    if (!ability.isTechnique() || ability instanceof Summon<?>) continue;

                    victimAbilityData.disrupt(ability, 20);

                    disabled = true;
                }

                if (disabled) {
                    victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.GLASS_BREAK, SoundSource.MASTER, 2.0F, 1.0F);

                    if (victim instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncAbilityDataS2CPacket(victimAbilityData.serializeNBT()), player);
                    }
                }
            }
            this.setDirty();
        }

        public void tick(ServerLevel level) {
            Iterator<HitsNBT> iter = this.hits.values().iterator();

            while (iter.hasNext()) {
                HitsNBT nbt = iter.next();

                int duration = nbt.getDuration();

                if (--duration == 0) {
                    iter.remove();
                    this.setDirty();
                    continue;
                }
                nbt.setDuration(duration);

                if (!(level.getEntity(nbt.getVictim()) instanceof LivingEntity victim) || victim.isRemoved() || victim.isDeadOrDying()) {
                    iter.remove();
                    this.setDirty();
                    continue;
                }
                this.setDirty();
            }
        }

        private static class HitsNBT extends CompoundTag {
            private static final int DURATION = 5 * 20;

            public HitsNBT(LivingEntity victim, int hits) {
                this.putUUID("victim", victim.getUUID());
                this.putInt("hits", hits);
            }

            public HitsNBT(CompoundTag nbt) {
                this.putUUID("victim", nbt.getUUID("victim"));
                this.putInt("hits", nbt.getInt("hits"));
            }

            public UUID getVictim() {
                return this.getUUID("victim");
            }

            public int getHits() {
                return this.getInt("hits");
            }

            public int getDuration() {
                return this.getInt("duration");
            }

            public void setDuration(int duration) {
                this.putInt("duration", duration);
            }

            public void hit(int count) {
                this.putInt("hits", this.getHits() + count);
                this.setDuration(DURATION);
            }
        }
    }
    
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLevelTick(TickEvent.LevelTickEvent event) {
            if (event.phase == TickEvent.Phase.START) return;
            if (!(event.level instanceof ServerLevel level)) return;

            DomainAmplificationData storage = level.getDataStorage().computeIfAbsent(DomainAmplificationData.FACTORY, DomainAmplificationData.IDENTIFIER);
            storage.tick(level);
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (!DamageUtil.isMelee(source)) return;

            IJujutsuCapability cap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) return;

            DomainAmplificationData storage = ((ServerLevel) victim.level()).getDataStorage().computeIfAbsent(DomainAmplificationData.FACTORY, DomainAmplificationData.IDENTIFIER);
            storage.hit(attacker, victim);
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getSource() instanceof JJKDamageSources.JujutsuDamageSource source)) return;

            LivingEntity victim = event.getEntity();

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) return;

            Ability ability = source.getAbility();

            if (ability == null) return;

            if (ability.isTechnique()) {
                event.setAmount(event.getAmount() * (ability.getRequirements().contains(JJKAbilities.RCT1.get()) ? 0.75F : 0.25F));
            }
        }
    }
}