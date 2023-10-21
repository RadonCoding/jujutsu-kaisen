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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExperienceHandler {
    private static final Map<UUID, BattleData> battles = new HashMap<>();

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity owner = event.getEntity();

        if (owner.level().isClientSide) return;
        if (!battles.containsKey(owner.getUUID())) return;

        boolean fighting = battles.get(owner.getUUID()).tick(owner);

        if (!fighting) {
            battles.get(owner.getUUID()).end(owner);
            battles.remove(owner.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity victim = event.getEntity();

        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (!victim.isAlive() || victim.isRemoved() || !attacker.isAlive() || attacker.isRemoved()) return;

            if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                if (battles.containsKey(attacker.getUUID())) {
                    battles.get(attacker.getUUID()).attack(event.getAmount());
                } else if (attacker.getLastHurtByMob() == victim) {
                    battles.put(attacker.getUUID(), new BattleData(victim));
                }
            }

            if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                if (battles.containsKey(victim.getUUID())) {
                    battles.get(victim.getUUID()).hurt(event.getAmount());
                } else {
                    battles.put(victim.getUUID(), new BattleData(attacker));
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
        private final LivingEntity target;

        private int duration;

        private float lowestOwnerHealth;
        private float lowestTargetHealth;

        private float totalDamage;

        public BattleData(LivingEntity target) {
            this.target = target;
        }

        public void end(LivingEntity owner) {
            if (this.lowestOwnerHealth == 1.0F || this.lowestTargetHealth == 1.0F) return;

            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            float amount = 10.0F;

            // Multiply by duration / 10 seconds
            amount *= (float) this.duration / (10 * 20);

            // Multiply by total damage dealt during battle / 10
            amount *= this.totalDamage / 10;

            // Multiply by the difference of the lowest health factors of both owner and target
            amount *= 1.0F - Math.abs(this.lowestOwnerHealth - this.lowestTargetHealth);

            // If owner has less health than target increase experience, if target has less health than owner decrease experience
            amount *= owner.getMaxHealth() / this.target.getMaxHealth();

            // If owner is dead they get 10% of the experience
            amount *= owner.isAlive() ? 1.0F : 0.1F;

            // Limit the experience to the max health of the target
            amount = Mth.clamp(this.target.getMaxHealth(), 0.0F, amount);

            if (amount == 0.0F) return;

            if (cap.addExperience(amount)) {
                if (owner instanceof Player player) {
                    player.sendSystemMessage(Component.translatable(String.format("chat.%s.experience", JujutsuKaisen.MOD_ID), amount));
                }
            }

            int points = (int) Math.floor(amount * 0.1F);

            if (points > 0) {
                cap.addPoints(points);

                if (owner instanceof Player player) {
                    player.sendSystemMessage(Component.translatable(String.format("chat.%s.points", JujutsuKaisen.MOD_ID), points));
                }
            }

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
            }
        }

        public void attack(float damage) {
            this.totalDamage += damage;
        }

        public void hurt(float damage) {
            this.totalDamage += damage;
        }

        public boolean tick(LivingEntity owner) {
            this.duration++;

            if (this.lowestOwnerHealth == 0.0F) this.lowestOwnerHealth = owner.getHealth();
            if (this.lowestTargetHealth == 0.0F) this.lowestTargetHealth = this.target.getHealth();

            if (owner.getHealth() / owner.getMaxHealth() < this.lowestOwnerHealth) this.lowestOwnerHealth = owner.getHealth() / owner.getMaxHealth();
            if (this.target.getHealth() / this.target.getMaxHealth() < this.lowestTargetHealth) this.lowestTargetHealth = this.target.getHealth() / this.target.getMaxHealth();

            return owner.isAlive() && !owner.isRemoved() && this.target.isAlive() && !this.target.isRemoved();
        }
    }
}
