package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.VaporParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

public class CursedEnergyBlast extends Ability implements Ability.IChannelened, Ability.IDurationable {
    private static final float DAMAGE = 5.0F;
    private static final double RANGE = 10.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return JJKAbilities.isChanneling(owner, this) || HelperMethods.RANDOM.nextInt(15) == 0 && target != null &&
                owner.hasLineOfSight(target) && owner.distanceTo(target) <= RANGE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        Vec3 look = HelperMethods.getLookAngle(owner);

        if (owner.level instanceof ServerLevel level) {
            Vec3 offset = owner.getEyePosition().add(HelperMethods.getLookAngle(owner).scale(2.0D));

            ParticleOptions particle = new VaporParticle.VaporParticleOptions(ParticleColors.getCursedEnergyColor(owner), 2.0F, 0.1F, true, (int) RANGE / 2);

            for (int i = 0; i < 96; i++) {
                double theta = HelperMethods.RANDOM.nextDouble() * 2 * Math.PI;
                double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
                double r = HelperMethods.RANDOM.nextDouble() * 0.8D;
                double x = r * Math.sin(phi) * Math.cos(theta);
                double y = r * Math.sin(phi) * Math.sin(theta);
                double z = r * Math.cos(phi);
                Vec3 speed = look.add(x, y, z);
                level.sendParticles(particle, offset.x(), offset.y(), offset.z(), 0, speed.x(), speed.y(), speed.z(), 1.0D);
            }

            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                Vec3 range = owner.getEyePosition().add(HelperMethods.getLookAngle(owner).scale(RANGE / 2));

                for (Entity entity : owner.level.getEntities(owner, AABB.ofSize(range, RANGE, RANGE, RANGE))) {
                    entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * cap.getGrade().getPower(owner));
                }
            });
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public int getCooldown() {
        return 10 * 20;
    }

    @Override
    public int getDuration() {
        return 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public void onRelease(LivingEntity owner, int charge) {

    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.SCROLL;
    }
}
