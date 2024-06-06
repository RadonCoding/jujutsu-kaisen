package radon.jujutsu_kaisen.entity.effect;


import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.JujutsuProjectile;

public class ForestDashEntity extends JujutsuProjectile {
    public static final float SIZE = 3.0F;
    private static final int DURATION = 5 * 20;

    public ForestDashEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ForestDashEntity(LivingEntity owner) {
        super(JJKEntities.FOREST_DASH.get(), owner.level(), owner);
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

    @Override
    public void tick() {
        super.tick();

       if (this.getTime() >= DURATION) {
           this.discard();
       }
    }
}
