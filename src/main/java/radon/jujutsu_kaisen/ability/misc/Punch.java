package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.List;

public class Punch extends Ability implements Ability.ICharged {
    private static final float DAMAGE = 6.0F;
    private static final double RANGE = 5.0D;
    private static final double LAUNCH_POWER = 2.5D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;
        if (!owner.hasLineOfSight(target)) return false;
        return owner.distanceTo(target) <= RANGE;
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
        return ActivationType.CHANNELED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION) ? 0.0F : 30.0F;
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.MELEE;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        return (!(owner instanceof ISorcerer sorcerer) || sorcerer.hasMeleeAttack() && sorcerer.hasArms()) && super.isValid(owner);
    }

    @Override
    public boolean onRelease(LivingEntity owner) {
        if (owner.isUsingItem()) return false;

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        Vec3 offset = owner.getEyePosition().add(look.scale(RANGE / 2));

        List<LivingEntity> targets = owner.level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(offset, RANGE, RANGE, RANGE), entity -> entity != owner && entity.hasLineOfSight(owner));

        if (targets.isEmpty()) return false;

        if (owner.level() instanceof ServerLevel level) {
            for (int i = 0; i < 4; i++) {
                Vec3 pos = owner.getEyePosition().add(look.scale(2.5D));
                level.sendParticles(owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwordItem ? ParticleTypes.SWEEP_ATTACK : ParticleTypes.CLOUD,
                        pos.x + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                        pos.y + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                        pos.z + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                        0, 0.0D, 0.0D, 0.0D, 1.0D);
            }
            for (int i = 0; i < 4; i++) {
                Vec3 pos = owner.getEyePosition().add(look.scale(2.5D));
                level.sendParticles(ParticleTypes.CRIT,
                        pos.x + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                        pos.y + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                        pos.z + (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.5D,
                        0, 0.0D, 0.0D, 0.0D, 1.0D);
            }

            for (LivingEntity entity : targets) {
                Vec3 pos = entity.position().add(0.0D, entity.getBbHeight() / 2.0F, 0.0D);
                level.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
                entity.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

                owner.swing(InteractionHand.MAIN_HAND, true);

                if (owner instanceof Player player) {
                    player.attack(entity);
                } else {
                    owner.doHurtTarget(entity);
                }
                entity.invulnerableTime = 0;

                if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                    if (entity.hurt(owner instanceof Player player ? owner.damageSources().playerAttack(player) : owner.damageSources().mobAttack(owner), (DAMAGE*1.25F) * this.getPower(owner))) {
                        entity.setDeltaMovement(look.scale(LAUNCH_POWER * (1.0F + this.getPower(owner) * 0.1F) * (cap.hasTrait(Trait.HEAVENLY_RESTRICTION) ? 2.0F : 1.0F))
                                .multiply(1.0D, 0.25D, 1.0D));
                    }
                } else {
                    if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getPower(owner))) {
                        entity.setDeltaMovement(look.scale(LAUNCH_POWER * (1.0F + this.getPower(owner) * 0.1F) * (cap.hasTrait(Trait.HEAVENLY_RESTRICTION) ? 2.0F : 1.0F))
                                .multiply(1.0D, 0.25D, 1.0D));
                    }
                }
            }
        }
        return true;
    }
}
