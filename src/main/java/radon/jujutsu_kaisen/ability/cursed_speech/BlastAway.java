package radon.jujutsu_kaisen.ability.cursed_speech;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class BlastAway extends Ability {
    private static final double RANGE = 20.0D;
    private static final double RADIUS = 1.0D;
    private static final float DAMAGE = 5.0F;
    private static final double LAUNCH_POWER = 2.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return getEntities(owner).contains(target) && HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private static List<Entity> getEntities(LivingEntity owner) {
        Vec3 look = HelperMethods.getLookAngle(owner);
        Vec3 src = owner.getEyePosition();
        AABB bounds = AABB.ofSize(src, 1.0D, 1.0D, 1.0D).expandTowards(look.scale(RANGE)).inflate(RADIUS);
        return owner.level.getEntities(owner, bounds, entity -> !(entity instanceof LivingEntity living) || owner.canAttack(living));
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level.isClientSide) return;

        Vec3 look = HelperMethods.getLookAngle(owner);

        Vec3 src = owner.getEyePosition();

        for(int i = 1; i < RANGE + 7; i++) {
            Vec3 dst = src.add(look.scale(i));
            ((ServerLevel) owner.level).sendParticles(JJKParticles.CURSED_SPEECH.get(), dst.x(), dst.y(), dst.z(), 0, src.distanceTo(dst) * 0.5D, 0.0D, 0.0D, 1.0D);
        }

        owner.level.playSound(null, src.x(), src.y(), src.z(), JJKSounds.CURSED_SPEECH.get(), SoundSource.MASTER, 2.0F, 0.8F + HelperMethods.RANDOM.nextFloat() * 0.2F);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            cap.delayTickEvent(() -> {
                for (Entity entity : getEntities(owner)) {
                    if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * cap.getGrade().getRealPower(owner))) {
                        Vec3 center = entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D);
                        ((ServerLevel) owner.level).sendParticles(ParticleTypes.EXPLOSION, center.x(), center.y(), center.z(), 0, 1.0D, 0.0D, 0.0D, 1.0D);
                        ((ServerLevel) owner.level).sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x(), center.y(), center.z(),  0,1.0D, 0.0D, 0.0D, 1.0D);
                        owner.level.playSound(null, center.x(), center.y(), center.z(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS,
                                4.0F, (1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.2F) * 0.7F);

                        double power = LAUNCH_POWER * cap.getGrade().getRealPower(owner);
                        entity.setDeltaMovement(look.multiply(power, Math.min(LAUNCH_POWER, power), power));
                        entity.hurtMarked = true;
                    }
                    if (entity instanceof Player player) {
                        player.sendSystemMessage(Component.translatable(String.format("chat.%s.blast_away", JujutsuKaisen.MOD_ID), owner.getName()));
                    }
                }
            }, 20);
        });
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
    public boolean isTechnique() {
        return true;
    }

    @Override
    public Classification getClassification() {
        return Classification.CURSED_SPEECH;
    }
}
