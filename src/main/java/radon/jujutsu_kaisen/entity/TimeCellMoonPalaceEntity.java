package radon.jujutsu_kaisen.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.base.DomainExpansionCenterEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public class TimeCellMoonPalaceEntity extends DomainExpansionCenterEntity {
    public TimeCellMoonPalaceEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public TimeCellMoonPalaceEntity(DomainExpansionEntity domain) {
        super(JJKEntities.TIME_CELL_MOON_PALACE.get(), domain.level());

        this.setDomain(domain);
    }
}
