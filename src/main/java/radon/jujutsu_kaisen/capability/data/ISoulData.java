package radon.jujutsu_kaisen.capability.data;

import net.minecraft.nbt.CompoundTag;

public interface ISoulData {
    float getDamage();
    void hurt(float amount);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
