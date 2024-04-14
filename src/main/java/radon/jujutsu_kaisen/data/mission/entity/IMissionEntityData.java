package radon.jujutsu_kaisen.data.mission.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.MissionGrade;
import radon.jujutsu_kaisen.data.mission.MissionType;

import java.util.Set;

public interface IMissionEntityData extends INBTSerializable<CompoundTag> {
    void setMission(@Nullable Mission mission);

    @Nullable Mission getMission();
}
