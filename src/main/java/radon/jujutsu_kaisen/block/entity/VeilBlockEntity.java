package radon.jujutsu_kaisen.block.entity;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
import radon.jujutsu_kaisen.block.base.TemporaryBlockEntity;
import radon.jujutsu_kaisen.entity.VeilEntity;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.ModifierUtils;
import radon.jujutsu_kaisen.util.VeilUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VeilBlockEntity extends TemporaryBlockEntity {
    private boolean initialized;

    @Nullable
    private UUID parentUUID;

    @Nullable
    private UUID ownerUUID;

    private int size;

    private List<Modifier> modifiers;

    private int death;

    public VeilBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.VEIL.get(), pPos, pBlockState);

        this.modifiers = new ArrayList<>();
    }

    @Nullable
    public UUID getParentUUID() {
        return this.parentUUID;
    }

    @Nullable
    public UUID getOwnerUUID() {
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

    public void create(UUID parentUUID, UUID ownerUUID, int delay, int size, List<Modifier> modifiers, BlockState state, CompoundTag saved) {
        this.initialized = true;
        this.parentUUID = parentUUID;
        this.ownerUUID = ownerUUID;
        this.death = delay;
        this.size = size;
        this.modifiers = modifiers;
        this.setOriginal(state);
        this.setSaved(saved);
        this.setChanged();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return this.saveWithoutMetadata(pRegistries);
    }
    
    private void markUpdated() {
        this.setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

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
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

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
        }

        if (this.level != null) {
            this.markUpdated();
        }
    }
}
