package radon.jujutsu_kaisen.ability.disaster_tides;


import net.minecraft.world.level.block.Blocks;
import radon.jujutsu_kaisen.ability.IClosedDomain;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.DomainExpansion;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.domain.base.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.EelShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.PiranhaShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.SharkShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.FishShikigamiProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class HorizonOfTheCaptivatingSkandha extends DomainExpansion implements IClosedDomain {
    private static final float DAMAGE = 10.0F;

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
        super.onHitEntity(domain, owner, entity, instant);

        if (instant || owner.level().getGameTime() % 20 == 0) {
            float power = this.getOutput(owner) * (instant ? 0.5F : 1.0F);

            entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain, owner, this),
                    DAMAGE * power);

            if (owner.hasLineOfSight(entity)) {
                float xOffset = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F;
                float yOffset = owner.getBbHeight() + ((HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F);

                FishShikigamiProjectile[] projectiles = new FishShikigamiProjectile[]{
                        new EelShikigamiProjectile(owner, power, xOffset, yOffset, entity),
                        new SharkShikigamiProjectile(owner, power, xOffset, yOffset, entity),
                        new PiranhaShikigamiProjectile(owner, power, xOffset, yOffset, entity)
                };
                FishShikigamiProjectile projectile = projectiles[HelperMethods.RANDOM.nextInt(projectiles.length)];
                projectile.setDomain(true);
                owner.level().addFreshEntity(projectile);
            }
        }
    }

    @Override
    protected DomainExpansionEntity summon(LivingEntity owner) {
        ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this);
        owner.level().addFreshEntity(domain);
        return domain;
    }

    @Override
    public List<Block> getBlocks() {
        return List.of(Blocks.SAND);
    }
}
