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
    private static final float MAX_RADIUS = 1.0F;
    private static final float RADIUS = 0.5F;

    public PureLoveBeamEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public PureLoveBeamEntity(LivingEntity owner, float power) {
        super(JJKEntities.PURE_LOVE.get(), owner, power);
    }

    @Override
    public float getScale() {
        return Math.max(RADIUS, Math.min(MAX_RADIUS, RADIUS * this.getPower()))
                * (this.getOwner() instanceof RikaEntity rika && rika.isOpen() ? 2.0F : 1.0F);
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
    protected boolean shouldSwing() {
        return false;
    }

    @Override
    @Nullable
    protected Ability getSource() {
        return JJKAbilities.SHOOT_PURE_LOVE.get();
    }

    @Override
    protected Vec3 calculateSpawnPos(LivingEntity owner) {
        return new Vec3(owner.getX(), owner.getEyeY() - (owner.getBbHeight() * 0.1F) - (this.getBbHeight() / 2), owner.getZ())
                .add(RotationUtil.getTargetAdjustedLookAngle(owner));
    }

    private void spawnParticles() {
        float intensity = Math.min(1.0F, (float) this.getTime() / CHARGE);

        Vec3 center = new Vec3(this.getX(), this.getY() + (this.getBbHeight() / 2), this.getZ());

        float radius = this.getScale() * 2.0F;

        ParticleAnimator.sphere(this.level(), center, () -> radius * this.random.nextFloat() * 4.0F, () -> radius * 0.2F,
                () -> radius * intensity * this.random.nextFloat() * 0.3F, Math.round(radius * intensity * 8.0F),
                1.0F, true, true, CHARGE - this.getTime(), ParticleColors.PURE_LOVE_DARK);

        ParticleAnimator.sphere(this.level(), center, () -> radius * 0.1F, () -> radius * intensity * 0.25F,
                () -> radius * intensity * 0.2F, Math.round(radius * intensity * 8.0F),
                1.0F, true, true, CHARGE - this.getTime(), ParticleColors.PURE_LOVE_BRIGHT);

        ParticleAnimator.lightning(this.level(), center, radius * intensity * 0.2F, () -> radius * (1.0F + intensity) * this.random.nextFloat() * 4.0F,
                Math.round(radius * intensity * 4.0F), 4, ParticleColors.PURE_LOVE_BRIGHT);
    }

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (this.getTime() - 1 == 0) {
            this.playSound(JJKSounds.PURE_LOVE.get(), 3.0F, 1.0F);
        }

        this.spawnParticles();
    }
}