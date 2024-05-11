package radon.jujutsu_kaisen.block.base;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TemporaryBlockEntity extends BlockEntity implements ITemporaryBlockEntity {
    @Nullable
    private BlockState original;

    private CompoundTag deferred;

    @Nullable
    private CompoundTag saved;

    public TemporaryBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void setOriginal(@Nullable BlockState original) {
        this.original = original;
    }

    public void setSaved(@Nullable CompoundTag saved) {
        this.saved = saved;
    }

    public void destroy() {
        if (this.level == null) return;

        BlockState original = this.getOriginal();

        if (original != null) {
            if (original.isAir()) {
                this.level.setBlockAndUpdate(this.getBlockPos(), Blocks.AIR.defaultBlockState());
            } else {
                this.level.setBlockAndUpdate(this.getBlockPos(), original);

                if (this.saved != null) {
                    BlockEntity be = this.level.getBlockEntity(this.getBlockPos());

                    if (be != null) {
                        be.loadWithComponents(this.saved, this.level.registryAccess());
                    }
                }
            }
        } else {
            this.level.setBlockAndUpdate(this.getBlockPos(), Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    @Nullable
    public BlockState getOriginal() {
        if (this.level == null) return this.original;

        if (this.original == null && this.deferred != null) {
            this.original = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), this.deferred);
            this.deferred = null;
            this.setChanged();
        }
        return this.original;
    }

    @Override
    @Nullable
    public CompoundTag getSaved() {
        return this.saved;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        if (this.original != null) {
            pTag.put("original", NbtUtils.writeBlockState(this.original));
        } else {
            pTag.put("original", this.deferred);
        }

        if (this.saved != null) {
            pTag.put("saved", this.saved);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        if (pTag.contains("original")) {
            this.deferred = pTag.getCompound("original");
        }

        if (pTag.contains("saved")) {
            this.saved = pTag.getCompound("saved");
        }
    }
}
