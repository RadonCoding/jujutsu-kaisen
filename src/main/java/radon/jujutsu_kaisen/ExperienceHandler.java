package radon.jujutsu_kaisen;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExperienceHandler {
    private static final Map<UUID, Set<BattleData>> battles = new HashMap<>();

    private static void addBattle(UUID owner, BattleData data) {
        if (!battles.containsKey(owner)) {
            battles.put(owner, new HashSet<>());
        }
        battles.get(owner).add(data);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity owner = event.getEntity();

        if (owner.level().isClientSide) return;
        if (!battles.containsKey(owner.getUUID())) return;

        for (BattleData battle : battles.get(owner.getUUID())) {
            boolean fighting = battle.tick(owner);

            if (!fighting) {
                battle.end(owner);

                battles.get(owner.getUUID()).remove(battle);

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
                if (battles.containsKey(attacker.getUUID())) {
                    for (BattleData battle : battles.get(attacker.getUUID())) {
                        if (battle.target != victim) continue;
                        battle.attack(event.getAmount());
                    }
                } else if (attacker.getLastHurtByMob() == victim) {
                    addBattle(attacker.getUUID(), new BattleData(victim));
                }
            }

            if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                if (battles.containsKey(victim.getUUID())) {
                    for (BattleData battle : battles.get(victim.getUUID())) {
                        if (battle.target != attacker) continue;
                        battle.hurt(event.getAmount());
                    }
                } else {
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
        private int duration;
        private int idle;
        private float damageDealt;
        private float damageTaken;

        public BattleData(LivingEntity target) {
            this.target = target;
        }

        public void end(LivingEntity owner) {
            if (this.duration >= MAX_DURATION || owner.isRemoved() || this.target.isRemoved()) return;

            if (this.damageDealt == 0.0F || this.damageTaken == 0.0F) return;

            ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            float amount = (this.damageDealt + this.damageTaken) * 2.0F;

            // Multiply by duration / 30 seconds
            amount *= Math.min(1.0F, (float) this.duration / (30 * 20));

            // Decrease amount to a minimum of 25% depending on the relativity of the damage taken and dealt
            amount *= Math.max(0.25F, (float) Math.abs((this.damageDealt - this.damageTaken) / ((double) (this.damageDealt + this.damageTaken) / 2)));

            // If owner has less health than target increase experience, if target has less health than owner decrease experience
            amount *= this.target.getMaxHealth() / owner.getMaxHealth();

            // If owner is dead they get 10% of the experience
            amount *= owner.isDeadOrDying() ? 0.1F : 1.0F;

            // Decrease amount to a minimum of 25% depending on the health of owner and target, if both are relatively on the same health amount owner gets the full amount
            amount *= Math.min(1.0F, 0.25F + 1.0F - Math.abs((owner.getHealth() / owner.getMaxHealth()) - (this.target.getHealth() / this.target.getMaxHealth())));

            // Limit the experience to the max health of the target
            amount = Mth.clamp(this.target.getMaxHealth(), 0.0F, amount);

            if (this.target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                ISorcererData targetCap = this.target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                amount *= targetCap.getExperience() / ownerCap.getExperience();
            } else {
                amount *= 0.1F;
            }

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
            this.duration++;
            return this.idle < MAX_DURATION && owner.isAlive() && this.target.isAlive();
        }
    }
}
