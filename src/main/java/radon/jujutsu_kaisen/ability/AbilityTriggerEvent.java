package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class AbilityTriggerEvent extends LivingEvent {
    private final Ability ability;

    public AbilityTriggerEvent(LivingEntity entity, Ability ability) {
        super(entity);

        this.ability = ability;
    }

    public Ability getAbility() {
        return this.ability;
    }
}