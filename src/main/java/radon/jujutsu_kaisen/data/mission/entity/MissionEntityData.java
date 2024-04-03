package radon.jujutsu_kaisen.data.mission.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.UnknownNullability;
import radon.jujutsu_kaisen.data.mission.Mission;

import javax.annotation.Nullable;

public class MissionEntityData implements IMissionEntityData {
    @Nullable
    private Mission mission;

    private final LivingEntity owner;

    public MissionEntityData(LivingEntity owner) {
        this.owner = owner;
    }

    @Override
    public void setMission(@Nullable Mission mission) {
        this.mission = mission;
    }

    @Override
    public @Nullable Mission getMission() {
        return this.mission;
    }

    @Override
    public void tick() {

    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        if (this.mission != null) {
            nbt.put("mission", this.mission.serializeNBT());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("mission")) {
            this.mission = new Mission(nbt.getCompound("mission"));
        }
    }
}
