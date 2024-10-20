package radon.jujutsu_kaisen.data.mission.level;


import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.MissionGrade;
import radon.jujutsu_kaisen.data.mission.MissionType;
import radon.jujutsu_kaisen.data.mission.entity.IMissionEntityData;
import radon.jujutsu_kaisen.network.packet.s2c.RemoveMissionCurseS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMissionLevelDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.AddMissionCurseS2CPacket;

import java.util.*;

public class MissionLevelData implements IMissionLevelData {
    private final LinkedHashSet<Mission> missions;
    private final LinkedHashMap<Mission, UUID> taken;

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
            if (!mission.isInitialized()) continue;

            Set<BlockPos> spawns = mission.getSpawns();

            if (!spawns.isEmpty()) continue;

            Set<UUID> curses = mission.getCurses();

            if (curses.isEmpty()) {
                remove.add(mission);
                continue;
            }

            Iterator<UUID> iter = curses.iterator();

            while (iter.hasNext()) {
                UUID identifier = iter.next();

                Entity curse = serverLevel.getEntity(identifier);

                if (curse == null || curse.isRemoved() || !curse.isAlive()) {
                    iter.remove();

                    PacketDistributor.sendToAllPlayers(new RemoveMissionCurseS2CPacket(this.level.dimension(),
                            mission.getPos(), identifier));
                }
            }
        }

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
            PacketDistributor.sendToAllPlayers(new SyncMissionLevelDataS2CPacket(this.level.dimension(),
                    this.serializeNBT(this.level.registryAccess())));
        }

        // Remove missions that need to be removed
        this.missions.removeAll(remove);
    }

    @Override
    public void register(MissionType type, MissionGrade grade, BlockPos pos) {
        this.missions.add(new Mission(this.level.dimension(), type, grade, pos));

        if (!this.level.isClientSide) {
            PacketDistributor.sendToAllPlayers(new SyncMissionLevelDataS2CPacket(this.level.dimension(),
                    this.serializeNBT(this.level.registryAccess())));
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
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
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
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
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
