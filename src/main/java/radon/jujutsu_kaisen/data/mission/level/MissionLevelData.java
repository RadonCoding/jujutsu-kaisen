package radon.jujutsu_kaisen.data.mission.level;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import radon.jujutsu_kaisen.block.CurseSpawnerBlock;
import radon.jujutsu_kaisen.block.entity.CurseSpawnerBlockEntity;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.MissionGrade;
import radon.jujutsu_kaisen.data.mission.MissionType;
import radon.jujutsu_kaisen.data.mission.entity.IMissionEntityData;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMissionS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMissionLevelDataS2CPacket;
import radon.jujutsu_kaisen.tags.JJKStructureTags;

import java.util.*;

public class MissionLevelData implements IMissionLevelData {
    private final Set<Mission> missions;
    private final Map<Mission, UUID> taken;

    private final Level level;

    public MissionLevelData(Level level) {
        this.level = level;

        this.missions = new LinkedHashSet<>();
        this.taken = new LinkedHashMap<>();
    }

    @Override
    public void tick() {
        if (!(this.level instanceof ServerLevel serverLevel)) return;

        Set<Mission> remove = new HashSet<>();

        for (Mission mission : new LinkedHashSet<>(this.missions)) {
            Set<BlockPos> spawns = mission.getSpawns();
            spawns.removeIf(pos -> !(this.level.getBlockEntity(pos) instanceof CurseSpawnerBlockEntity));

            if (!spawns.isEmpty()) continue;

            Set<UUID> curses = mission.getCurses();

            if (curses.isEmpty()) {
                remove.add(mission);
                continue;
            }

            Iterator<UUID> cursesIter = curses.iterator();

            while (cursesIter.hasNext()) {
                UUID identifier = cursesIter.next();

                Entity curse = serverLevel.getEntity(identifier);

                if (curse == null || curse.isRemoved() || !curse.isAlive()) {
                    cursesIter.remove();

                    PacketHandler.broadcast(new SyncMissionS2CPacket(this.level.dimension(), mission.serializeNBT()));
                }
            }
        }

        // Remove missions that need to be removed
        this.missions.removeAll(remove);

        if (!remove.isEmpty()) {
            Iterator<Map.Entry<Mission, UUID>> iter = this.taken.entrySet().iterator();

            // Find missions that were removed
            while (iter.hasNext()) {
                Map.Entry<Mission, UUID> entry = iter.next();

                if (this.missions.contains(entry.getKey())) continue;

                UUID identifier = entry.getValue();

                Entity entity = serverLevel.getEntity(identifier);

                if (entity == null) continue;

                entity.sendSystemMessage(Component.literal("Completed mission!"));

                IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) continue;

                IMissionEntityData data = cap.getMissionData();

                data.setMission(null);

                iter.remove();
            }
            PacketHandler.broadcast(new SyncMissionLevelDataS2CPacket(this.level.dimension(), this.serializeNBT()));
        }
    }

    @Override
    public void register(MissionType type, MissionGrade grade, BlockPos pos) {
        this.register(new Mission(this.level.dimension(), type, grade, pos));
    }

    @Override
    public void register(Mission mission) {
        this.missions.remove(mission);
        this.missions.add(mission);

        if (!this.level.isClientSide) {
            PacketHandler.broadcast(new SyncMissionLevelDataS2CPacket(this.level.dimension(), this.serializeNBT()));
        }
    }

    @Override
    public Set<Mission> getMissions() {
        return this.missions;
    }

    @Override
    public boolean isRegistered(BlockPos pos) {
        for (Mission mission : this.missions) {
            if (mission.getPos().equals(pos)) return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Mission getMission(BlockPos pos) {
        for (Mission mission : this.missions) {
            if (mission.getPos().equals(pos)) return mission;
        }
        return null;
    }

    @Override
    public boolean isTaken(Mission mission) {
        return this.taken.containsKey(mission);
    }

    @Override
    public void setTaken(Mission mission, UUID identifier) {
        this.taken.put(mission, identifier);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        ListTag missionsTag = new ListTag();

        for (Mission mission : this.missions) {
            missionsTag.add(mission.serializeNBT());
        }
        nbt.put("missions", missionsTag);

        ListTag takenTag = new ListTag();

        for (Map.Entry<Mission, UUID> entry : this.taken.entrySet()) {
            CompoundTag data = new CompoundTag();
            data.put("mission", entry.getKey().serializeNBT());
            data.putUUID("identifier", entry.getValue());
            takenTag.add(data);
        }
        nbt.put("taken", takenTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundTag nbt) {
        this.missions.clear();

        for (Tag tag : nbt.getList("missions", Tag.TAG_COMPOUND)) {
            this.missions.add(new Mission((CompoundTag) tag));
        }

        for (Tag tag : nbt.getList("taken", Tag.TAG_COMPOUND)) {
            CompoundTag data = (CompoundTag) tag;
            this.taken.put(new Mission(data.getCompound("mission")), data.getUUID("identifier"));
        }
    }
}
