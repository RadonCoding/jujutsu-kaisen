package radon.jujutsu_kaisen.client.layer.overlay;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public abstract class Overlay {
    public abstract void init(LivingEntity owner);
    public abstract CompoundTag addCustomData();
    public abstract void readCustomData(CompoundTag nbt);
}
