package radon.jujutsu_kaisen;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ParryHandler {
    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ParryHandlerForgeEvents {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (!HelperMethods.isMelee(source)) return;

            LivingEntity victim = event.getEntity();

            if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData cap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (attacker.swinging && victim.swinging) {
                int rng = ConfigHolder.SERVER.parryChance.get();

                if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) rng /= 2;
                if (HelperMethods.RANDOM.nextInt(rng) != 0) return;

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
