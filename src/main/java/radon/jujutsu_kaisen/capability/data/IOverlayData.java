package radon.jujutsu_kaisen.capability.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;
import java.util.UUID;

public interface IOverlayData {
    void sync(UUID identifier);

    void addLocalOverlay(LivingEntity owner, ResourceLocation overlay);
    void removeLocalOverlay(LivingEntity owner, ResourceLocation overlay);

    Set<ResourceLocation> getLocalOverlays();

    Set<ResourceLocation> getRemoteOverlays(UUID identifier);
    boolean isSynced(UUID identifier);

    void tick(LivingEntity owner);

    CompoundTag serializeNBT();
    void deserializeLocalNBT(CompoundTag nbt);
    void deserializeRemoteNBT(UUID identifier, CompoundTag nbt);
}
