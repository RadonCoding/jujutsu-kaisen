package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DomainBlockEntity extends BlockEntity {
    private boolean initialized;
    @Nullable
    private UUID identifier;
    @Nullable
    private BlockState original;

    private CompoundTag deferred;

    public DomainBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DOMAIN.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DomainBlockEntity pBlockEntity) {
        if (pBlockEntity.identifier == null) return;

        Entity domain = ((ServerLevel) pLevel).getEntity(pBlockEntity.identifier);

        if (domain == null || domain.isRemoved() || !domain.isAlive()) {
            BlockState original = pBlockEntity.getOriginal();

            if (original != null) {
                if (original.isAir()) {
                    pLevel.destroyBlock(pPos, false);

                    if (!pBlockEntity.getBlockState().getFluidState().isEmpty() && original.getFluidState().isEmpty()) {
                        pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
                    }
                } else {
                    pLevel.setBlockAndUpdate(pPos, original);
                }
            }
        }
    }

    @Nullable
    public UUID getIdentifier() {
        return this.identifier;
    }

    public @Nullable BlockState getOriginal() {
        if (this.original == null && this.deferred != null) {
            this.original = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), this.deferred);
            this.deferred = null;
            this.setChanged();
        }
        return this.original;
    }

    public void create(UUID identifier, BlockState state) {
        this.initialized = true;
        this.identifier = identifier;
        this.original = state;
        this.setChanged();
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        this.initialized = pTag.getBoolean("initialized");

        if (this.initialized) {
            this.identifier = pTag.getUUID("identifier");
            this.deferred = pTag.getCompound("original");
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.putBoolean("initialized", this.initialized);

        if (this.initialized) {
            assert this.identifier != null;

            pTag.putUUID("identifier", this.identifier);

            if (this.original != null) {
                pTag.put("original", NbtUtils.writeBlockState(this.original));
            } else {
                pTag.put("original", this.deferred);
            }
        }
    }
}
