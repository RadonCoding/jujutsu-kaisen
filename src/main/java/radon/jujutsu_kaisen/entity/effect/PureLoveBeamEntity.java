package radon.jujutsu_kaisen.entity.effect;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.entity.effect.base.BeamEntity;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.sound.JJKSounds;
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

    @Override
    public void tick() {
        super.tick();

        this.refreshDimensions();

        if (this.getTime() - 1 == 0) {
            this.playSound(JJKSounds.PURE_LOVE.get(), 3.0F, 1.0F);
        }
    }
}