package radon.jujutsu_kaisen.data.mission.level;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.MissionGrade;
import radon.jujutsu_kaisen.data.mission.MissionType;

import java.util.Set;
import java.util.UUID;

public interface IMissionLevelData extends INBTSerializable<CompoundTag> {
    void tick();

    void register(MissionType type, MissionGrade grade, BlockPos pos);

    void register(Mission mission);

    Set<Mission> getMissions();

    boolean isRegistered(BlockPos pos);

    @Nullable
    Mission getMission(BlockPos pos);

    boolean isTaken(Mission mission);

    void setTaken(Mission mission, UUID identifier);
}
