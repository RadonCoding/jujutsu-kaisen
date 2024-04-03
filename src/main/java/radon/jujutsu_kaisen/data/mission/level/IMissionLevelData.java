package radon.jujutsu_kaisen.data.mission.level;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.MissionGrade;
import radon.jujutsu_kaisen.data.mission.MissionType;

import java.util.Set;

public interface IMissionLevelData extends INBTSerializable<CompoundTag> {
    void tick();

    void register(MissionType type, MissionGrade grade, BlockPos pos);

    Set<Mission> getMissions();

    boolean isRegistered(BlockPos pos);

    Mission getMission(BlockPos pos);
}
