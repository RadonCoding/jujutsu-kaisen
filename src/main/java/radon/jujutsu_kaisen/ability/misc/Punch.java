package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
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
import radon.jujutsu_kaisen.util.HelperMethods;

public class Punch extends Ability implements Ability.ICharged {
    private static final float DAMAGE = 5.0F;
    private static final double RANGE = 16.0D;
    private static final double LAUNCH_POWER = 2.5D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return JJKAbilities.isChanneling(owner, this) ? this.getTarget(owner) == target : target != null && owner.distanceTo(target) <= 5.0D;
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

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            if (!owner.canAttack(target)) return null;

            return target;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        float charge = (float) Math.min(20, this.getCharge(owner)) / 20;

        if (owner.level().isClientSide) {
            ClientWrapper.setOverlayMessage(Component.translatable(String.format("chat.%s.charge", JujutsuKaisen.MOD_ID), charge * 100), false);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return this.getCharge(owner) > 20 ? 0.0F : JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION) ? 0.0F : 30.0F;
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }

    @Override
    public boolean onRelease(LivingEntity owner) {
        if (owner.isUsingItem()) return false;

        LivingEntity target = this.getTarget(owner);

        if (target == null) return false;

        float charge = (float) Math.min(20, this.getCharge(owner)) / 20;

        Vec3 look = owner.getLookAngle();

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        double range = cap.getRealPower() * charge;

        if (charge >= 0.5F) {
            if (owner.distanceTo(target) < range && owner.distanceTo(target) > 3.0D) {
                Vec3 direction = target.position().subtract(owner.position());
                owner.teleportRelative(direction.x, direction.y, direction.z);
            }
        }

        if (owner.distanceTo(target) <= 3.0D) {
            if (!owner.level().isClientSide) {
                cap.delayTickEvent(() -> {
                    Vec3 pos = target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D);
                    ((ServerLevel) target.level()).sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
                    target.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

                    owner.swing(InteractionHand.MAIN_HAND, true);

                    if (owner instanceof Player player) {
                        player.attack(target);
                    } else {
                        owner.doHurtTarget(target);
                    }
                    target.invulnerableTime = 0;

                    if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                        if (target.hurt(owner instanceof Player player ? owner.damageSources().playerAttack(player) : owner.damageSources().mobAttack(owner), DAMAGE * this.getPower(owner) * charge)) {
                            target.setDeltaMovement(look.scale(LAUNCH_POWER * (1.0F + this.getPower(owner) * 0.1F) * (cap.hasTrait(Trait.HEAVENLY_RESTRICTION) ? 2.0F : 1.0F) * charge)
                                    .multiply(1.0D, 0.25D, 1.0D));
                            target.hurtMarked = true;
                        }
                    } else {
                        if (target.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getPower(owner) * charge)) {
                            target.setDeltaMovement(look.scale(LAUNCH_POWER * (1.0F + this.getPower(owner) * 0.1F) * (cap.hasTrait(Trait.HEAVENLY_RESTRICTION) ? 2.0F : 1.0F) * charge)
                                    .multiply(1.0D, 0.25D, 1.0D));
                            target.hurtMarked = true;
                        }
                    }
                }, 1);
            }
            return true;
        }
        return false;
    }
}
