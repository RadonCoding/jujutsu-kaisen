package radon.jujutsu_kaisen.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.base.TemporaryBlockEntity;

public class DurationBlockEntity extends TemporaryBlockEntity {
    private int duration;


    public DurationBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public DurationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(JJKBlockEntities.DURATION.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DurationBlockEntity pBlockEntity) {
        if (--pBlockEntity.duration <= 0) {
            pBlockEntity.destroy();
        }
    }

    public void setDuration(int duration) {
        this.duration = duration;
        this.setChanged();
    }

    public void create(int duration, BlockState state) {
        this.duration = duration;
        this.setOriginal(state);
        this.setChanged();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        pTag.putInt("duration", this.duration);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        this.duration = pTag.getInt("duration");
    }
}
