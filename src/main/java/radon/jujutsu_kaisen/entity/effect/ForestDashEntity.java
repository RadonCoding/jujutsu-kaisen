package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;

public class ForestDashEntity extends JujutsuProjectile {
    public static final float SIZE = 3.0F;

    public ForestDashEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ForestDashEntity(LivingEntity owner) {
        super(JJKEntities.FOREST_DASH.get(), owner.level(), owner);
    }

    @Override
    protected int getDuration() {
        return 5 * 20;
    }

    @Override
    public void push(@NotNull Entity pEntity) {

    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected boolean isProjectile() {
        return false;
    }
}
