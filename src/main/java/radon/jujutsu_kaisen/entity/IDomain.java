package radon.jujutsu_kaisen.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public interface IDomain extends IBarrier {
    @Nullable Level getVirtual();

    float getScale();

    void setInstant(boolean instant);

    boolean isInstant();

    default float getStrength() {
        return IBarrier.super.getStrength();
    }

    void doSureHitEffect(LivingEntity owner);

    boolean hasSureHitEffect();

    boolean checkSureHitEffect();
}
