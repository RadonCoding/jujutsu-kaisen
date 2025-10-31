package radon.jujutsu_kaisen.ability.disaster_plants;


import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.ability.IClosedDomain;
import radon.jujutsu_kaisen.entity.domain.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.domain.ClosedDomainExpansionEntity;

import java.util.List;

public class ShiningSeaOfFlowers extends DomainExpansion implements IClosedDomain {
    @Override
    public void onHitLiving(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitLiving(domain, owner, entity, instant);

        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Math.round(10 * 20 * (instant ? 0.5F : 1.0F)),
                4, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, Math.round(10 * 20 * (instant ? 0.5F : 1.0F)),
                4, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, Math.round(10 * 20 * (instant ? 0.5F : 1.0F)),
                4, false, false, false));
    }

    @Override
    protected DomainExpansionEntity summon(LivingEntity owner) {
        ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this);
        owner.level().addFreshEntity(domain);

        return domain;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(Blocks.GRASS_BLOCK);
    }
}
