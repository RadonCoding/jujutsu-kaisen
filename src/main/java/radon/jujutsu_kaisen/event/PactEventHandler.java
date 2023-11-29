package radon.jujutsu_kaisen.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.BindingVow;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.CloneEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.world.dimension.JJKDimensions;

import java.util.Set;

public class PactEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class PactEventHandlerForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            // Check for BindingVow.RECOIL
            if (source.is(JJKDamageSources.JUJUTSU)) {
                if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData cap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    if (cap.hasBindingVow(BindingVow.RECOIL)) {
                        attacker.hurt(JJKDamageSources.self(victim), event.getAmount() * 0.25F);
                        event.setAmount(event.getAmount() * 1.25F);
                    }
                }
            }

            // Check for Pact.INVULNERABILITY
            if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent() && attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                ISorcererData victimCap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                ISorcererData attackerCap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                if (victimCap.hasPact(attacker.getUUID(), Pact.INVULNERABILITY) && attackerCap.hasPact(victim.getUUID(), Pact.INVULNERABILITY)) {
                    victimCap.removePact(attacker.getUUID(), Pact.INVULNERABILITY);
                    attackerCap.removePact(victim.getUUID(), Pact.INVULNERABILITY);

                    MinecraftServer server = attacker.level().getServer();

                    if (server != null) {
                        ServerLevel dimension = server.getLevel(JJKDimensions.LIMBO_KEY);

                        if (dimension != null) {
                            BlockPos pos = HelperMethods.findSafePos(dimension, attacker);
                            attacker.teleportTo(dimension, pos.getX(), pos.getY(), pos.getZ(), Set.of(), attacker.getYRot(), attacker.getXRot());

                            attacker.level().addFreshEntity(new CloneEntity(attacker, attacker.level().dimension().location()));
                        }
                    }
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(victimCap.serializeNBT()), (ServerPlayer) victim);
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(attackerCap.serializeNBT()), (ServerPlayer) attacker);
                }
            }
        }
    }
}