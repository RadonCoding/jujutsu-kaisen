package radon.jujutsu_kaisen;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ParryHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (!DamageUtil.isMelee(source)) return;

            LivingEntity victim = event.getEntity();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (attacker.swinging && victim.swinging && attacker.swingTime == victim.swingTime) {
                Vec3 look = RotationUtil.getTargetAdjustedLookAngle(victim);
                Vec3 start = victim.getEyePosition();
                Vec3 result = attacker.getEyePosition().subtract(start);

                double angle = Math.acos(look.normalize().dot(result.normalize()));

                if (angle < 1.0D) {
                    victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.MASTER, 1.0F, 1.0F);
                    event.setCanceled(true);
                }
            }
        }
    }
}
