package radon.jujutsu_kaisen.data.mission.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.UnknownNullability;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMissionEntityDataS2CPacket;
import radon.jujutsu_kaisen.tags.JJKStructureTags;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MissionEntityData implements IMissionEntityData {
    @Nullable
    private BlockPos pos;

    private final LivingEntity owner;

    public MissionEntityData(LivingEntity owner) {
        this.owner = owner;
    }

    @Override
    public @Nullable Mission getMission() {
        if (this.pos == null) return null;

        IMissionLevelData data = this.owner.level().getData(JJKAttachmentTypes.MISSION_LEVEL);
        return data.getMission(this.pos);
    }

    @Override
    public void setMission(@Nullable Mission mission) {
        this.pos = mission == null ? null : mission.getPos();
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        if (this.pos != null) {
            nbt.put("pos", NbtUtils.writeBlockPos(this.pos));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("pos")) {
            this.pos = NbtUtils.readBlockPos(nbt.getCompound("pos"));
        }
    }
}
