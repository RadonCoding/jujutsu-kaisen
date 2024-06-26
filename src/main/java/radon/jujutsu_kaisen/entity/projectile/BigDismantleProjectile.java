package radon.jujutsu_kaisen.entity.projectile;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;

public class BigDismantleProjectile extends DismantleProjectile {
    public BigDismantleProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public BigDismantleProjectile(LivingEntity owner, float power, float roll) {
        super(JJKEntities.BIG_DISMANTLE.get(), owner, power, roll);
    }

    @Override
    protected boolean isInfinite() {
        return true;
    }

    @Override
    protected float getDamage() {
        return super.getDamage() * 2;
    }

    @Override
    public int getMinLength() {
        return super.getMinLength() * 2;
    }

    @Override
    public int getMaxLength() {
        return super.getMaxLength() * 2;
    }

    @Override
    public int getScalar() {
        return super.getScalar() * 2;
    }
}
