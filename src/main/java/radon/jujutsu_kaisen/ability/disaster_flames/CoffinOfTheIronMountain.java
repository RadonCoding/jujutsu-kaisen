package radon.jujutsu_kaisen.ability.disaster_flames;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.LavaRockProjectile;

import java.util.List;

public class CoffinOfTheIronMountain extends DomainExpansion implements DomainExpansion.IClosedDomain {
    private static final float DAMAGE = 10.0F;

    public List<Block> getBlocks() {
        return List.of(JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_ONE.get(),
                JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_TWO.get(),
                JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_THREE.get());
    }

    @Override
    @Nullable
    public ParticleOptions getEnvironmentParticle() {
        return ParticleTypes.LARGE_SMOKE;
    }

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        if (instant || owner.level().getGameTime() % 20 == 0) {
            if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain, owner, this), DAMAGE * this.getOutput(owner))) {
                entity.setRemainingFireTicks(15 * 20);
            }

            if (owner.hasLineOfSight(entity)) {
                LavaRockProjectile rock = new LavaRockProjectile(owner, this.getOutput(owner), entity);
                rock.setDomain(true);
                owner.level().addFreshEntity(rock);
            }
        }
    }

    @Override
    protected DomainExpansionEntity createBarrier(LivingEntity owner) {
        ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this);
        owner.level().addFreshEntity(domain);

        return domain;
    }
}
