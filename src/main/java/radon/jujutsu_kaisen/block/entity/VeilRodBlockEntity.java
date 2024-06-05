package radon.jujutsu_kaisen.block.entity;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.ModifierUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VeilRodBlockEntity extends BlockEntity {
    @Nullable
    private UUID ownerUUID;

    private int radius;

    private List<Modifier> modifiers;

    public VeilRodBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.VEIL_ROD.get(), pPos, pBlockState);

        this.radius = ConfigHolder.SERVER.minimumVeilSize.get();
        this.modifiers = new ArrayList<>();
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        this.setChanged();
    }

    public List<Modifier> getModifiers() {
        return this.modifiers;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        this.setChanged();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        if (this.ownerUUID != null) {
            pTag.putUUID("owner", this.ownerUUID);
        }
        pTag.putInt("size", this.radius);

        if (this.modifiers != null) {
            pTag.put("modifiers", ModifierUtils.serialize(this.modifiers));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        if (pTag.contains("owner")) {
            this.ownerUUID = pTag.getUUID("owner");
        }
        this.radius = pTag.getInt("size");

        if (pTag.contains("modifiers")) {
            this.modifiers = ModifierUtils.deserialize(pTag.getList("modifiers", Tag.TAG_COMPOUND));
        }
    }
}
