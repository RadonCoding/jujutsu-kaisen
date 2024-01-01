package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import radon.jujutsu_kaisen.ability.base.Ability;

public class AbilityStopEvent extends LivingEvent {
    private final Ability ability;

    public AbilityStopEvent(LivingEntity entity, Ability ability) {
        super(entity);

        this.ability = ability;
    }

    public Ability getAbility() {
        return this.ability;
    }
}