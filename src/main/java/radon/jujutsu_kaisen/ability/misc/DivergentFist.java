package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityDisplayInfo;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

public class DivergentFist extends Ability implements Ability.IToggled {
    private static final float DAMAGE = 10.0F;
    private static final double LAUNCH_POWER = 5.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.distanceTo(target) < 5.0D;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(0.0F, 1.0F);
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        Vec2 coordinates = this.getDisplayCoordinates();
        return new AbilityDisplayInfo(String.format("%s_%s", JJKAbilities.getKey(this).getPath(), cap.getType().name().toLowerCase()), coordinates.x, coordinates.y);
    }

    @Override
    public boolean isScalable() {
        return true;
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.divergentFistCost.get();
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (attacker.level().isClientSide) return;

            LivingEntity victim = event.getEntity();

            ISorcererData cap = attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            Vec3 look = attacker.getLookAngle();

            cap.delayTickEvent(() -> {
                attacker.swing(InteractionHand.MAIN_HAND, true);

                if (attacker instanceof Player player) {
                    player.attack(victim);
                } else {
                    attacker.doHurtTarget(victim);
                }
                victim.invulnerableTime = 0;

                Vec3 pos = victim.position().add(0.0D, victim.getBbHeight() / 2.0F, 0.0D);

                for (int i = 0; i < 96; i++) {
                    double theta = HelperMethods.RANDOM.nextDouble() * 2 * Math.PI;
                    double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
                    double r = HelperMethods.RANDOM.nextDouble() * 0.8D;
                    double x = r * Math.sin(phi) * Math.cos(theta);
                    double y = r * Math.sin(phi) * Math.sin(theta);
                    double z = r * Math.cos(phi);
                    Vec3 speed = look.add(x, y, z);
                    Vec3 offset = pos.add(look);
                    ((ServerLevel) victim.level()).sendParticles(new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.getCursedEnergyColor(attacker), attacker.getBbWidth(),
                            0.2F, 8), offset.x(), offset.y(), offset.z(), 0, speed.x(), speed.y(), speed.z(), 1.0D);
                }

                if (victim.hurt(JJKDamageSources.jujutsuAttack(attacker, JJKAbilities.DIVERGENT_FIST.get()), DAMAGE * Ability.getPower(JJKAbilities.DIVERGENT_FIST.get(), attacker))) {
                    ((ServerLevel) victim.level()).sendParticles(ParticleTypes.EXPLOSION, pos.x(), pos.y(), pos.z(), 0, 1.0D, 0.0D, 0.0D, 1.0D);
                    victim.level().playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

                    victim.setDeltaMovement(look.scale(LAUNCH_POWER));
                    victim.hurtMarked = true;
                }
            }, 5);
        }
    }
}
