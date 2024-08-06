package radon.jujutsu_kaisen.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.base.TemporaryBlockEntity;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;

import java.util.UUID;

public class DomainBlockEntity extends TemporaryBlockEntity {
    private boolean initialized;
    protected UUID identifier;

    private int death;

    public DomainBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DOMAIN.get(), pPos, pBlockState);
    }

    public DomainBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DomainBlockEntity pBlockEntity) {
        if (pBlockEntity.identifier == null || !(((ServerLevel) pLevel).getEntity(pBlockEntity.identifier) instanceof DomainExpansionEntity domain) ||
                domain.isRemoved()) {
            pBlockEntity.death--;
        }

        if (pBlockEntity.death <= 0) {
            pBlockEntity.destroy();
        }
    }

    @Nullable
    public UUID getIdentifier() {
        return this.identifier;
    }

    public void create(UUID identifier, int delay, BlockState state, CompoundTag saved) {
        this.initialized = true;
        this.identifier = identifier;
        this.death = delay;
        this.setOriginal(state);
        this.setSaved(saved);
        this.setChanged();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        pTag.putBoolean("initialized", this.initialized);

        if (this.initialized) {
            pTag.putUUID("identifier", this.identifier);
            pTag.putInt("death", this.death);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        this.initialized = pTag.getBoolean("initialized");

        if (this.initialized) {
            this.identifier = pTag.getUUID("identifier");
            this.death = pTag.getInt("death");
        }
    }
}