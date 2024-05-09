package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;

import java.util.List;

public class ShiningSeaOfFlowers extends DomainExpansion implements DomainExpansion.IClosedDomain {
    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Math.round(10 * 20 * (instant ? 0.5F : 1.0F)),
                4, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, Math.round(10 * 20 * (instant ? 0.5F : 1.0F)),
                4, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, Math.round(10 * 20 * (instant ? 0.5F : 1.0F)),
                4, false, false, false));
    }

    @Override
    protected DomainExpansionEntity createBarrier(LivingEntity owner) {
        ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this);
        owner.level().addFreshEntity(domain);

        return domain;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.SHINING_SEA_OF_FLOWERS.get());
    }

    @Override
    public List<Block> getFillBlocks() {
        return List.of(JJKBlocks.SHINING_SEA_OF_FLOWERS_FILL.get());
    }

    @Override
    public List<Block> getFloorBlocks() {
        return List.of(JJKBlocks.SHINING_SEA_OF_FLOWERS_FLOOR.get());
    }

    @Override
    public List<Block> getDecorationBlocks() {
        return List.of(JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_ONE.get(),
                JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_TWO.get(),
                JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_THREE.get(),
                JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_FOUR.get());
    }
}
