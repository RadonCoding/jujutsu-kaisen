package radon.jujutsu_kaisen.data.mission.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;

import javax.annotation.Nullable;

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
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();

        if (this.pos != null) {
            nbt.put("pos", NbtUtils.writeBlockPos(this.pos));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        if (nbt.contains("pos")) {
            this.pos = NbtUtils.readBlockPos(nbt, "pos").orElseThrow();
        }
    }
}
