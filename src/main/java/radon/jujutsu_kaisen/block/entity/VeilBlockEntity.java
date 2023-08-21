package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.item.veil.EntityBlacklistModifier;
import radon.jujutsu_kaisen.item.veil.Modifier;
import radon.jujutsu_kaisen.item.veil.ModifierUtils;
import radon.jujutsu_kaisen.item.veil.PlayerBlacklistModifier;

import javax.annotation.Nullable;
import java.util.List;

public class VeilBlockEntity extends BlockEntity {
    private int counter;

    private List<Modifier> modifiers;

    public VeilBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.VEIL.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, VeilBlockEntity pBlockEntity) {
        if (++pBlockEntity.counter == VeilRodBlockEntity.INTERVAL * 2) {
            pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
        }
    }

    public void setModifiers(@NotNull List<Modifier> modifiers) {
        this.modifiers = modifiers;
        this.sendUpdates();
    }

    public boolean isBlacklisted(Entity entity) {
        if (entity == null || this.modifiers == null) return false;

        if (entity instanceof Player player) {
            for (Modifier modifier : this.modifiers) {
                if (modifier.getType() != Modifier.Type.PLAYER_BLACKLIST) continue;
                if (((PlayerBlacklistModifier) modifier).getName().equals(player.getDisplayName().getString())) {
                    return true;
                }
            }
        } else {
            Registry<EntityType<?>> registry = entity.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            ResourceLocation key = registry.getKey(entity.getType());

            if (key == null) return false;

            for (Modifier modifier : this.modifiers) {
                if (modifier.getType() != Modifier.Type.ENTITY_BLACKLIST) continue;
                if (key.equals(((EntityBlacklistModifier) modifier).getKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void reset() {
        this.counter = 0;
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

        if (this.modifiers != null) {
            pTag.put("modifiers", ModifierUtils.serialize(this.modifiers));
        }
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        if (pTag.contains("modifiers")) {
            this.modifiers = ModifierUtils.deserialize(pTag.getList("modifiers", Tag.TAG_COMPOUND));
        }
    }
}
