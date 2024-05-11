package radon.jujutsu_kaisen.ability.disaster_tides;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.IDomainAttack;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;

import java.util.List;

public class HorizonOfTheCaptivatingSkandha extends DomainExpansion implements DomainExpansion.IClosedDomain {
    private static final float DAMAGE = 10.0F;

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        if (instant || owner.level().getGameTime() % 20 == 0) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain, owner, JJKAbilities.DEATH_SWARM.get()),
                    DAMAGE * this.getOutput(owner));

            if (owner.hasLineOfSight(entity)) {
                DeathSwarm swarm = JJKAbilities.DEATH_SWARM.get();
                swarm.performEntity(owner, entity, domain, instant);
            }
        }
    }

    @Override
    protected DomainExpansionEntity createBarrier(LivingEntity owner) {
        ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this);
        owner.level().addFreshEntity(domain);
        return domain;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(JJKBlocks.HORIZON_OF_THE_CAPTIVATING_SKANDHA.get());
    }

    @Override
    public List<Block> getFillBlocks() {
        return List.of(JJKBlocks.HORIZON_OF_THE_CAPTIVATING_SKANDHA_FILL.get());
    }
}
