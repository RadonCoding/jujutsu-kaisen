package radon.jujutsu_kaisen.entity.effect;


import net.minecraft.world.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ParticleAnimator;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.entity.effect.base.BeamEntity;
import radon.jujutsu_kaisen.entity.projectile.JujutsuProjectile;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

public class PureLoveBeamEntity extends BeamEntity {
    public static final double RANGE = 32.0D;
    public static final int CHARGE = (int) (2.5F * 20);
    public static final int DURATION = 3 * 20;
    private static final float SPEED = 5.0F;
    private static final float MAX_RADIUS = 1.0F;
    private static final float RADIUS = 0.5F;

    public PureLoveBeamEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public PureLoveBeamEntity(LivingEntity owner, float power) {
        super(JJKEntities.PURE_LOVE.get(), owner, power);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (owner.getBbHeight() * 0.1F) -
                (this.getBbHeight() / 2), owner.getZ()).add(look));
    }

    @Override
    public int getFrames() {
        return 16;
    }

    @Override
    public float getScale() {
        return 1.0F;
    }

    @Override
    protected double getRange() {
        return RANGE;
    }

    @Override
    protected float getDamage() {
        return 30.0F;
    }

    @Override
    public int getDuration() {
        return DURATION;
    }

    @Override
    public int getCharge() {
        return CHARGE;
    }

    @Override
    @Nullable
    protected Ability getSource() {
        return JJKAbilities.SHOOT_PURE_LOVE.get();
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        float radius = this.getRadius();
        return EntityDimensions.fixed(radius, radius);
    }

    public float getRadius() {
        return Math.max(RADIUS, Math.min(MAX_RADIUS, RADIUS * this.getPower()))
                * (this.getOwner() instanceof RikaEntity rika && rika.isOpen() ? 2.0F : 1.0F);
    }

    private void animate() {
        float intensity = (float) this.getTime() / CHARGE;

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2), this.getZ());

        float radius = this.getRadius();

        ParticleAnimator.sphere(this.level(), center, () -> radius * this.random.nextFloat() * 4.0F, () -> radius * 0.2F,
                () -> radius * intensity * this.random.nextFloat() * 0.3F, Math.round(radius * intensity * 8.0F),
                1.0F, true, true, CHARGE - this.getTime(), ParticleColors.PURE_LOVE_DARK);

        ParticleAnimator.sphere(this.level(), center, () -> radius * 0.1F, () -> radius * intensity * 0.25F,
                () -> radius * intensity * 0.2F, Math.round(radius * intensity * 8.0F),
                1.0F, true, true, CHARGE - this.getTime(), ParticleColors.PURE_LOVE_BRIGHT);

        ParticleAnimator.lightning(this.level(), center, radius * 0.2F, () -> radius * (1.0F + intensity) * this.random.nextFloat() * 4.0F,
                Math.round(radius * intensity * 4.0F), 4, ParticleColors.PURE_LOVE_BRIGHT);
    }

    private void spawnParticles() {
        if (this.getTime() <= CHARGE) {
            this.animate();
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (this.getTime() >= DURATION) {
            this.discard();
            return;
        }

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        if (this.getTime() - 1 == 0) {
            this.playSound(JJKSounds.PURE_LOVE.get(), 3.0F, 1.0F);
        }

        this.spawnParticles();

        if (this.getTime() < CHARGE) {
            Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
            EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (owner.getBbHeight() * 0.1F) -
                    (this.getBbHeight() / 2), owner.getZ()).add(look));
        } else if (this.getTime() >= DURATION) {
            this.discard();
        } else if (this.getTime() >= CHARGE) {
            if (this.getTime() == CHARGE) {
                this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
            }
        }
    }
}