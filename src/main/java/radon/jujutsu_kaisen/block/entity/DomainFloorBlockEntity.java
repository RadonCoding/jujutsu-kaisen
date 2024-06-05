package radon.jujutsu_kaisen.block.entity;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.base.TemporaryBlockEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;

import java.util.UUID;

public class DomainFloorBlockEntity extends BlockEntity {
    public DomainFloorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DOMAIN_FLOOR.get(), pPos, pBlockState);
    }
}