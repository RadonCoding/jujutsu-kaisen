package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.List;

public class DisasterFlames extends Ability {
    private static final double AOE_RANGE = 5.0D;
    private static final float DAMAGE = 25.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private void spawnParticles(Entity entity) {
        double x = entity.getX();
        double y = entity.getY() + (entity.getBbHeight() / 2.0F);
        double z = entity.getZ();

        for (int i = 0; i < 32; i++) {
            double scale = HelperMethods.RANDOM.nextDouble() * 0.5D + 0.5D;
            Vec3 speed = new Vec3(HelperMethods.RANDOM.nextGaussian(), HelperMethods.RANDOM.nextGaussian(), HelperMethods.RANDOM.nextGaussian())
                    .normalize().scale(scale);

            double offsetX = x + speed.x;
            double offsetY = y + speed.y;
            double offsetZ = z + speed.z;

            ((ServerLevel) entity.level()).sendParticles((ParticleOptions) ParticleTypes.FLAME, offsetX, offsetY, offsetZ, 0,
                    speed.x, speed.y, speed.z, 1.0D);
        }
    }

    private List<Entity> getTargets(LivingEntity owner) {
        List<Entity> entities = owner.level().getEntities(owner, owner.getBoundingBox().inflate(AOE_RANGE));
        entities.removeIf(entity -> (entity instanceof LivingEntity living && !owner.canAttack(living)));
        return entities;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        List<Entity> targets = this.getTargets(owner);

        if (targets.isEmpty()) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        for (Entity entity : this.getTargets(owner)) {
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 1.0F, 1.0F);

            if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(owner, owner, this), DAMAGE * this.getPower(owner) * (float) (1.0F - (entity.distanceTo(owner) / AOE_RANGE)))) {
                entity.setSecondsOnFire(5);
                this.spawnParticles(entity);
            }
        }
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
    public MenuType getMenuType() {
        return MenuType.MELEE;
    }
}
