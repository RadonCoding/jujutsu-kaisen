package radon.jujutsu_kaisen.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;

public class LivingHitByDomainEvent extends LivingEvent {
    private final DomainExpansion ability;

    public LivingHitByDomainEvent(LivingEntity entity, DomainExpansion ability) {
        super(entity);

        this.ability = ability;
    }

    public DomainExpansion getAbility() {
        return this.ability;
    }
}