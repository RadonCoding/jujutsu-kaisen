package radon.jujutsu_kaisen.ability.ai.cyclops;

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
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.curse.CyclopsCurseEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class CyclopsSmash extends Ability {
    private static final int RADIUS = 10;
    private static final float DAMAGE = 10.0F;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && !target.isDeadOrDying() && owner.distanceTo(target) <= 3.0D;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return owner instanceof CyclopsCurseEntity;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        int current = 1;

        while (current < RADIUS) {
            current++;

            int radius = current;

            cap.delayTickEvent(() -> {
                for (double phi = 0.0D; phi < Math.PI * 2; phi += 0.5D) {
                    double x = owner.getX() + radius * Math.cos(phi);
                    double y = owner.getY();
                    double z = owner.getZ() + radius * Math.sin(phi);
                    level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 0, 0.0D, HelperMethods.RANDOM.nextDouble(), 0.0D, 1.0D);
                }

                for (Entity entity : owner.level().getEntities(owner, AABB.ofSize(owner.position(), radius, radius, radius))) {
                    if ((!(entity instanceof LivingEntity living) || !owner.canAttack(living)) || entity.distanceTo(owner) > RADIUS)
                        continue;

                    if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getPower(owner))) {
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
