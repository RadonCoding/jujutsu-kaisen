package radon.jujutsu_kaisen.entity.domain;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.DomainExpansionCenterEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;

public class MalevolentShrineEntity extends DomainExpansionCenterEntity {
    public MalevolentShrineEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public MalevolentShrineEntity(DomainExpansionEntity domain) {
        super(JJKEntities.MALEVOLENT_SHRINE.get(), domain.level());

        this.setDomain(domain);
    }
}
