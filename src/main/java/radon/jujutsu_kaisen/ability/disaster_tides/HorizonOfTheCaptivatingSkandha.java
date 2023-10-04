package radon.jujutsu_kaisen.ability.disaster_tides;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.List;

public class HorizonOfTheCaptivatingSkandha extends DomainExpansion implements DomainExpansion.IClosedDomain {
    private static final float DAMAGE = 10.0F;

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.HORIZON_OF_THE_CAPTIVATING_SKANDHA.get());
    }

    @Override
    public List<Block> getFillBlocks() {
        return List.of(JJKBlocks.HORIZON_OF_THE_CAPTIVATING_SKANDHA_FILL.get());
    }

    @Override
    public List<Block> getFloorBlocks() {
        return List.of(JJKBlocks.FAKE_WATER_DOMAIN.get());
    }

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity) {
        if (owner.level().getGameTime() % 20 == 0) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain, owner, JJKAbilities.DEATH_SWARM.get()), DAMAGE * cap.getPower() * (1.6F - cap.getDomainSize())));

            if (owner.level().getGameTime() % 3 * 20 == 0) {
                Ability fish = JJKAbilities.DEATH_SWARM.get();
                ((IDomainAttack) fish).perform(owner, domain, entity);
            }
        }
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
}
