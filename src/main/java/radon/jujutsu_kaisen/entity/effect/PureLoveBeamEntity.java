package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.effect.base.BeamEntity;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class PureLoveBeamEntity extends BeamEntity {
    public static final int FRAMES = 3;
    public static final float SCALE = 2.0F;
    public static final double RANGE = 16.0D;
    private static final float DAMAGE = 10.0F;
    public static final int CHARGE = (int) (2.5F * 20);
    public static final int DURATION = 3 * 20;

    public PureLoveBeamEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public PureLoveBeamEntity(LivingEntity owner, float power) {
        super(JJKEntities.PURE_LOVE.get(), owner, power);
    }

    @Override
    protected boolean shouldSwing() {
        return false;
    }

    @Override
    public int getFrames() {
        return FRAMES;
    }

    @Override
    public float getScale() {
        if (!(this.getOwner() instanceof RikaEntity rika)) return SCALE;
        return SCALE * (rika.isOpen() ? 1.0F : 0.5F);
    }

    @Override
    protected double getRange() {
        return RANGE;
    }

    @Override
    protected float getDamage() {
        if (!(this.getOwner() instanceof RikaEntity rika)) return DAMAGE;
        return DAMAGE * (rika.isOpen() ? 1.0F : 0.5F);
    }

    @Override
    public int getDuration() {
        if (!(this.getOwner() instanceof RikaEntity rika)) return DURATION;
        return (int) (DURATION * (rika.isOpen() ? 1.0F : 0.5F));
    }

    @Override
    public int getCharge() {
        return CHARGE;
    }

    @Override
    protected @Nullable Ability getSource() {
        return JJKAbilities.SHOOT_PURE_LOVE.get();
    }

    @Override
    protected Vec3 calculateSpawnPos(LivingEntity owner) {
        return new Vec3(owner.getX(), owner.getEyeY() - 0.2D - (this.getBbHeight() / 2.0F), owner.getZ())
                .add(RotationUtil.getTargetAdjustedLookAngle(owner));
    }
}