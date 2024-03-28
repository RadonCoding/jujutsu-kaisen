package radon.jujutsu_kaisen.data.mission;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import java.util.List;
import java.util.Set;

public interface IMissionData extends INBTSerializable<CompoundTag> {
    void tick();

    void register(MissionType type, MissionGrade grade, BlockPos pos);

    void register(BlockPos pos);

    Set<Mission> getMissions();

    boolean isRegistered(BlockPos pos);

    Mission getMission(BlockPos pos);
}
