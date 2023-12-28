package radon.jujutsu_kaisen.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.item.cursed_tool.KamutokeDaggerItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdaptationEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class AdaptationEventHandlerForgeEvents {
        @SubscribeEvent
        public static void onLivingHitByDomain(LivingHitByDomainEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim instanceof Mob mob && mob.canAttack(event.getAttacker())) mob.setTarget(event.getAttacker());
            if (!JJKAbilities.hasToggled(victim, JJKAbilities.WHEEL.get())) return;

            victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (!cap.isAdaptedTo(event.getAbility())) {
                    cap.tryAdapt(event.getAbility());
                }
            });
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData cap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get()) || !cap.hasToggled(JJKAbilities.WHEEL.get())) return;

            if (!cap.isAdaptedTo(source)) {
                cap.tryAdapt(source);
            }

            if (!(victim instanceof MahoragaEntity)) return;

            if (cap.isAdaptedTo(source)) {
                victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.MASTER, 1.0F, 1.0F);
            }
            event.setAmount(event.getAmount() * (1.0F - cap.getAdaptation(source)));
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof MahoragaEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(victimCap -> {
                attacker.getCapability(SorcererDataHandler.INSTANCE).ifPresent(attackerCap -> {
                    Set<Ability> toggled = new HashSet<>(victimCap.getToggled());

                    for (Ability ability : toggled) {
                        if (!attackerCap.isAdaptedTo(ability)) continue;
                        victimCap.toggle(ability);
                    }

                    if (victim instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(victimCap.serializeNBT()), player);
                    }
                });
            });
        }
    }
}
