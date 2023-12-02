package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class Punch extends Ability {
    private static final float DAMAGE = 10.0F;
    private static final double RANGE = 3.0D;
    private static final double LAUNCH_POWER = 2.5D;

    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.distanceTo(target) <= 5.0D;
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

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            if (owner.canAttack(target)) {
                return target;
            }
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        owner.swing(InteractionHand.MAIN_HAND, true);

        LivingEntity target = this.getTarget(owner);

        if (target != null) {
            Vec3 look = owner.getLookAngle();

            Vec3 pos = target.position().add(0.0D, target.getBbHeight() / 2.0F, 0.0D);
            ((ServerLevel) target.level()).sendParticles(ParticleTypes.EXPLOSION, pos.x(), pos.y(), pos.z(), 0, 1.0D, 0.0D, 0.0D, 1.0D);
            target.level().playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);

            if (owner instanceof Player player) {
                player.attack(target);
            } else {
                owner.doHurtTarget(target);
            }
            target.invulnerableTime = 0;

            if (target.hurt(JJKDamageSources.jujutsuAttack(owner, this), DAMAGE * this.getPower(owner))) {
                ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                target.setDeltaMovement(look.scale(LAUNCH_POWER * (1.0F + this.getPower(owner) * 0.1F) * (cap.hasTrait(Trait.HEAVENLY_RESTRICTION) ? 2.0F : 1.0F))
                        .multiply(1.0D, 0.25D, 1.0D));
                target.hurtMarked = true;
            }
        }
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        if (owner.isUsingItem()) return Status.FAILURE;

        LivingEntity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION) ? 0.0F : 50.0F;
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.SCROLL;
    }
}
