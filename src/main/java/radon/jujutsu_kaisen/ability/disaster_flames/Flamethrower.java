package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Flamethrower extends Ability implements Ability.IChannelened, Ability.IDurationable {
    private static final float DAMAGE = 10.0F;
    private static final double RANGE = 5.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return JJKAbilities.isChanneling(owner, this) || HelperMethods.RANDOM.nextInt(5) == 0 && target != null &&
                owner.hasLineOfSight(target) && owner.distanceTo(target) <= RANGE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        Vec3 look = HelperMethods.getLookAngle(owner);

        if (owner.level instanceof ServerLevel level) {
            for (int i = 0; i < 96; i++) {
                Vec3 speed = look.add((HelperMethods.RANDOM.nextDouble() - 0.5D) * 0.8D,
                        (HelperMethods.RANDOM.nextDouble() - 0.5D) * 0.8D,
                        (HelperMethods.RANDOM.nextDouble() - 0.5D) * 0.8D);
                Vec3 offset = owner.getEyePosition().add(HelperMethods.getLookAngle(owner));
                level.sendParticles(ParticleTypes.FLAME, offset.x(), offset.y(), offset.z(), 0, speed.x(), speed.y(), speed.z(), 1.0D);
            }

            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                Vec3 offset = owner.getEyePosition().add(HelperMethods.getLookAngle(owner).scale(RANGE / 2));

                for (Entity entity : owner.level.getEntities(owner, AABB.ofSize(offset, RANGE, RANGE, RANGE))) {
                    if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * cap.getGrade().getPower(owner))) {
                        entity.setSecondsOnFire(5);
                    }
                }
            });
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 5.0F;
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
    public Classification getClassification() {
        return Classification.DISASTER_FLAMES;
    }

    @Override
    public void onRelease(LivingEntity owner, int charge) {

    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.SCROLL;
    }
}
