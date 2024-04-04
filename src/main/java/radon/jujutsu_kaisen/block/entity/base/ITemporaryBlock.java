package radon.jujutsu_kaisen.block.entity.base;

import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface ITemporaryBlock {
    @Nullable
    BlockState getOriginal();
}
