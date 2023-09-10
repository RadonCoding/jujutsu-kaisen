package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.VeilBlock;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.item.veil.ColorModifier;
import radon.jujutsu_kaisen.item.veil.Modifier;
import radon.jujutsu_kaisen.item.veil.ModifierUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class VeilRodBlockEntity extends BlockEntity {
    public static final int RANGE = 128;
    public static final int INTERVAL = 5;

    private int counter;
    public int frequency;

    @Nullable
    public List<Modifier> modifiers;
    @Nullable
    public UUID ownerUUID;

    public VeilRodBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.VEIL_ROD.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, VeilRodBlockEntity pBlockEntity) {
        if (++pBlockEntity.counter != INTERVAL) return;

        pBlockEntity.counter = 0;

        BlockPos.MutableBlockPos current = new BlockPos.MutableBlockPos();
        current.set(pPos);

        List<BlockPos> nodes = new ArrayList<>();

        boolean success = true;

        Set<Modifier> modifiers = new HashSet<>();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!success) break;

            success = false;

            for (int i = 1; i < RANGE; i++) {
                BlockPos relative = current.relative(direction, i);

                if (pLevel.getBlockEntity(relative) instanceof VeilRodBlockEntity be && be.frequency == pBlockEntity.frequency) {
                    if (be.modifiers != null) {
                        modifiers.addAll(be.modifiers);
                    }
                    nodes.add(relative);
                    current.set(relative);
                    success = true;
                    break;
                }
            }
        }

        if (nodes.size() == 4) {
            BlockState state = JJKBlocks.VEIL.get().defaultBlockState();

            for (Modifier modifier : modifiers) {
                if (modifier.getType() == Modifier.Type.COLOR) {
                    state = state.setValue(VeilBlock.COLOR, ((ColorModifier) modifier).getColor());
                } else if (modifier.getType() == Modifier.Type.TRANSPARENT) {
                    state = state.setValue(VeilBlock.TRANSPARENT, true);
                }
            }

            int xWidth = nodes.get(2).getX() - nodes.get(0).getX();
            int zWidth = nodes.get(3).getZ() - nodes.get(0).getZ();
            int yWidth = (xWidth + zWidth) / 2;

            BlockPos corner1 = nodes.get(0).west().north().below();
            BlockPos corner2 = nodes.get(2).above(yWidth).east().south();
            Stream<BlockPos> box = BlockPos.betweenClosedStream(corner1, corner2);

            BlockState block = state;

            box.forEach(pos -> {
                if (pos.getX() == corner1.getX() || pos.getX() == corner2.getX() ||
                        pos.getZ() == corner1.getZ() || pos.getZ() == corner2.getZ() ||
                        pos.getY() == corner1.getY() || pos.getY() == corner2.getY()) {
                    BlockState original = pLevel.getBlockState(pos);

                    for (DomainExpansionEntity domain : pLevel.getEntitiesOfClass(DomainExpansionEntity.class, AABB.ofSize(pos.getCenter(),
                            64.0D, 64.0D, 64.0D))) {
                        if (domain.isInsideBarrier(null, pos)) return;
                    }

                    if (original.isAir() || !original.canOcclude()) {
                        pLevel.setBlockAndUpdate(pos, block);

                        if (pLevel.getBlockEntity(pos) instanceof VeilBlockEntity be) {
                            be.setParent(pPos);
                        }
                    }
                }
            });
        }
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
        this.setChanged();
    }

    public void setOwner(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
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

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        if (this.ownerUUID != null) {
            pTag.putUUID("owner", this.ownerUUID);
        }
        pTag.putInt("counter", this.counter);
        pTag.putInt("frequency", this.frequency);

        if (this.modifiers != null) {
            pTag.put("modifiers", ModifierUtils.serialize(this.modifiers));
        }
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        if (pTag.contains("owner")) {
            this.ownerUUID = pTag.getUUID("owner");
        }
        this.counter = pTag.getInt("counter");
        this.frequency = pTag.getInt("frequency");

        if (pTag.contains("modifiers")) {
            this.modifiers = ModifierUtils.deserialize(pTag.getList("modifiers", Tag.TAG_COMPOUND));
        }
    }
}
