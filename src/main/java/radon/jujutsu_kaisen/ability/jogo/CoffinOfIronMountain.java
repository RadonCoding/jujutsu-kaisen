package radon.jujutsu_kaisen.ability.jogo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.DomainBlock;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public class CoffinOfIronMountain extends DomainExpansion implements DomainExpansion.IClosedDomain {
    private static final float DAMAGE = 10.0F;

    @Override
    public float getCost(LivingEntity owner) {
        return 1000.0F;
    }

    @Override
    public int getRadius() {
        return 20;
    }

    @Override
    protected int getDuration() {
        return 30 * 20;
    }

    @Override
    public DomainBlock getBlock() {
        return JJKBlocks.COFFIN_OF_IRON_MOUNTAIN.get();
    }

    @Override
    public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, Entity entity) {
        if (owner.level.getGameTime() % 20 == 0) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain, owner), DAMAGE * cap.getGrade().getPower())) {
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
            int duration = this.getDuration();
            int radius = this.getRadius();
            Block block = this.getBlock();

            ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this, block.defaultBlockState(), radius, duration);
            owner.level.addFreshEntity(domain);
        });
    }
}
