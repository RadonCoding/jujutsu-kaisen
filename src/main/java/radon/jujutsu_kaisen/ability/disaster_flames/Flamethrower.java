package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Flamethrower extends Ability implements Ability.IChannelened, Ability.IDurationable {
    private static final float DAMAGE = 7.5F;
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
        owner.swing(InteractionHand.MAIN_HAND);

        Vec3 look = owner.getLookAngle();

        if (owner.level() instanceof ServerLevel level) {
            for (int i = 0; i < 96; i++) {
                double theta = HelperMethods.RANDOM.nextDouble() * 2 * Math.PI;
                double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
                double r = HelperMethods.RANDOM.nextDouble() * 0.8D;
                double x = r * Math.sin(phi) * Math.cos(theta);
                double y = r * Math.sin(phi) * Math.sin(theta);
                double z = r * Math.cos(phi);
                Vec3 speed = look.add(x, y, z);
                Vec3 offset = owner.getEyePosition().add(owner.getLookAngle());
                level.sendParticles(ParticleTypes.FLAME, offset.x, offset.y, offset.z, 0, speed.x, speed.y, speed.z, 1.0D);
            }

            Vec3 offset = owner.getEyePosition().add(owner.getLookAngle().scale(RANGE / 2));

            for (Entity entity : owner.level().getEntities(owner, AABB.ofSize(offset, RANGE, RANGE, RANGE))) {
                if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getPower(owner))) {
                    entity.setSecondsOnFire(5);
                }
            }
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
    public void onStart(LivingEntity owner) {

    }

    @Override
    public void onRelease(LivingEntity owner) {

    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
