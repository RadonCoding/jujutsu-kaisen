package radon.jujutsu_kaisen.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DomainBlockEntity extends BlockEntity {
    private UUID identifier;
    private int duration;
    private BlockState original;

    @Nullable
    private CompoundTag deferred;

    public DomainBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DOMAIN_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DomainBlockEntity pBlockEntity) {
        if (pBlockEntity.deferred != null) {
            pBlockEntity.setOriginal(NbtUtils.readBlockState(pLevel.holderLookup(Registries.BLOCK), pBlockEntity.deferred));
            pBlockEntity.deferred = null;
        }

        Entity domain = ((ServerLevel) pLevel).getEntity(pBlockEntity.identifier);

        if (--pBlockEntity.duration == 0 || domain == null || domain.isRemoved() || !domain.isAlive()) {
            if (pBlockEntity.original.isAir()) {
                pLevel.destroyBlock(pPos, false);
            } else {
                pLevel.setBlockAndUpdate(pPos, pBlockEntity.original);
            }
        }
        pBlockEntity.setChanged();
    }

    public BlockState getOriginal() {
        return this.original;
    }

    public void setOriginal(BlockState original) {
        this.original = original;
        this.setChanged();
    }

    public void create(UUID identifier, int duration, BlockState state) {
        this.identifier = identifier;
        this.duration = duration;
        this.original = state;
        this.setChanged();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        this.identifier = nbt.getUUID("identifier");
        this.duration = nbt.getInt("duration");
        this.deferred = nbt.getCompound("original");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.putUUID("identifier", this.identifier);
        nbt.putInt("duration", this.duration);
        nbt.put("original", NbtUtils.writeBlockState(this.original));
    }
}
