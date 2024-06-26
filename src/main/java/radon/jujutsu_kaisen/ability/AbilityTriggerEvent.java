package radon.jujutsu_kaisen.ability;


import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public abstract class AbilityTriggerEvent extends LivingEvent {
    private final Ability ability;

    protected AbilityTriggerEvent(LivingEntity entity, Ability ability) {
        super(entity);

        this.ability = ability;
    }

    public Ability getAbility() {
        return this.ability;
    }

    public static class Pre extends AbilityTriggerEvent {
        public Pre(LivingEntity entity, Ability ability) {
            super(entity, ability);
        }
    }

    public static class Post extends AbilityTriggerEvent {
        public Post(LivingEntity entity, Ability ability) {
            super(entity, ability);
        }
    }
}