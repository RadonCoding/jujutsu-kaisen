package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.List;

public class CoffinOfTheIronMountain extends DomainExpansion implements DomainExpansion.IClosedDomain {
    private static final float DAMAGE = 10.0F;

    @Override
    public int getRadius() {
        return 20;
    }

    public List<Block> getBlocks() {
        return List.of(JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_ONE.get(),
                JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_TWO.get(),
                JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_THREE.get());
    }

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity) {
        super.onHitEntity(domain, owner, entity);

        if (owner.level.getGameTime() % 20 == 0) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain, owner, this), DAMAGE * cap.getGrade().getPower(owner))) {
                    entity.setSecondsOnFire(15);
                }
            });
        }
    }

    @Override
    public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {

    }

    @Override
    protected void createBarrier(LivingEntity owner) {
        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            int radius = this.getRadius();
            List<Block> blocks = this.getBlocks();

            ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this, radius);
            owner.level.addFreshEntity(domain);

            cap.setDomain(domain);
        });
    }

    @Override
    public Classification getClassification() {
        return Classification.DISASTER_FLAMES;
    }
}
