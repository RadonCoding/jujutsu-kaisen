package radon.jujutsu_kaisen.ability.disaster_tides;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.List;

public class HorizonOfTheCaptivatingSkandha extends DomainExpansion implements DomainExpansion.IClosedDomain {
    @Override
    public int getRadius() {
        return 20;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.HORIZON_OF_THE_CAPTIVATING_SKANDHA.get());
    }

    @Override
    public List<Block> getFillBlocks() {
        return List.of(JJKBlocks.HORIZON_OF_THE_CAPTIVATING_SKANDHA_FILL.get());
    }

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity) {
        if (owner.level.getGameTime() % 10 == 0) {
            Ability fish = JJKAbilities.FISH_SHIKIGAMI.get();
            ((IDomainAttack) fish).perform(owner, domain, entity);
        }
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {

    }

    @Override
    protected void createBarrier(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            int radius = this.getRadius();

            ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this, radius,
                    cap.getGrade().getPower(owner) + (cap.hasTrait(Trait.STRONGEST) ? 1.0F : 0.0F));
            owner.level.addFreshEntity(domain);

            cap.setDomain(domain);
        });
    }

    @Override
    public Classification getClassification() {
        return Classification.DISASTER_TIDES;
    }
}
