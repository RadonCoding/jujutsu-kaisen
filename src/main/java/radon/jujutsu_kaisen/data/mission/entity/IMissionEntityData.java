package radon.jujutsu_kaisen.data.mission.entity;


import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.mission.Mission;

public interface IMissionEntityData extends INBTSerializable<CompoundTag> {
    @Nullable Mission getMission();

    void setMission(@Nullable Mission mission);
}
