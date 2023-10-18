package radon.jujutsu_kaisen.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;
import radon.jujutsu_kaisen.entity.CloneEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.world.dimension.JJKDimensions;

import java.util.Set;

public class PactEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide()) return;

            DamageSource source = event.getSource();

            if (source.getEntity() instanceof Player attacker) {
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
                    }
                }
            }
        }
    }
}