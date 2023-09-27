package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
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
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.item.veil.EntityModifier;
import radon.jujutsu_kaisen.item.veil.Modifier;
import radon.jujutsu_kaisen.item.veil.PlayerModifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class VeilBlockEntity extends BlockEntity {
    private int counter;

    private BlockPos parent;

    public VeilBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.VEIL.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, VeilBlockEntity pBlockEntity) {
        if (++pBlockEntity.counter != VeilRodBlockEntity.INTERVAL * 2) return;

        pBlockEntity.counter = 0;

        List<BlockPos> nodes = new ArrayList<>();

        if (pLevel.getBlockEntity(pBlockEntity.parent) instanceof VeilRodBlockEntity parent) {
            BlockPos.MutableBlockPos current = new BlockPos.MutableBlockPos();
            current.set(pBlockEntity.parent);

            boolean success = true;

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (!success) break;

                success = false;

                for (int i = 1; i < VeilRodBlockEntity.RANGE; i++) {
                    BlockPos relative = current.relative(direction, i);

                    if (pLevel.getBlockEntity(relative) instanceof VeilRodBlockEntity be && be.frequency == parent.frequency) {
                        nodes.add(relative);
                        current.set(relative);
                        success = true;
                        break;
                    }
                }
            }
        }
        if (nodes.size() != 4) {
            pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
        }
    }

    public void setParent(BlockPos parent) {
        this.parent = parent;
        this.sendUpdates();
    }

    public boolean isBlacklisted(Entity entity) {
        if (!(entity.level().getBlockEntity(this.parent) instanceof VeilRodBlockEntity be)) return false;

        if (entity.getUUID().equals(be.ownerUUID) || be.modifiers == null) return false;

        if (entity instanceof Player player) {
            for (Modifier modifier : be.modifiers) {
                if (modifier.getAction() != Modifier.Action.DENY || modifier.getType() != Modifier.Type.PLAYER)
                    continue;
                if (((PlayerModifier) modifier).getName().equals(player.getDisplayName().getString())) {
                    return true;
                }
            }
        } else {
            Registry<EntityType<?>> registry = entity.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            ResourceLocation key = registry.getKey(entity.getType());

            if (key != null) {
                for (Modifier modifier : be.modifiers) {
                    if (modifier.getAction() != Modifier.Action.DENY || modifier.getType() != Modifier.Type.ENTITY)
                        continue;
                    if (key.equals(((EntityModifier) modifier).getKey())) return true;
                }
            }
        }

        for (Modifier modifier : be.modifiers) {
            if (modifier.getAction() == Modifier.Action.DENY && (modifier.getType() == Modifier.Type.CURSE || modifier.getType() == Modifier.Type.SORCERER)) {
                if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
                ISorcererData cap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                return cap.getType() == JujutsuType.CURSE && modifier.getType() == Modifier.Type.CURSE ||
                        cap.getType() != JujutsuType.CURSE && modifier.getType() == Modifier.Type.SORCERER;
            }
        }
        return false;
    }

    public boolean isWhitelisted(Entity entity) {
        if (!(entity.level().getBlockEntity(this.parent) instanceof VeilRodBlockEntity be)) return false;
        if (entity.getUUID().equals(be.ownerUUID) || be.modifiers == null) return false;

        if (entity instanceof Player player) {
            for (Modifier modifier : be.modifiers) {
                if (modifier.getAction() != Modifier.Action.ALLOW || modifier.getType() != Modifier.Type.PLAYER) continue;
                if (((PlayerModifier) modifier).getName().equals(player.getDisplayName().getString())) {
                    return true;
                }
            }
        } else {
            Registry<EntityType<?>> registry = entity.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            ResourceLocation key = registry.getKey(entity.getType());

            if (key != null) {
                for (Modifier modifier : be.modifiers) {
                    if (modifier.getAction() != Modifier.Action.ALLOW || modifier.getType() != Modifier.Type.ENTITY) continue;
                    if (key.equals(((EntityModifier) modifier).getKey())) return true;
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

    public boolean isAllowed(Entity entity) {
        return this.isWhitelisted(entity) || !this.isBlacklisted(entity);
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

        pTag.put("parent", NbtUtils.writeBlockPos(this.parent));
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        this.parent = NbtUtils.readBlockPos(pTag.getCompound("parent"));
    }
}
