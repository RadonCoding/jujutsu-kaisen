package radon.jujutsu_kaisen.entity.base;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

public abstract class CurseEntity extends SorcererEntity implements GeoAnimatable {
    protected CurseEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getHealth() < this.getMaxHealth()) {
            AbilityHandler.trigger(this, JJKAbilities.HEAL.get());
        }
    }
}
