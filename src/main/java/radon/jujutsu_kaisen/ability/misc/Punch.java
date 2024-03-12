package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Punch extends Ability {
    private static final double RANGE = 3.0D;
    private static final float DAMAGE = 5.0F;
    private static final double LAUNCH_POWER = 2.5D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (owner.isInWall() || owner.getNavigation().isStuck()) return true;
        if (target == null || target.isDeadOrDying()) return false;

        HitResult hit = RotationUtil.getLookAtHit(owner, 1.0D);

        if (hit.getType() == HitResult.Type.BLOCK) {
            if (owner.level().getBlockState(((BlockHitResult) hit).getBlockPos()).getBlock().defaultDestroyTime() > Block.INDESTRUCTIBLE) {
                return true;
            }
        }

        if (!owner.hasLineOfSight(target) || owner.distanceTo(target) > RANGE) return false;

        return HelperMethods.RANDOM.nextInt(3) == 0;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public boolean isMelee() {
        return true;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (!(owner.level() instanceof ServerLevel level)) return;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

        for (int i = 0; i < 8; i++) {
            Vec3 pos = owner.getEyePosition().add(look.scale(2.5D));
            level.sendParticles(owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwordItem ? ParticleTypes.SWEEP_ATTACK : ParticleTypes.CLOUD,
                    pos.x + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                    pos.y + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                    pos.z + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                    0, 0.0D, 0.0D, 0.0D, 1.0D);
        }
        for (int i = 0; i < 8; i++) {
            Vec3 pos = owner.getEyePosition().add(look.scale(2.5D));
            level.sendParticles(ParticleTypes.CRIT,
                    pos.x + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                    pos.y + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                    pos.z + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                    0, 0.0D, 0.0D, 0.0D, 1.0D);
        }

        Vec3 start = owner.getEyePosition().add(look);
        owner.level().playSound(null, start.x, start.y, start.z, SoundEvents.GENERIC_SMALL_FALL, SoundSource.MASTER, 1.0F, 0.3F);

        Vec3 end = owner.getEyePosition().add(look.scale(RANGE / 2));

        AABB bounds = AABB.ofSize(end, RANGE, RANGE, RANGE).inflate(1.0D);

        boolean hit = false;

        for (LivingEntity entity : owner.level().getEntitiesOfClass(LivingEntity.class, bounds,
                EntitySelector.ENTITY_STILL_ALIVE.and(entity -> entity != owner && owner.hasLineOfSight(entity)))) {
            if (Math.sqrt(entity.distanceToSqr(bounds.getCenter())) > RANGE) continue;

            hit = true;

            Vec3 center = entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D);
            level.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
            entity.level().playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

            if (owner instanceof Player player) {
                player.attack(entity);
            } else {
                owner.doHurtTarget(entity);
            }
            entity.invulnerableTime = 0;

            if (!entity.isDeadOrDying()) {
                if (data.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                    if (entity.hurt(owner instanceof Player player ? owner.damageSources().playerAttack(player) : owner.damageSources().mobAttack(owner), DAMAGE * this.getPower(owner))) {
                        entity.setDeltaMovement(look.scale(LAUNCH_POWER * (1.0F + this.getPower(owner) * 0.1F) * 2.0F)
                                .multiply(1.0D, 0.25D, 1.0D));
                    }
                } else {
                    if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getPower(owner))) {
                        entity.setDeltaMovement(look.scale(LAUNCH_POWER * (1.0F + this.getPower(owner) * 0.1F))
                                .multiply(1.0D, 0.25D, 1.0D));
                    }
                }
            }
        }

        if (!hit) {
            BlockPos.betweenClosedStream(bounds).forEach(pos -> {
                if (pos.getCenter().distanceTo(bounds.getCenter()) > RANGE) return;
                if (!HelperMethods.isDestroyable(level, owner, pos)) return;

                owner.level().destroyBlock(pos, true, owner);
            });
        }
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        return owner.isUsingItem() ? Status.FAILURE : super.isTriggerable(owner);
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return (!(owner instanceof ISorcerer sorcerer) || sorcerer.hasMeleeAttack() && sorcerer.hasArms()) && super.isValid(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISorcererData data = cap.getSorcererData();

        if (data == null) return 0.0F;

        return data.hasTrait(Trait.HEAVENLY_RESTRICTION) ? 0.0F : 30.0F;
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}
