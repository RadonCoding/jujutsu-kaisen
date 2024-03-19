package radon.jujutsu_kaisen.ability.shockwave;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Shockwave extends Ability {
    private static final double RADIUS = 10.0D;
    private static final float DAMAGE = 10.0F;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && !target.isDeadOrDying() && owner.distanceTo(target) <= RADIUS;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        int current = 1;

        while (current < RADIUS) {
            current++;

            int radius = current;

            data.delayTickEvent(() -> {
                for (double phi = 0.0D; phi < Math.PI * 2; phi += 0.5D) {
                    double x = owner.getX() + radius * Math.cos(phi);
                    double y = owner.getY();
                    double z = owner.getZ() + radius * Math.sin(phi);
                    level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 0, 0.0D, HelperMethods.RANDOM.nextDouble(), 0.0D, 1.0D);
                }

                for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, owner.level(), owner, AABB.ofSize(owner.position(), radius, radius, radius))) {
                    if (entity.distanceTo(owner) > RADIUS) continue;

                    if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getOutput(owner))) {
                        entity.setDeltaMovement(entity.position().subtract(owner.position()));
                    }
                }
            }, current);
        }
    }

    @Override
    public int getCooldown() {
        return 5 * 20;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }
}
