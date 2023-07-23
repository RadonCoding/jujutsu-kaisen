package radon.jujutsu_kaisen.ability.jogo;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class DisasterFlames extends Ability {
    private static final double AOE_RANGE = 5.0D;
    private static final double DIRECT_RANGE = 1.0D;
    private static final float DAMAGE = 5.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        LivingEntity result = null;

        if (owner instanceof Player) {
            if (HelperMethods.getLookAtHit(owner, DIRECT_RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
                if (owner.canAttack(target)) {
                    result = target;
                }
            }
        }
        return result;
    }

    private void spawnParticles(Entity entity, int count) {
        double x = entity.getX();
        double y = entity.getY() + (entity.getBbHeight() / 2.0F);
        double z = entity.getZ();

        for (int i = 0; i < count; i++) {
            Vec3 vec = new Vec3(HelperMethods.RANDOM.nextGaussian(), HelperMethods.RANDOM.nextGaussian(), HelperMethods.RANDOM.nextGaussian()).normalize();

            double scale = HelperMethods.RANDOM.nextDouble() * 0.5 + 0.5;
            vec = vec.scale(scale);

            double offsetX = x + vec.x();
            double offsetY = y + vec.y();
            double offsetZ = z + vec.z();

            ((ServerLevel) entity.level).sendParticles((ParticleOptions) ParticleTypes.FLAME, offsetX, offsetY, offsetZ, 0,
                    vec.x(), vec.y(), vec.z(), 1.0D);
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 1.0F, 1.0F);
        }
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        if (this.getTarget(owner) == null && this.getTargets(owner).isEmpty()) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    private List<Entity> getTargets(LivingEntity owner) {
        List<Entity> entities = owner.level.getEntities(owner, owner.getBoundingBox().inflate(AOE_RANGE));
        entities.removeIf(entity -> (entity instanceof LivingEntity living && !owner.canAttack(living)));
        return entities;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            LivingEntity target = this.getTarget(owner);

            if (target == null) {
                for (Entity entity : this.getTargets(owner)) {
                    if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(owner, null), DAMAGE * cap.getGrade().getPower())) {
                        entity.setSecondsOnFire(5);

                        if (!owner.level.isClientSide) {
                            this.spawnParticles(entity, 32);
                        }
                    }
                }
            } else {
                if (target.hurt(JJKDamageSources.indirectJujutsuAttack(owner, null), (DAMAGE * 2) * cap.getGrade().getPower())) {
                    target.setSecondsOnFire(10);

                    if (!owner.level.isClientSide) {
                        this.spawnParticles(target, 64);
                    }
                }
            }
        });
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 100.0F;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }
}
