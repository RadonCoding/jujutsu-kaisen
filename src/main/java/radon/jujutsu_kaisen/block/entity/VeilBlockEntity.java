package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.PlayerModifier;

import javax.annotation.Nullable;

public class VeilBlockEntity extends BlockEntity {
    private int counter;

    @Nullable
    private BlockPos parent;

    private boolean initialized;

    @Nullable
    private BlockState original;

    private CompoundTag deferred;

    private int size;

    public VeilBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.VEIL.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, VeilBlockEntity pBlockEntity) {
        if (++pBlockEntity.counter != VeilRodBlockEntity.INTERVAL * 2) return;

        pBlockEntity.counter = 0;

        if (pBlockEntity.parent == null || !(pLevel.getBlockEntity(pBlockEntity.parent) instanceof VeilRodBlockEntity be) || be.getSize() != pBlockEntity.size) {
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
            }
        } else {
            if (!this.getBlockState().getFluidState().isEmpty()) {
                this.level.setBlockAndUpdate(this.getBlockPos(), Blocks.AIR.defaultBlockState());
            } else {
                this.level.destroyBlock(this.getBlockPos(), false);
            }
        }
    }

    public static boolean isWhitelisted(@Nullable BlockPos parent, Entity entity) {
        if (parent == null || !(entity.level().getBlockEntity(parent) instanceof VeilRodBlockEntity be)) return false;
        if (entity.getUUID().equals(be.ownerUUID)) return true;
        if (be.modifiers == null) return false;

        if (entity instanceof Player player) {
            for (Modifier modifier : be.modifiers) {
                if (modifier.getAction() != Modifier.Action.ALLOW || modifier.getType() != Modifier.Type.PLAYER)
                    continue;
                if (((PlayerModifier) modifier).getName().equals(player.getDisplayName().getString())) {
                    return true;
                }
            }
        }

        for (Modifier modifier : be.modifiers) {
            if (modifier.getAction() == Modifier.Action.ALLOW && (modifier.getType() == Modifier.Type.CURSE || modifier.getType() == Modifier.Type.SORCERER)) {
                if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
                ISorcererData cap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                return cap.getType() == JujutsuType.CURSE && modifier.getType() == Modifier.Type.CURSE ||
                        cap.getType() != JujutsuType.CURSE && modifier.getType() == Modifier.Type.SORCERER;
            }
        }
        return false;
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

    public void create(BlockPos parent, int size, BlockState original) {
        this.parent = parent;
        this.size = size;
        this.original = original;
        this.sendUpdates();
    }

    public @Nullable BlockPos getParent() {
        return this.parent;
    }

    public static boolean isAllowed(BlockPos pos, Entity entity) {
        return isWhitelisted(pos, entity);
    }

    public void sendUpdates() {
        if (this.level != null) {
            this.level.setBlocksDirty(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition));
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 3);
            this.level.updateNeighborsAt(this.worldPosition, this.level.getBlockState(this.worldPosition).getBlock());
            this.setChanged();
        }
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.putBoolean("initialized", this.initialized);

        if (this.initialized) {
            if (this.original != null) {
                pTag.put("original", NbtUtils.writeBlockState(this.original));
            } else {
                pTag.put("original", this.deferred);
            }
        }

        if (this.parent != null) {
            pTag.put("parent", NbtUtils.writeBlockPos(this.parent));
        }
        pTag.putInt("size", this.size);
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        this.initialized = pTag.getBoolean("initialized");

        if (this.initialized) {
            this.deferred = pTag.getCompound("original");
        }

        if (pTag.contains("parent")) {
            this.parent = NbtUtils.readBlockPos(pTag.getCompound("parent"));
        }
        this.size = pTag.getInt("size");
    }
}
