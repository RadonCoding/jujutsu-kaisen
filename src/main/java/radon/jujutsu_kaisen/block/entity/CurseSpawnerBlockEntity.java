package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.OpenMissionScreenS2CPacket;

import java.util.*;

public class CurseSpawnerBlockEntity extends BlockEntity {
    private BlockPos pos;

    public CurseSpawnerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(JJKBlockEntities.CURSE_SPAWNER.get(), pPos, pBlockState);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
        this.setChanged();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);

        pTag.put("pos", NbtUtils.writeBlockPos(this.pos));
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);

        this.pos = NbtUtils.readBlockPos(pTag.getCompound("pos"));
    }
}
