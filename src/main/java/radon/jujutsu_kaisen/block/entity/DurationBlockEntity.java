package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.entity.base.ITemporaryBlock;

public class DurationBlockEntity extends BlockEntity implements ITemporaryBlock {
    private boolean initialized;
    private int duration;

    @Nullable
    private BlockState original;

    private CompoundTag deferred;

    public DurationBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public DurationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(JJKBlockEntities.DURATION.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DurationBlockEntity pBlockEntity) {
        if (--pBlockEntity.duration <= 0) {
            BlockState original = pBlockEntity.getOriginal();

            if (original != null) {
                if (original.isAir()) {
                    pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
                } else {
                    pLevel.setBlockAndUpdate(pPos, original);
                }
            } else {
                pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    @Nullable
    @Override
    public BlockState getOriginal() {
        if (this.original == null && this.deferred != null && this.level != null) {
            this.original = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), this.deferred);
            this.deferred = null;
            this.setChanged();
        }
        return this.original;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        this.setChanged();
    }

    public void create(int duration, BlockState state) {
        this.initialized = true;
        this.duration = duration;
        this.original = state;
        this.setChanged();
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        this.initialized = pTag.getBoolean("initialized");

        if (this.initialized) {
            this.duration = pTag.getInt("duration");
            this.deferred = pTag.getCompound("original");
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.putBoolean("initialized", this.initialized);

        if (this.initialized) {
            pTag.putInt("duration", this.duration);

            if (this.original != null) {
                pTag.put("original", NbtUtils.writeBlockState(this.original));
            } else {
                pTag.put("original", this.deferred);
            }
        }
    }
}
