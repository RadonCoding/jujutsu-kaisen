package radon.jujutsu_kaisen.entity.domain;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.DomainExpansionCenterEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ChimeraShadowGardenEntity extends DomainExpansionCenterEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ChimeraShadowGardenEntity(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ChimeraShadowGardenEntity(DomainExpansionEntity domain) {
        super(JJKEntities.CHIMERA_SHADOW_GARDEN.get(), domain);
    }
}
