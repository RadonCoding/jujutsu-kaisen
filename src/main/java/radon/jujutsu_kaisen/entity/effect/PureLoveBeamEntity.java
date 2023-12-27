package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.BeamEntity;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;

public class PureLoveBeamEntity extends BeamEntity {
    public static final int FRAMES = 3;
    public static final float SCALE = 2.0F;
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
        return 16.0D;
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
}