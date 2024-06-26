package radon.jujutsu_kaisen.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CurseSpawnerBlockEntity extends BlockEntity {
    private BlockPos pos;

    public CurseSpawnerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.CURSE_SPAWNER.get(), pPos, pBlockState);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
        this.setChanged();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        pTag.put("pos", NbtUtils.writeBlockPos(this.pos));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        this.pos = NbtUtils.readBlockPos(pTag, "pos").orElseThrow();
    }
}
