package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.JJKEntities;

public class MaximumHollowPurpleProjectile extends HollowPurpleProjectile {
    public MaximumHollowPurpleProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public MaximumHollowPurpleProjectile(LivingEntity pShooter) {
        super(JJKEntities.MAXIMUM_HOLLOW_PURPLE.get(), pShooter);
    }

    @Override
    public float getSize() {
        return super.getSize() * 2;
    }

    @Override
    protected float getDamage() {
        return super.getDamage() * 2.0F;
    }

    @Override
    protected double getRadius() {
        return super.getRadius() * 2;
    }
}
