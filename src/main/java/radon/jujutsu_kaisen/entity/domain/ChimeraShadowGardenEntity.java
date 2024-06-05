package radon.jujutsu_kaisen.entity.domain;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.DomainExpansionCenterEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
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
