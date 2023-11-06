package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import radon.jujutsu_kaisen.ability.base.Ability;

public class CursedEnergyCostEvent extends LivingEvent {
    private final float cost;

    public CursedEnergyCostEvent(LivingEntity entity, float cost) {
        super(entity);

        this.cost = cost;
    }

    public float getCost() {
        return this.cost;
    }
}