package radon.jujutsu_kaisen.ability;


import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

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