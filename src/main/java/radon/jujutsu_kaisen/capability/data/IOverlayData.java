package radon.jujutsu_kaisen.capability.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.client.layer.overlay.Overlay;

import java.util.Set;
import java.util.UUID;

public interface IOverlayData {
    void sync(LivingEntity owner);

    void addLocalOverlay(LivingEntity owner, Overlay overlay);
    void removeLocalOverlay(LivingEntity owner, Overlay overlay);

    Set<Overlay> getLocalOverlays();

    Set<Overlay> getRemoteOverlays(UUID identifier);
    boolean isSynced(UUID identifier);

    void tick(LivingEntity owner);

    CompoundTag serializeNBT();
    void deserializeLocalNBT(CompoundTag nbt);
    void deserializeRemoteNBT(UUID identifier, CompoundTag nbt);
}
