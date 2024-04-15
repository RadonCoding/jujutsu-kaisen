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
import radon.jujutsu_kaisen.block.base.ITemporaryBlockEntity;
import radon.jujutsu_kaisen.block.base.TemporaryBlockEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

import java.util.UUID;

public class DomainBlockEntity extends TemporaryBlockEntity {
    private boolean initialized;
    private UUID identifier;

    private int death;

    public DomainBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.DOMAIN.get(), pPos, pBlockState);
    }

    public DomainBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DomainBlockEntity pBlockEntity) {
        if (pBlockEntity.identifier == null || !(((ServerLevel) pLevel).getEntity(pBlockEntity.identifier) instanceof DomainExpansionEntity domain) || domain.isRemoved() || !domain.isAlive()) {
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
    public void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.putBoolean("initialized", this.initialized);

        if (this.initialized) {
            pTag.putUUID("identifier", this.identifier);
            pTag.putInt("death", this.death);
        }
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        this.initialized = pTag.getBoolean("initialized");

        if (this.initialized) {
            this.identifier = pTag.getUUID("identifier");
            this.death = pTag.getInt("death");
        }
    }
}
