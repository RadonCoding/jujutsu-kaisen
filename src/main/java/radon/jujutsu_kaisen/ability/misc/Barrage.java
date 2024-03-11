package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Barrage extends Ability {
    private static final double RANGE = 3.0D;
    public static int DURATION = 8;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;
        if (!owner.hasLineOfSight(target) || owner.distanceTo(target) > RANGE) return false;
        return HelperMethods.RANDOM.nextInt(3) == 0;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        for (int i = 0; i < DURATION; i++) {
            data.delayTickEvent(() -> {
                owner.swing(InteractionHand.MAIN_HAND, true);

                Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

                for (int j = 0; j < 4; j++) {
                    Vec3 pos = owner.getEyePosition().add(look.scale(2.5D));
                    level.sendParticles(owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwordItem ? ParticleTypes.SWEEP_ATTACK : ParticleTypes.CLOUD,
                            pos.x + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                            pos.y + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                            pos.z + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                            0, 0.0D, 0.0D, 0.0D, 1.0D);
                }
                for (int j = 0; j < 4; j++) {
                    Vec3 pos = owner.getEyePosition().add(look.scale(2.5D));
                    level.sendParticles(ParticleTypes.CRIT,
                            pos.x + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                            pos.y + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                            pos.z + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                            0, 0.0D, 0.0D, 0.0D, 1.0D);
                }

                Vec3 pos = owner.getEyePosition().add(look);
                owner.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_SMALL_FALL, SoundSource.MASTER, 1.0F, 0.3F);

                Vec3 offset = owner.getEyePosition().add(look.scale(RANGE / 2));

                for (LivingEntity entity : owner.level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(offset, RANGE, RANGE, RANGE),
                        EntitySelector.ENTITY_STILL_ALIVE.and(entity -> entity != owner && owner.hasLineOfSight(entity)))) {
                    if (owner instanceof Player player) {
                        player.attack(entity);
                    } else {
                        owner.doHurtTarget(entity);
                    }
                    entity.invulnerableTime = 0;
                }
            }, i * 2);
        }
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return (!(owner instanceof ISorcerer sorcerer) || sorcerer.hasMeleeAttack() && sorcerer.hasArms()) && super.isValid(owner);
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        return owner.isUsingItem() ? Status.FAILURE : super.isTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    public int getCooldown() {
        return 3 * 20;
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean isMelee() {
        return true;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
