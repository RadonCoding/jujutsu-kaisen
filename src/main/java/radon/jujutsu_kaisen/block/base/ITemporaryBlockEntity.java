package radon.jujutsu_kaisen.block.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface ITemporaryBlockEntity {
    BlockState getOriginal();

    @Nullable
    CompoundTag getSaved();
}
