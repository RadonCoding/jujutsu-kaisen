package radon.jujutsu_kaisen.block.base;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface ITemporaryBlockEntity {
    BlockState getOriginal();

    @Nullable
    CompoundTag getSaved();
}
