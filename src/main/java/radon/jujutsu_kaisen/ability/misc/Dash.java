package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.MirageParticle;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Dash extends Ability {
    public static final double RANGE = 60.0D;
    private static final float DASH = 2.0F;
    private static final float MAX_DASH = 5.0F;

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
        if (target == null) return false;
        if (!owner.hasLineOfSight(target)) return false;

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 start = owner.getEyePosition();
        Vec3 result = target.getEyePosition().subtract(start);
        double angle = Math.acos(look.normalize().dot(result.normalize()));
        return angle <= 0.5D;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        if (!canDash(owner)) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    private static boolean canDash(LivingEntity owner) {
        return !owner.hasEffect(JJKEffects.STUN.get()) && RotationUtil.getLookAtHit(owner, RANGE) instanceof EntityHitResult || owner.isInWater() ||
                owner.onGround() || !owner.getFeetBlockState().getFluidState().isEmpty();
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        if (!canDash(owner)) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.getSpeedStacks() > 0 || cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), JJKSounds.DASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
            owner.addEffect(new MobEffectInstance(JJKEffects.INVISIBILITY.get(), 5, 0, false, false, false));
            level.sendParticles(new MirageParticle.MirageParticleOptions(owner.getId()), owner.getX(), owner.getY(), owner.getZ(),
                    0, 0.0D, 0.0D, 0.0D, 1.0D);
        }

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

        if (RotationUtil.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit) {
            Entity target = hit.getEntity();

            double distanceX = target.getX() - owner.getX();
            double distanceY = target.getY() - owner.getY();
            double distanceZ = target.getZ() - owner.getZ();

            double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
            double motionX = distanceX / distance * DASH;
            double motionY = distanceY / distance * DASH;
            double motionZ = distanceZ / distance * DASH;

            owner.setDeltaMovement(motionX, motionY, motionZ);
            owner.hurtMarked = true;
        } else if (owner.onGround() || !owner.getFeetBlockState().getFluidState().isEmpty()) {
            float power = Math.min(MAX_DASH, DASH * (1.0F + this.getPower(owner) * 0.1F) * (cap.hasTrait(Trait.HEAVENLY_RESTRICTION) ? 1.5F : 1.0F));

            float f7 = owner.getYRot();
            float f = owner.getXRot();
            float f1 = -Mth.sin(f7 * ((float) Math.PI / 180.0F)) * Mth.cos(f * ((float) Math.PI / 180.0F));
            float f2 = -Mth.sin(f * ((float) Math.PI / 180.0F));
            float f3 = Mth.cos(f7 * ((float) Math.PI / 180.0F)) * Mth.cos(f * ((float) Math.PI / 180.0F));
            float f4 = Mth.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
            f1 *= power / f4;
            f2 *= power / f4;
            f3 *= power / f4;
            owner.push(f1, f2, f3);
            owner.move(MoverType.SELF, new Vec3(0.0D, 1.1999999F, 0.0D));
            owner.hurtMarked = true;
        }

        Vec3 pos = owner.position();

        for (int i = 0; i < 32; i++) {
            double theta = HelperMethods.RANDOM.nextDouble() * 2 * Math.PI;
            double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
            double r = HelperMethods.RANDOM.nextDouble() * 0.8D;
            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);
            Vec3 speed = look.add(x, y, z).reverse();
            level.sendParticles(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 0, speed.x, speed.y, speed.z, 1.0D);
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public int getCooldown() {
        return 2 * 20;
    }

    @Override
    public int getRealCooldown(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            return 0;
        }
        return super.getRealCooldown(owner);
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.NONE;
    }
}
