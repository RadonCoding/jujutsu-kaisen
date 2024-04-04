package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.VeilEntity;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.ModifierUtils;
import radon.jujutsu_kaisen.util.VeilUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VeilBlockEntity extends BlockEntity {
    private boolean initialized;

    @Nullable
    private UUID parentUUID;

    @Nullable
    private UUID ownerUUID;

    private int size;

    private List<Modifier> modifiers;

    @Nullable
    private BlockState original;

    private CompoundTag deferred;

    @Nullable
    private CompoundTag saved;

    private int death;

    public VeilBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.VEIL.get(), pPos, pBlockState);

        this.modifiers = new ArrayList<>();
    }

    public @Nullable UUID getParentUUID() {
        return this.parentUUID;
    }

    public @Nullable UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public boolean isAllowed(Entity entity) {
        return VeilUtil.isAllowed(entity, this.ownerUUID, this.modifiers);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, VeilBlockEntity pBlockEntity) {
        if (pBlockEntity.parentUUID == null || !(((ServerLevel) pLevel).getEntity(pBlockEntity.parentUUID) instanceof VeilEntity veil) || veil.isRemoved() || !veil.isAlive()) {
            --pBlockEntity.death;
        }

        if (pBlockEntity.death <= 0) {
            pBlockEntity.destroy();
        }
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
                        be.load(this.saved);
                    }
                }
            }
        } else {
            this.level.setBlockAndUpdate(this.getBlockPos(), Blocks.AIR.defaultBlockState());
        }
    }

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

    public void create(UUID parentUUID, UUID ownerUUID, int delay, int size, List<Modifier> modifiers, BlockState original, CompoundTag saved) {
        this.initialized = true;
        this.parentUUID = parentUUID;
        this.ownerUUID = ownerUUID;
        this.death = delay;
        this.size = size;
        this.modifiers = modifiers;
        this.original = original;
        this.saved = saved;
        this.setChanged();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    private void markUpdated() {
        this.setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.putBoolean("initialized", this.initialized);

        if (this.initialized) {
            pTag.putInt("death", this.death);

            if (this.parentUUID != null) {
                pTag.putUUID("parent", this.parentUUID);
            }
            if (this.ownerUUID != null) {
                pTag.putUUID("owner", this.ownerUUID);
            }
            pTag.putInt("size", this.size);
            pTag.put("modifiers", ModifierUtils.serialize(this.modifiers));

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

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        this.initialized = pTag.getBoolean("initialized");

        if (this.initialized) {
            this.death = pTag.getInt("death");

            if (pTag.contains("parent")) {
                this.parentUUID = pTag.getUUID("parent");
            }
            if (pTag.contains("owner")) {
                this.ownerUUID = pTag.getUUID("owner");
            }
            this.size = pTag.getInt("size");
            this.modifiers = ModifierUtils.deserialize(pTag.getList("modifiers", Tag.TAG_COMPOUND));
            this.deferred = pTag.getCompound("original");

            if (pTag.contains("saved")) {
                this.saved = pTag.getCompound("saved");
            }
        }

        if (this.level != null) {
            this.markUpdated();
        }
    }
}
