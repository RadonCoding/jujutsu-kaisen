package radon.jujutsu_kaisen.entity.domain;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;

public class ChimeraShadowGardenEntity extends DomainExpansionCenterEntity {
    public ChimeraShadowGardenEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ChimeraShadowGardenEntity(DomainExpansionEntity domain) {
        super(JJKEntities.CHIMERA_SHADOW_GARDEN.get(), domain);
    }
}
