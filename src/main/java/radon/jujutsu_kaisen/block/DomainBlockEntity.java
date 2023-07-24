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
    private boolean initialized;
    private UUID identifier;
    private int duration;
    private BlockState original;

    private CompoundTag deferred;

    public DomainBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DOMAIN_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DomainBlockEntity pBlockEntity) {
        Entity domain = ((ServerLevel) pLevel).getEntity(pBlockEntity.identifier);

        if (--pBlockEntity.duration == 0 || domain == null || domain.isRemoved() || !domain.isAlive()) {
            BlockState original = pBlockEntity.getOriginal();

            if (original != null) {
                if (original.isAir()) {
                    pLevel.destroyBlock(pPos, false);
                } else {
                    pLevel.setBlockAndUpdate(pPos, original);
                }
            }
        }
        pBlockEntity.setChanged();
    }

    public @Nullable BlockState getOriginal() {
        if (this.original == null && this.deferred != null) {
            this.original = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), this.deferred);
            this.deferred = null;
            this.setChanged();
        }
        return this.original;
    }

    public void create(UUID identifier, int duration, BlockState state) {
        this.initialized = true;
        this.identifier = identifier;
        this.duration = duration;
        this.original = state;
        this.setChanged();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        this.initialized = nbt.getBoolean("initialized");

        if (this.initialized) {
            this.identifier = nbt.getUUID("identifier");
            this.duration = nbt.getInt("duration");
            this.deferred = nbt.getCompound("original");
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.putBoolean("initialized", this.initialized);

        if (this.initialized) {
            nbt.putUUID("identifier", this.identifier);
            nbt.putInt("duration", this.duration);
            nbt.put("original", NbtUtils.writeBlockState(this.original));
        }
    }
}
