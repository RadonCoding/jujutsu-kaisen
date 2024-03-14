package radon.jujutsu_kaisen.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;

import java.util.Iterator;

public class VeilEventHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onExplosion(ExplosionEvent.Detonate event) {
            Explosion explosion = event.getExplosion();
            LivingEntity instigator = explosion.getIndirectSourceEntity();

            Iterator<BlockPos> iter = explosion.getToBlow().iterator();

            while (iter.hasNext()) {
                BlockPos pos = iter.next();
                Vec3 center = pos.getCenter();

                if (!VeilHandler.canDestroy(instigator, event.getLevel(), center.x, center.y, center.z)) {
                    iter.remove();
                }
            }
        }

        @SubscribeEvent
        public static void onEntityPlaceBlock(BlockEvent.EntityPlaceEvent event) {
            Entity entity = event.getEntity();
            Vec3 center = event.getPos().getCenter();

            if (!VeilHandler.canDestroy(entity, (Level) event.getLevel(), center.x, center.y, center.z)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onLivingDestroyBlock(LivingDestroyBlockEvent event) {
            LivingEntity entity = event.getEntity();
            Vec3 center = event.getPos().getCenter();

            if (!VeilHandler.canDestroy(entity, entity.level(), center.x, center.y, center.z)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            Player player = event.getPlayer();
            Vec3 center = event.getPos().getCenter();

            if (!VeilHandler.canDestroy(player, player.level(), center.x, center.y, center.z)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            Vec3 center = victim.position();

            if (!VeilHandler.canDamage(attacker, victim.level(), center.x, center.y, center.z)) {
                event.setCanceled(true);
            }
        }
    }
}
