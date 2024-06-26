package radon.jujutsu_kaisen.ability.event;


import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import radon.jujutsu_kaisen.ability.DomainExpansion;

public class LivingInsideDomainEvent extends LivingEvent {
    private final DomainExpansion ability;
    private final LivingEntity attacker;

    public LivingInsideDomainEvent(LivingEntity entity, DomainExpansion ability, LivingEntity attacker) {
        super(entity);

        this.ability = ability;
        this.attacker = attacker;
    }

    public DomainExpansion getAbility() {
        return this.ability;
    }

    public LivingEntity getAttacker() {
        return this.attacker;
    }
}