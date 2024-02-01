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
import radon.jujutsu_kaisen.client.particle.FireParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Flamethrower extends Ability implements Ability.IChannelened, Ability.IDurationable {
    private static final float DAMAGE = 7.5F;
    private static final double RANGE = 10.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || !owner.hasLineOfSight(target)) return false;

        if (JJKAbilities.isChanneling(owner, this)) {
            return HelperMethods.RANDOM.nextInt(5) != 0;
        }
        return HelperMethods.RANDOM.nextInt(5) == 0 && owner.distanceTo(target) <= RANGE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

        if (owner.level() instanceof ServerLevel level) {
            float scale = 1.0F;

            for (int i = 0; i < 96; i++) {
                double theta = HelperMethods.RANDOM.nextDouble() * 2 * Math.PI;
                double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
                double r = HelperMethods.RANDOM.nextDouble() * 4.0D;
                double x = r * Math.sin(phi) * Math.cos(theta);
                double y = r * Math.sin(phi) * Math.sin(theta);
                double z = r * Math.cos(phi);
                Vec3 start = owner.getEyePosition().subtract(0.0D, scale / 2, 0.0D).add(look);
                Vec3 end = start.add(look.scale(RANGE)).add(x, y, z);
                Vec3 speed = start.subtract(end).scale(1.0D / 20).reverse();
                level.sendParticles(new FireParticle.FireParticleOptions(scale, true, 20), start.x, start.y, start.z, 0,
                        speed.x, speed.y, speed.z, 1.0D);
            }

            AABB bounds = AABB.ofSize(owner.getEyePosition(), 1.0D, 1.0D, 1.0D).expandTowards(look.scale(RANGE)).inflate(1.0D);

            for (Entity entity : owner.level().getEntitiesOfClass(LivingEntity.class, bounds, entity -> entity != owner && entity.hasLineOfSight(owner))) {
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
    public MenuType getMenuType() {
        return MenuType.MELEE;
    }

    @Override
    public Classification getClassification() {
        return Classification.FIRE;
    }
}