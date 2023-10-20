package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Barrage extends Ability {
    private static final double RANGE = 5.0D;
    public static int DURATION = 8;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && HelperMethods.RANDOM.nextInt(3) == 0 && owner.hasLineOfSight(target) && owner.distanceTo(target) <= RANGE;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level() instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (int i = 0; i < DURATION; i++) {
                    cap.delayTickEvent(() -> {
                        owner.swing(InteractionHand.MAIN_HAND, true);

                        Vec3 look = HelperMethods.getLookAngle(owner);
                        Vec3 pos = owner.getEyePosition().add(look);

                        for (int j = 0; j < 4; j++) {
                            Vec3 speed = look.add((HelperMethods.RANDOM.nextDouble() - 0.5D) * 1.5D,
                                    (HelperMethods.RANDOM.nextDouble() - 0.5D) * 1.5D,
                                    (HelperMethods.RANDOM.nextDouble() - 0.5D) * 1.5D);
                            Vec3 offset = owner.getEyePosition().add(HelperMethods.getLookAngle(owner));
                            level.sendParticles(ParticleTypes.CLOUD, offset.x() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 1.5D,
                                    offset.y() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 1.5D,
                                    offset.z() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 1.5D,
                                    0, speed.x(), speed.y(), speed.z(), 1.0D);
                        }
                        owner.level().playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.GENERIC_SMALL_FALL, SoundSource.MASTER, 1.0F, 0.3F);

                        Vec3 offset = owner.getEyePosition().add(HelperMethods.getLookAngle(owner).scale(RANGE / 2));

                        for (Entity entity : owner.level().getEntities(owner, AABB.ofSize(offset, RANGE, RANGE, RANGE))) {
                            if (owner instanceof Player player) {
                                player.attack(entity);
                            } else {
                                owner.doHurtTarget(entity);
                            }
                            entity.invulnerableTime = 0;
                        }
                    }, i * 2);
                }
            });
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    public int getCooldown() {
        return 3 * 20;
    }

    @Override
    public boolean isMelee() {
        return true;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
