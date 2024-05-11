package radon.jujutsu_kaisen.event;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;

import java.util.Iterator;

public class VeilEventHandler {
    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onExplosion(ExplosionEvent.Detonate event) {
            if (!(event.getLevel() instanceof ServerLevel level)) return;

            Explosion explosion = event.getExplosion();

            Entity direct = explosion.getDirectSourceEntity();
            LivingEntity indirect = explosion.getIndirectSourceEntity();

            Entity exploder = indirect == null ? direct : indirect;

            if (exploder == null) return;

            Iterator<BlockPos> iter = explosion.getToBlow().iterator();

            while (iter.hasNext()) {
                BlockPos pos = iter.next();
                Vec3 center = pos.getCenter();

                if (!VeilHandler.canDestroy(exploder, level, center.x, center.y, center.z)) {
                    iter.remove();
                }
            }
        }

        @SubscribeEvent
        public static void onEntityPlaceBlock(BlockEvent.EntityPlaceEvent event) {
            if (!(event.getLevel() instanceof ServerLevel level)) return;

            Entity entity = event.getEntity();
            Vec3 center = event.getPos().getCenter();

            if (!VeilHandler.canDestroy(entity, level, center.x, center.y, center.z)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onLivingDestroyBlock(LivingDestroyBlockEvent event) {
            LivingEntity entity = event.getEntity();

            if (!(entity.level() instanceof ServerLevel level)) return;

            Vec3 center = event.getPos().getCenter();

            if (!VeilHandler.canDestroy(entity, level, center.x, center.y, center.z)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            Player player = event.getPlayer();

            if (!(player.level() instanceof ServerLevel level)) return;

            Vec3 center = event.getPos().getCenter();

            if (!VeilHandler.canDestroy(player, level, center.x, center.y, center.z)) {
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

            if (!VeilHandler.canDamage(attacker, victim, ((ServerLevel) victim.level()), center.x, center.y, center.z)) {
                event.setCanceled(true);
            }
        }
    }
}
