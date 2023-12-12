package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.UUID;

public class DomainBlockEntity extends BlockEntity {
    private boolean initialized;
    private UUID identifier;

    @Nullable
    private BlockState original;

    private CompoundTag deferred;

    @Nullable
    private CompoundTag saved;

    public DomainBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DOMAIN.get(), pPos, pBlockState);
    }

    public DomainBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DomainBlockEntity pBlockEntity) {
        if (pBlockEntity.identifier == null || !(((ServerLevel) pLevel).getEntity(pBlockEntity.identifier) instanceof DomainExpansionEntity domain) || domain.isRemoved() || !domain.isAlive()) {
            pBlockEntity.destroy();
        }
    }

    public void destroy() {
        if (this.level == null) return;

        BlockState original = this.getOriginal();

        if (original != null) {
            if (original.isAir()) {
                if (!this.getBlockState().getFluidState().isEmpty()) {
                    this.level.setBlockAndUpdate(this.getBlockPos(), Blocks.AIR.defaultBlockState());
                } else {
                    this.level.destroyBlock(this.getBlockPos(), false);
                }
            } else {
                this.level.setBlockAndUpdate(this.getBlockPos(), original);

                if (this.saved != null) {
                    BlockEntity be = this.level.getBlockEntity(this.getBlockPos());

                    if (be != null) {
                        be.load(this.saved);
                    }
                }
            }
        } else {
            if (!this.getBlockState().getFluidState().isEmpty()) {
                this.level.setBlockAndUpdate(this.getBlockPos(), Blocks.AIR.defaultBlockState());
            } else {
                this.level.destroyBlock(this.getBlockPos(), false);
            }
        }
    }

    public @Nullable UUID getIdentifier() {
        return this.identifier;
    }

    public @Nullable BlockState getOriginal() {
        if (this.level == null) return this.original;

        if (this.original == null && this.deferred != null) {
            this.original = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), this.deferred);
            this.deferred = null;
            this.setChanged();
        }
        return this.original;
    }

    public void create(UUID identifier, BlockState state, CompoundTag saved) {
        this.initialized = true;
        this.identifier = identifier;
        this.original = state;
        this.saved = saved;
        this.setChanged();
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        this.initialized = pTag.getBoolean("initialized");

        if (this.initialized) {
            this.identifier = pTag.getUUID("identifier");
            this.deferred = pTag.getCompound("original");

            if (pTag.contains("saved")) {
                this.saved = pTag.getCompound("saved");
            }
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.putBoolean("initialized", this.initialized);

        if (this.initialized) {
            pTag.putUUID("identifier", this.identifier);

            if (this.original != null) {
                pTag.put("original", NbtUtils.writeBlockState(this.original));
            } else {
                pTag.put("original", this.deferred);
            }

            if (this.saved != null) {
                pTag.put("saved", this.saved);
            }
        }
    }
}
