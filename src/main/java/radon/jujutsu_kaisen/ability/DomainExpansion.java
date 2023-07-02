package radon.jujutsu_kaisen.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public abstract class DomainExpansion extends Ability {
    public static final int BURNOUT = 30 * 20;

    @Override
    public ActivationType getActivationType() {
        return ActivationType.INSTANT;
    }

    protected abstract int getDuration();

    public abstract void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, Entity entity);
    public abstract void onHitBlock(DomainExpansionEntity domain, LivingEntity owner,  BlockPos pos);

    protected abstract void createBarrier(LivingEntity owner);

    @Override
    public final int getCooldown() {
        return 60 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public void run(LivingEntity owner) {
        this.createBarrier(owner);
    }

    public interface IClosedDomain {
        int getRadius();
        Block getBlock();
    }

    public interface IOpenDomain {
        int getWidth();
        int getHeight();
    }
}
