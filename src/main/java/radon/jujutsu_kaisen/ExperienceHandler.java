package radon.jujutsu_kaisen;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.curse.base.PackCursedSpirit;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExperienceHandler {
    private static final Map<UUID, CopyOnWriteArraySet<BattleData>> battles = new HashMap<>();

    private static void addBattle(UUID ownerUUID, BattleData data) {
        if (!battles.containsKey(ownerUUID)) {
            battles.put(ownerUUID, new CopyOnWriteArraySet<>());
        }
        battles.get(ownerUUID).add(data);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity victim = event.getEntity();

        // Tames do not give experience
        if (victim instanceof TamableAnimal tamable && tamable.isTame()) return;
        if (victim instanceof TenShadowsSummon) return;

        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (!victim.isAlive() || victim.isRemoved() || !attacker.isAlive() || attacker.isRemoved()) return;

            Iterator<Map.Entry<UUID, CopyOnWriteArraySet<BattleData>>> iter = battles.entrySet().iterator();

            boolean existing = false;

            // Find battles where the target is the victim and increase the total damage
            while (iter.hasNext()) {
                for (BattleData battle : iter.next().getValue()) {
                    if (battle.getTargetUUID() != victim.getUUID()) continue;
                    battle.attack(attacker.getUUID(), event.getAmount());
                    existing = true;
                }
            }

            while (attacker instanceof TamableAnimal tamable && tamable.isTame()) {
                attacker = tamable.getOwner();

                if (attacker == null) return;
            }

            if (!existing) {
                BattleData battle = new BattleData(attacker.getUUID(), victim.getUUID());
                addBattle(attacker.getUUID(), battle);
                battle.attack(attacker.getUUID(), event.getAmount());
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Entity owner = event.getEntity();
        battles.remove(owner.getUUID());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity.level() instanceof ServerLevel level)) return;

        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (data != null && data.getExperience() > 0.0F) {
            float penalty = (data.getExperience() * ConfigHolder.SERVER.deathPenalty.get().floatValue());
            data.setExperience(Math.max(0.0F, data.getExperience() - penalty));

            if (entity instanceof ServerPlayer player) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.experience_penalty", JujutsuKaisen.MOD_ID), penalty));

                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(data.serializeNBT()), player);
            }
        }

        Iterator<Map.Entry<UUID, CopyOnWriteArraySet<BattleData>>> battleIter = battles.entrySet().iterator();

        while (battleIter.hasNext()) {
            Set<BattleData> current = battleIter.next().getValue();
            List<BattleData> battlesToRemove = new ArrayList<>();

            for (BattleData battle : current) {
                if (battle.getOwnerUUID() == entity.getUUID() || battle.getTargetUUID().equals(entity.getUUID())) {
                    battle.end(level);
                    battlesToRemove.add(battle);
                }
            }

            battlesToRemove.forEach(current::remove);

            if (current.isEmpty()) {
                battleIter.remove();
            }
        }
    }

    private static class BattleData {
        private final UUID ownerUUID;
        private final UUID targetUUID;

        private float totalDamageDealt;
        private float damageDealtByOwner;

        public BattleData(UUID ownerUUID, UUID targetUUID) {
            this.ownerUUID = ownerUUID;
            this.targetUUID = targetUUID;
        }

        public UUID getOwnerUUID() {
            return this.ownerUUID;
        }

        public UUID getTargetUUID() {
            return targetUUID;
        }

        private static float calculateStrength(LivingEntity target) {
            float strength = target.getMaxHealth() * 0.1F;

            AttributeMap attributes = target.getAttributes();

            if (attributes.hasAttribute(Attributes.ARMOR_TOUGHNESS)) {
                float armor = (float) target.getArmorValue();
                float toughness = (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
                float f = 2.0F + toughness / 4.0F;
                float f1 = Mth.clamp(armor - strength / f, armor * 0.2F, 20.0F);
                strength /= 1.0F - f1 / 25.0F;
            }

            MobEffectInstance instance = target.getEffect(MobEffects.DAMAGE_RESISTANCE);

            if (instance != null) {
                int resistance = instance.getAmplifier();
                int i = (resistance + 1) * 5;
                int j = 25 - i;

                if (j == 0) {
                    return strength;
                } else {
                    float x = 25.0F / (float) j;
                    strength = strength * x;
                }
            }

            int k = EnchantmentHelper.getDamageProtection(target.getArmorSlots(), target.damageSources().generic());

            if (k > 0) {
                float f2 = Mth.clamp(k, 0.0F, 20.0F);
                strength /= 1.0F - f2 / 25.0F;
            }

            if (target instanceof PackCursedSpirit pack) {
                strength += pack.getMinCount() + ((float) (pack.getMaxCount() - pack.getMinCount()) / 2);
            }

            IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap != null) {
                // Increase from target's experience
                ISorcererData data = cap.getSorcererData();
                strength += data.getExperience() * 0.01F;

                // Increase if the target can heal
                if (data.getType() == JujutsuType.CURSE || data.isUnlocked(JJKAbilities.RCT1.get())) {
                    strength *= 1.25F;
                }
            }
            return strength;
        }

        public void end(ServerLevel level) {
            if (!(level.getEntity(this.ownerUUID) instanceof LivingEntity owner) || !(level.getEntity(this.targetUUID) instanceof LivingEntity target)) return;
            if (owner.isRemoved() || owner.isDeadOrDying() || target.isRemoved()) return;
            if (this.damageDealtByOwner == 0.0F) return;

            IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (ownerCap == null) return;

            ISorcererData ownerData = ownerCap.getSorcererData();

            float experience = calculateStrength(target)
                    * (this.damageDealtByOwner / this.totalDamageDealt)
                    * ConfigHolder.SERVER.experienceMultiplier.get().floatValue();

            if (experience < 0.1F) return;

            if (ownerData.addExperience(experience)) {
                if (owner instanceof Player player) {
                    player.sendSystemMessage(Component.translatable(String.format("chat.%s.experience", JujutsuKaisen.MOD_ID), experience));
                }
            }

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerData.serializeNBT()), player);
            }
        }

        public void attack(UUID attackerUUID, float damage) {
            this.totalDamageDealt += damage;

            if (attackerUUID.equals(this.ownerUUID)) {
                this.damageDealtByOwner += damage;
            }
        }
    }
}
