package radon.jujutsu_kaisen.ability.projection_sorcery;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.util.HelperMethods;

public class ProjectionSorcery extends Ability {
    private static final double LAUNCH_POWER = 3.0D;

    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && HelperMethods.RANDOM.nextInt(10) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        cap.addSpeedStack();
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            if (!attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData cap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.getSpeedStacks() == 0) return;

            float speed = attacker.walkDist - attacker.walkDistO;

            if (speed <= 0.0F) return;

            Vec3 pos = victim.position().add(0.0D, victim.getBbHeight() / 2.0F, 0.0D);
            ((ServerLevel) victim.level()).sendParticles(ParticleTypes.EXPLOSION, pos.x(), pos.y(), pos.z(), 0, 1.0D, 0.0D, 0.0D, 1.0D);
            victim.level().playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

            Vec3 look = attacker.getLookAngle();

            victim.setDeltaMovement(look.scale(LAUNCH_POWER * speed));
            victim.hurtMarked = true;

            event.setAmount(event.getAmount() * speed);
        }
    }
}
