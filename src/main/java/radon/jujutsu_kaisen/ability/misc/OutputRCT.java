package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class OutputRCT extends Ability {
    public static final float RANGE = 5.0F;

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
        if (target == null || target.isDeadOrDying() || !owner.hasLineOfSight(target)) return false;

        IJujutsuCapability jujutsuCap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

if (jujutsuCap == null) return false;

ISorcererData data = jujutsuCap.getSorcererData();

        if (data == null) return false;

        return data.getType() == JujutsuType.CURSE && this.getTarget(owner) == target;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.RCT3.get();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(5.0F, 2.0F);
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.outputRCTCost.get();
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable LivingEntity getTarget(LivingEntity owner) {
        if (RotationUtil.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
            return target;
        }
        return null;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (!(owner.level() instanceof ServerLevel level)) return;

        LivingEntity target = this.getTarget(owner);

        if (target == null) return;

        IJujutsuCapability ownerJujutsuCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerJujutsuCap == null) return;

        ISorcererData ownerData = ownerJujutsuCap.getSorcererData();

        for (int i = 0; i < 8; i++) {
            ownerData.delayTickEvent(() -> {
                for (int j = 0; j < 8; j++) {
                    double x = target.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (target.getBbWidth() * 1.25F) - target.getLookAngle().scale(0.35D).x;
                    double y = target.getY() + HelperMethods.RANDOM.nextDouble() * (target.getBbHeight());
                    double z = target.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (target.getBbWidth() * 1.25F) - target.getLookAngle().scale(0.35D).z;
                    double speed = (target.getBbHeight() * 0.1F) * HelperMethods.RANDOM.nextDouble();
                    level.sendParticles(new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.RCT, target.getBbWidth() * 0.5F,
                            0.2F, 16), x, y, z, 0, 0.0D, speed, 0.0D, 1.0D);
                }
            }, i * 2);
        }

        float amount = ConfigHolder.SERVER.sorcererHealingAmount.get().floatValue() * this.getPower(owner) * 5 * 20.0F;

        IJujutsuCapability targetJujutsuCap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (targetJujutsuCap == null) return;

        ISorcererData targetData = targetJujutsuCap.getSorcererData();

        if (targetData != null && targetData.getType() == JujutsuType.CURSE) {
            target.hurt(JJKDamageSources.jujutsuAttack(owner, this), amount);
            return;
        }
        target.heal(amount);
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        LivingEntity target = this.getTarget(owner);

        if (target == null) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @Override
    public int getCooldown() {
        return 10;
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability jujutsuCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap == null) return false;

        ISorcererData data = jujutsuCap.getSorcererData();

        if (data == null || data.getType() == JujutsuType.CURSE) return false;

        return super.isValid(owner);
    }
}
