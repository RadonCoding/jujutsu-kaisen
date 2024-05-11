package radon.jujutsu_kaisen.ability;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import radon.jujutsu_kaisen.ability.IAttack;
import radon.jujutsu_kaisen.ability.IChanneled;
import radon.jujutsu_kaisen.ability.ICharged;
import radon.jujutsu_kaisen.ability.IDomainAttack;
import radon.jujutsu_kaisen.ability.IDurationable;
import radon.jujutsu_kaisen.ability.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.IToggled;

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