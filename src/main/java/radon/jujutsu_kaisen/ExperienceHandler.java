package radon.jujutsu_kaisen;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExperienceHandler {
    private static final Map<UUID, CopyOnWriteArraySet<BattleData>> battles = new HashMap<>();

    private static void addBattle(UUID owner, BattleData data) {
        if (!battles.containsKey(owner)) {
            battles.put(owner, new CopyOnWriteArraySet<>());
        }
        battles.get(owner).add(data);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity owner = event.getEntity();

        if (owner.level().isClientSide) return;
        if (!battles.containsKey(owner.getUUID())) return;

        for (BattleData data : battles.get(owner.getUUID())) {
            boolean fighting = data.tick(owner);

            if (!fighting) {
                data.end(owner);

                battles.get(owner.getUUID()).remove(data);

                if (battles.get(owner.getUUID()).isEmpty()) {
                    battles.remove(owner.getUUID());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity victim = event.getEntity();

        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (!victim.isAlive() || victim.isRemoved() || !attacker.isAlive() || attacker.isRemoved()) return;

            if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                boolean existing = false;

                if (battles.containsKey(attacker.getUUID())) {
                    for (BattleData battle : battles.get(attacker.getUUID())) {
                        if (battle.target != victim) continue;
                        battle.attack(event.getAmount());
                        existing = true;
                    }
                }
                if (!existing) {
                    addBattle(attacker.getUUID(), new BattleData(victim));
                }
            }

            if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                boolean existing = false;

                if (battles.containsKey(victim.getUUID())) {
                    for (BattleData battle : battles.get(victim.getUUID())) {
                        if (battle.target != attacker) continue;
                        battle.hurt(event.getAmount());
                    }
                }
                if (!existing) {
                    addBattle(victim.getUUID(), new BattleData(attacker));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Entity owner = event.getEntity();
        battles.remove(owner.getUUID());
    }

    private static class BattleData {
        private static final int MAX_DURATION = 5 * 60 * 20;

        private final LivingEntity target;
        private int idle;
        private float damageDealt;
        private float damageTaken;

        public BattleData(LivingEntity target) {
            this.target = target;
        }

        public void end(LivingEntity owner) {
            if (owner.isRemoved() || this.target.isRemoved()) return;

            if (this.damageDealt == 0.0F || this.damageTaken == 0.0F) return;

            ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            float amount = (this.damageDealt + this.damageTaken) * 2.0F;

            // Decrease amount to a minimum of 25% depending on the relativity of the damage taken and dealt
            amount *= Math.max(0.25F, (float) Math.abs((this.damageDealt - this.damageTaken) / ((double) (this.damageDealt + this.damageTaken) / 2)));

            // If owner has less health than target increase experience, if target has less health than owner decrease experience
            amount *= (this.target.getMaxHealth() + this.target.getArmorValue()) / (owner.getMaxHealth() + owner.getArmorValue());

            // Decrease amount to a minimum of 25% depending on the health of owner and target, if both are relatively on the same health amount owner gets the full amount
            amount *= Math.min(1.0F, 0.25F + 1.0F - Math.abs((owner.getHealth() / owner.getMaxHealth()) - (this.target.getHealth() / this.target.getMaxHealth())));

            if (this.target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                ISorcererData targetCap = this.target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                // If owner has less experience than target increase experience, if target has less experience than owner decrease experience
                amount *= (Math.max(1, targetCap.getExperience()) / Math.max(1, ownerCap.getExperience()));

                // Limit the experience to the max health of the target multiplied by whether the target can heal
                amount = Mth.clamp(amount, 0.0F, this.target.getMaxHealth() * (targetCap.getType() == JujutsuType.CURSE || targetCap.hasTrait(Trait.REVERSE_CURSED_TECHNIQUE) ? 1.5F : 1.0F));
            } else {
                amount *= 0.1F;

                // Limit the experience to the max health of the target
                amount = Mth.clamp(amount, 0.0F, this.target.getMaxHealth());
            }

            // If owner is dead they get 25% of the experience
            amount *= owner.isDeadOrDying() ? 0.25F : 1.0F;

            if (amount < 0.1F) return;

            if (ownerCap.addExperience(owner, amount)) {
                if (owner instanceof Player player) {
                    player.sendSystemMessage(Component.translatable(String.format("chat.%s.experience", JujutsuKaisen.MOD_ID), amount));
                }
            }

            int points = (int) Math.floor(amount * 0.1F);

            if (points > 0) {
                ownerCap.addPoints(points);

                if (owner instanceof Player player) {
                    player.sendSystemMessage(Component.translatable(String.format("chat.%s.points", JujutsuKaisen.MOD_ID), points));
                }
            }

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerCap.serializeNBT()), player);
            }
        }

        public void attack(float damage) {
            this.idle = 0;
            this.damageDealt += damage;
        }

        public void hurt(float damage) {
            this.idle = 0;
            this.damageTaken += damage;
        }

        public boolean tick(LivingEntity owner) {
            this.idle++;
            return this.idle < MAX_DURATION && owner.isAlive() && this.target.isAlive();
        }
    }
}
