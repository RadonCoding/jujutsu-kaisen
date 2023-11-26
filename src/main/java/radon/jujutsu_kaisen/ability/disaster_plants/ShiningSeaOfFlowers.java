package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.List;

public class ShiningSeaOfFlowers extends DomainExpansion implements DomainExpansion.IClosedDomain {
    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Math.round(30 * 20 * (1.6F - cap.getDomainSize())), 4, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, Math.round(30 * 20 * (1.6F - cap.getDomainSize())), 4, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, Math.round(30 * 20 * (1.6F - cap.getDomainSize())), 4, false, false, false));
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {

    }

    @Override
    protected void createBarrier(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            int radius = Math.round(this.getRadius(owner));

            ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this, radius);
            owner.level().addFreshEntity(domain);

            cap.setDomain(domain);
        });
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
        return List.of(JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION.get());
    }

    @Override
    public boolean canPlaceFloor(BlockGetter getter, BlockPos pos) {
        return getter.getBlockState(pos).canOcclude() && getter.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean canPlaceDecoration(BlockGetter getter, BlockPos pos) {
        return getter.getBlockState(pos.below()).is(JJKBlocks.SHINING_SEA_OF_FLOWERS_FLOOR.get());
    }
}
