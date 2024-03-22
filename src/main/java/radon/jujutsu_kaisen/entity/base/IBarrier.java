package radon.jujutsu_kaisen.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;

public interface IBarrier {
    Level level();

    @Nullable
    LivingEntity getOwner();

    default boolean isAffected(BlockPos pos) {
        return this.isOwned(pos) && this.isInsideBarrier(pos);
    }

    default boolean isOwned(BlockPos pos) {
        if (!(this.level() instanceof ServerLevel level)) return false;
        return VeilHandler.isOwnedBy(level, pos, this);
    }

    boolean isInsideBarrier(BlockPos pos);

    boolean isBarrier(BlockPos pos);

    AABB getBounds();

    boolean hasSureHitEffect();

    boolean checkSureHitEffect();
}
