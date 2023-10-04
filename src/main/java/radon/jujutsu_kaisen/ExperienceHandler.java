package radon.jujutsu_kaisen;

import net.minecraft.network.chat.Component;
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
            if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent() || !attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;
            if (!victim.isAlive() || victim.isRemoved() || !attacker.isAlive() || attacker.isRemoved()) return;

            if (battles.containsKey(attacker.getUUID())) {
                battles.get(attacker.getUUID()).attack(event.getAmount());
            } else {
                battles.put(attacker.getUUID(), new BattleData(victim));
            }

            if (battles.containsKey(victim.getUUID())) {
                battles.get(victim.getUUID()).hurt(event.getAmount());
            } else {
                battles.put(victim.getUUID(), new BattleData(attacker));
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
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            float amount = 10.0F;
            amount *= (float) this.duration / (10 * 20);
            amount *= this.totalDamage / 10;
            amount *= 1.0F - Math.abs(this.lowestOwnerHealth - this.lowestTargetHealth);

            if (amount == 0.0F) return;

            if (cap.addExperience(amount)) {
                if (owner instanceof Player player) {
                    player.sendSystemMessage(Component.translatable(String.format("chat.%s.experience", JujutsuKaisen.MOD_ID), amount));
                }
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
