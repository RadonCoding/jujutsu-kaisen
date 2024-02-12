package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.client.particle.FireParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

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

    private List<LivingEntity> getTargets(LivingEntity owner) {
        return owner.level().getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(AOE_RANGE), entity -> entity != owner);
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        List<LivingEntity> targets = this.getTargets(owner);

        if (targets.isEmpty()) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }


    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        for (LivingEntity entity : this.getTargets(owner)) {
            owner.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 1.0F, 1.0F);

            if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(owner, owner, JJKAbilities.DISASTER_FLAMES.get()),
                    DAMAGE * Ability.getPower(JJKAbilities.DISASTER_FLAMES.get(), owner) * (float) (1.0F - (entity.distanceTo(owner) / AOE_RANGE)))) {
                entity.setSecondsOnFire(5);
            }

            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            for (int i = 0; i < 4; i++) {
                data.delayTickEvent(() -> {
                    double x = entity.getX();
                    double y = entity.getY();
                    double z = entity.getZ();

                    for (int j = 0; j < 32; j++) {
                        Vec3 speed = new Vec3((HelperMethods.RANDOM.nextDouble() - 0.5D) * 0.25D, HelperMethods.RANDOM.nextDouble() * 0.5D, (HelperMethods.RANDOM.nextDouble() - 0.5D) * 0.25D);

                        double offsetX = x + speed.x;
                        double offsetY = y + speed.y;
                        double offsetZ = z + speed.z;

                        ((ServerLevel) entity.level()).sendParticles(new FireParticle.FireParticleOptions(entity.getBbWidth(), true, 20),
                                offsetX, offsetY, offsetZ, 0, speed.x, speed.y, speed.z, 1.0D);
                    }
                }, i * 2);
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
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Override
    public Classification getClassification() {
        return Classification.FIRE;
    }
}
