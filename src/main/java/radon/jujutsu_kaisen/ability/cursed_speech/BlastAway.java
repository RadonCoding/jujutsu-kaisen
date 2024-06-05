package radon.jujutsu_kaisen.ability.cursed_speech;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.cursed_speech.util.CursedSpeechUtil;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class BlastAway extends CursedSpeech {
    private static final float DAMAGE = 5.0F;
    private static final double LAUNCH_POWER = 2.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || !owner.hasLineOfSight(target)) return false;
        return CursedSpeechUtil.getTargets(owner).contains(target) && HelperMethods.RANDOM.nextInt(5) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        if (owner.level().isClientSide) return;

        CursedSpeechUtil.attack(owner, entity -> {
            if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getOutput(owner))) {
                Vec3 center = entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D);
                ((ServerLevel) owner.level()).sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
                ((ServerLevel) owner.level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
                owner.level().playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS,
                        4.0F, (1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.2F) * 0.7F);

                Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

                double power = LAUNCH_POWER * this.getOutput(owner);
                entity.setDeltaMovement(look.scale(power).multiply(1.0D, 0.25D, 1.0D));
                entity.hurtMarked = true;
            }

            if (entity instanceof Player player) {
                player.sendSystemMessage(Component.translatable(String.format("chat.%s.blast_away", JujutsuKaisen.MOD_ID), owner.getName()));
            }
        });
    }

    @Override
    public int getThroatDamage() {
        return 2 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.CURSED_SPEECH;
    }
}
